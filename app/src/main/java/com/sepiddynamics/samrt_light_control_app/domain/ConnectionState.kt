package com.sepiddynamics.samrt_light_control_app.domain

sealed interface ConnectionState {
    data object Connected : ConnectionState
    data class ErrorOccurred(val message: String) : ConnectionState
    data class Disconnected(val reasonString: String) : ConnectionState
}