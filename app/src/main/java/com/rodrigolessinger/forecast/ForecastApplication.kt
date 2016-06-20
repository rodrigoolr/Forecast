package com.rodrigolessinger.forecast

import android.app.Activity
import android.app.Application

/**
 * Created by Rodrigo on 19/06/2016.
 */
class ForecastApplication : Application() {

    companion object {

        fun getApplication(activity: Activity) : ForecastApplication {
            return activity.application as ForecastApplication
        }

    }

    lateinit var component: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        component = ApplicationComponent.Initializer.init(this)
        component.inject(this)
    }
}