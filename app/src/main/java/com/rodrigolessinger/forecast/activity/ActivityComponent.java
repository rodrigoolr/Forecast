package com.rodrigolessinger.forecast.activity;

import android.app.Activity;

import com.rodrigolessinger.forecast.ApplicationComponent;
import com.rodrigolessinger.forecast.ForecastApplication;
import com.rodrigolessinger.forecast.di.PerActivity;

import dagger.Component;

/**
 * Created by armueller on 3/4/15.
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

}
