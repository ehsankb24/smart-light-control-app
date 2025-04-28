package com.sepiddynamics.samrt_light_control_app.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ClientId

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ServerUrl