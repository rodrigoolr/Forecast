package com.rodrigolessinger.forecast.cache

import com.rodrigolessinger.forecast.model.Preferences
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rodrigo on 20/06/2016.
 */
@Singleton
class PreferencesCache @Inject constructor() : RealmCache<Preferences>() {

    fun get(): Observable<Preferences> {
        return getFindAllObservable { it.where(Preferences::class.java) }
                .map { it?.firstOrNull() ?: Preferences() }
    }

}