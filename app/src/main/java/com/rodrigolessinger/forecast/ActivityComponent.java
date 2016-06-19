package com.rodrigolessinger.forecast;

import android.app.Activity;

import com.rodrigolessinger.forecast.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by armueller on 3/4/15.
 */
@Singleton
@Component(modules = {ActivityModule.class})
public interface ActivityComponent {

    final class Initializer {
        public static ActivityComponent init(Activity activity) {
            return DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(activity))
                    .build();
        }

    }

    void inject(MainActivity activity);

}
