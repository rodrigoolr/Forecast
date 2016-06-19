package com.rodrigolessinger.forecast

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class ActivityModule(
        private val activity: Activity
) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return activity
    }

    @Provides
    @Singleton
    fun provideResources(): Resources {
        return activity.resources
    }

}