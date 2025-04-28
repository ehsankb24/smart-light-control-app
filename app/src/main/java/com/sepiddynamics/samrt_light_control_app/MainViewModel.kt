package com.sepiddynamics.samrt_light_control_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sepiddynamics.samrt_light_control_app.domain.BulbState
import com.sepiddynamics.samrt_light_control_app.domain.CommunicationRepository
import com.sepiddynamics.samrt_light_control_app.domain.ConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CommunicationRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow<MainViewState>(MainViewState.Loading)
    val viewState = _viewState.asStateFlow()

    init {
        startConnection()
    }

    private fun startConnection() {
        viewModelScope.launch {
            repository.startConnection().collect {
                _viewState.emit(
                    when (it) {
                        ConnectionState.Connected -> {
                            subscribeOnState()
                            MainViewState.Connected(
                                isOn = true,
                                progressValue = 20f,
                                onCheckedChange = ::onCheckedChange,
                                onValueChange = ::onValueChange,
                                onValueChangeFinished = ::onValueChangeFinished
                            )
                        }

                        is ConnectionState.Disconnected -> MainViewState.Failed(::onRetry)
                        is ConnectionState.ErrorOccurred -> MainViewState.Failed(::onRetry)
                    }
                )
            }
        }
    }

    private fun onRetry() {
        _viewState.update { MainViewState.Loading }
        startConnection()
    }

    private fun subscribeOnState() {
        viewModelScope.launch {
            repository.stateSubscribe().collect { state ->
                _viewState.update {
                    val vs = (it as MainViewState.Connected)
                    when (state) {
                        BulbState.Off -> vs.copy(
                            isOn = false
                        )

                        is BulbState.On -> vs.copy(
                            isOn = true,
                            progressValue = state.brightness
                        )
                    }
                }
            }
        }
    }

    private fun onCheckedChange(isOn: Boolean) {
        val vs = (_viewState.value as MainViewState.Connected)
        _viewState.update { vs.copy(isOn = isOn) }
        viewModelScope.launch {
            repository.sendCommand(
                if (isOn) {
                    BulbState.On(vs.progressValue)
                } else {
                    BulbState.Off
                }
            )
        }
    }

    private fun onValueChange(brightness: Float) {
        _viewState.update { (it as MainViewState.Connected).copy(progressValue = brightness) }
    }

    private fun onValueChangeFinished() {
        viewModelScope.launch {
            repository.sendCommand(
                BulbState.On((_viewState.value as MainViewState.Connected).progressValue)
            )
        }
    }
}