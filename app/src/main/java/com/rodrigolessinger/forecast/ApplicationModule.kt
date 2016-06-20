package com.rodrigolessinger.forecast

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.rodrigolessinger.forecast.di.ForApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module that provides application dependencies that cannot be initialized normally
 */
@Module
open class ApplicationModule(
        private val application: Application
) {

    @Provides
    @Singleton
    internal fun provideApplication(): Application {
        return application
    }

    @Provides
    @Singleton
    @ForApplication
    internal fun provideApplicationContext(): Context {
        return application
    }

    @Provides
    @Singleton
    @ForApplication
    internal fun provideApplicationResources(): Resources {
        return application.resources
    }

}