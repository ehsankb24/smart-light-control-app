package com.sepiddynamics.samrt_light_control_app.domain

sealed interface BulbState {
    data object Off : BulbState
    data class On(val brightness: Float) : BulbState
}