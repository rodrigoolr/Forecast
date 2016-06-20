package com.rodrigolessinger.forecast.cache

import com.rodrigolessinger.forecast.model.Preferences
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Convenience class for obtaining the latest preferences from the database
 * Use {@link #get} to retrieve updates in the user's {@link Preferences} object.
 */
@Singleton
class PreferencesCache @Inject constructor() : RealmCache<Preferences>() {

    fun get(): Observable<Preferences> {
        return getFindAllObservable { it.where(Preferences::class.java) }
                .map { it?.firstOrNull() ?: Preferences() }
    }

}