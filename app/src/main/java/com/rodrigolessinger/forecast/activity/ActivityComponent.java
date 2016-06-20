package com.rodrigolessinger.forecast.activity;

import android.app.Activity;

import com.rodrigolessinger.forecast.ApplicationComponent;
import com.rodrigolessinger.forecast.ForecastApplication;
import com.rodrigolessinger.forecast.di.PerActivity;

import dagger.Component;

/**
 * Component used to inject dependencies on all activities
 * Use the {@link Initializer} to get an instance for an activity
 * and then the proper {@link #inject} method
 */
@PerActivity
@Component(dependencies = {ApplicationComponent.class}, modules = {ActivityModule.class})
public interface ActivityComponent {

    final class Initializer {
        public static ActivityComponent init(Activity activity) {
            ForecastApplication forecastApplication = ForecastApplication.Companion.getApplication(activity);
            ApplicationComponent applicationComponent = forecastApplication.getComponent();

            return DaggerActivityComponent.builder()
                    .applicationComponent(applicationComponent)
                    .activityModule(new ActivityModule(activity))
                    .build();
        }

    }

    void inject(MainActivity activity);
    void inject(ForecastActivity activity);
    void inject(AddCityActivity activity);

}
