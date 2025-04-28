package com.sepiddynamics.samrt_light_control_app.data

import kotlinx.serialization.Serializable

@Serializable
data class BulbStateResponse(
    val isOn: Boolean,
    val brightness: Float
)