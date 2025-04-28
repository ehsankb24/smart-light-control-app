package com.sepiddynamics.samrt_light_control_app.di

import android.content.Context
import com.sepiddynamics.samrt_light_control_app.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

@InstallIn(SingletonComponent::class)
@Module
class RemoteDataSourceModule {

    @ClientId
    @Provides
    fun provideClientId(
        @ApplicationContext context: Context
    ): String = context.getString(R.string.bulb_id)

    @ServerUrl
    @Provides
    fun provideServerAddress(
        @ApplicationContext context: Context
    ): String = context.getString(R.string.server_address)

    @Provides
    fun provideMqttClient(
        @ServerUrl serverUrl: String
    ) = MqttClient(
        serverUrl,
        "android app",
        MemoryPersistence()
    )
}