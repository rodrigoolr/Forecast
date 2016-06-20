package com.rodrigolessinger.forecast.cache

import com.rodrigolessinger.forecast.model.Preferences
import rx.Observable
import javax.inject.Inject

/**
 * Created by Rodrigo on 20/06/2016.
 */
class PreferencesCache @Inject constructor() : RealmCache<Preferences>() {

    fun get(): Observable<Preferences> {
        return getFindAllObservable { it.where(Preferences::class.java) }
                .map { it?.firstOrNull() ?: Preferences() }
    }

}