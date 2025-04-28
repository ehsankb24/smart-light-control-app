package com.sepiddynamics.samrt_light_control_app.domain

import kotlinx.coroutines.flow.Flow

interface CommunicationRepository {
    fun startConnection(): Flow<ConnectionState>
    fun stateSubscribe(): Flow<BulbState>
    suspend fun sendCommand(state: BulbState)
}