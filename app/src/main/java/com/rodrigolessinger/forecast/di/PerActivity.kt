package com.rodrigolessinger.forecast.di

import javax.inject.Scope

import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * Scope used to attach an object lifetime to the current Activity
 */
@Scope
@Retention(RUNTIME)
annotation class PerActivity
