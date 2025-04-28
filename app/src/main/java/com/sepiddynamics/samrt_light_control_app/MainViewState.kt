package com.sepiddynamics.samrt_light_control_app

sealed interface MainViewState {
    data object Loading : MainViewState
    data class Failed(val retry: () -> Unit) : MainViewState
    data class Connected(
        val isOn: Boolean,
        val progressValue: Float,
        val onCheckedChange: (Boolean) -> Unit,
        val onValueChange: (Float) -> Unit,
        val onValueChangeFinished: () -> Unit
    ) : MainViewState {
        val progressLabel: String = progressValue.toString()
        val stateTitle: String = if (isOn) {
            "on"
        } else {
            "off"
        }
    }
}