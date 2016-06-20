package com.rodrigolessinger.forecast.di

import javax.inject.Qualifier

import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * Qualifier to provide activity-related dependencies (For example a context or the resources object)
 */
@Qualifier
@Retention(RUNTIME)
annotation class ForActivity
