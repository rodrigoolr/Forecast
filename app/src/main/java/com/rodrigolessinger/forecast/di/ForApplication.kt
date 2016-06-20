package com.rodrigolessinger.forecast.di

import javax.inject.Qualifier

import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class ForApplication
