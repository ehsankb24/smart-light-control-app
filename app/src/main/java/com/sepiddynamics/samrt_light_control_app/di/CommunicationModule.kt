package com.sepiddynamics.samrt_light_control_app.di

import com.sepiddynamics.samrt_light_control_app.data.CommunicationRepositoryImpl
import com.sepiddynamics.samrt_light_control_app.domain.CommunicationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import org.eclipse.paho.client.mqttv3.MqttClient

@InstallIn(SingletonComponent::class)
@Module
class CommunicationModule {

    @Provides
    fun provideJsonParser(): Json = Json.Default

    @Provides
    fun provideCommunicationRepository(
        mqttClient: MqttClient,
        json: Json,
        @ClientId clientId: String
    ): CommunicationRepository = CommunicationRepositoryImpl(mqttClient, json, clientId)
}