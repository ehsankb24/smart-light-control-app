package com.sepiddynamics.samrt_light_control_app.data

import com.sepiddynamics.samrt_light_control_app.domain.BulbState
import com.sepiddynamics.samrt_light_control_app.domain.CommunicationRepository
import com.sepiddynamics.samrt_light_control_app.domain.ConnectionState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.Charset

class CommunicationRepositoryImpl(
    private val mqttClient: MqttClient,
    private val json: Json,
    private val clientId: String
) : CommunicationRepository {
    override fun startConnection(): Flow<ConnectionState> = callbackFlow {
        try {
            mqttClient.setCallback(
                object : MqttCallback {
                    override fun connectionLost(cause: Throwable?) {
                        // Handle connection lost and try to reconnect
                        cause?.printStackTrace()
                        trySend(ConnectionState.Disconnected(cause?.message.orEmpty()))
                    }

                    @Throws(Exception::class)
                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        // Handle received message
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        // Handle delivery completion
                    }
                }
            )
            val options = MqttConnectOptions()
            options.isCleanSession = true
            mqttClient.connect(options)
            trySend(ConnectionState.Connected)
        } catch (e: MqttException) {
            e.printStackTrace()
            trySend(ConnectionState.ErrorOccurred(e.message.toString()))
        }
        awaitClose {
            mqttClient.close(true)
        }
    }

    override fun stateSubscribe(): Flow<BulbState> = callbackFlow {
        mqttClient.subscribeWithResponse("bulbState/$clientId") { _, message ->
            val state = parseMessage(String(message.payload, Charset.defaultCharset()))
            trySend(
                if (state.isOn) {
                    BulbState.On(state.brightness)
                } else {
                    BulbState.Off
                }
            )
        }
        awaitClose { }
    }

    private fun parseMessage(payload: String): BulbStateResponse {
        return json.decodeFromString<BulbStateResponse>(payload)
    }

    override suspend fun sendCommand(state: BulbState) {
        val command = when (state) {
            BulbState.Off -> BulbStateResponse(false, 10f)
            is BulbState.On -> BulbStateResponse(true, state.brightness)
        }
        val payload = json.encodeToString(command)
        mqttClient.publish(
            "bulbOrder/$clientId",
            MqttMessage().apply {
                this.payload = payload.toByteArray()
            }
        )
    }
}