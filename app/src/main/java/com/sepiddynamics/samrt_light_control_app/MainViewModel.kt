package com.sepiddynamics.samrt_light_control_app

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _viewState = MutableStateFlow<MainViewState>(MainViewState.Loading)
    val viewState = _viewState.asStateFlow()
}