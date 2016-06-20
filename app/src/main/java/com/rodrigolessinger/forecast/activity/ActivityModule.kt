package com.rodrigolessinger.forecast.activity

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import com.rodrigolessinger.forecast.di.ForActivity
import com.rodrigolessinger.forecast.di.PerActivity
import dagger.Module
import dagger.Provides

/**
 * Module that provides activity dependencies that cannot be initialized normally
 */
@Module
open class ActivityModule(
        private val activity: Activity
) {

    @Provides
    @PerActivity
    internal fun provideActivity(): Activity {
        return activity
    }

    @Provides
    @PerActivity
    @ForActivity
    fun provideContext(): Context {
        return activity
    }

    @Provides
    @PerActivity
    @ForActivity
    fun provideResources(): Resources {
        return activity.resources
    }

}