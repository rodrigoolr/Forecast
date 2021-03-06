package com.rodrigolessinger.forecast;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.rodrigolessinger.forecast.api.client.WeatherClient;
import com.rodrigolessinger.forecast.cache.PreferencesCache;
import com.rodrigolessinger.forecast.cache.WeatherCache;
import com.rodrigolessinger.forecast.di.ForApplication;
import com.rodrigolessinger.forecast.repository.WeatherRepository;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Component used to inject dependencies on the main application object
 * Also used to provide Singleton instances to the activity-scoped objects
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    final class Initializer {
        public static ApplicationComponent init(Application application) {
            return DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(application))
                    .build();
        }

    }

    void inject(ForecastApplication application);

    // Exported for child-components.
    @ForApplication Context applicationContext();
    @ForApplication Resources applicationResources();

    WeatherClient weatherClient();

    WeatherRepository weatherRepository();

    WeatherCache weatherCache();
    PreferencesCache preferencesCache();
}
