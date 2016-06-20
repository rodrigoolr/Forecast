package com.rodrigolessinger.forecast.cache

import com.rodrigolessinger.forecast.model.CityWeather
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Convenience class for obtaining weather-related objects from the database
 * Use {@link #get} to retrieve all available forecasts
 */
@Singleton
class WeatherCache @Inject constructor() : RealmCache<CityWeather>() {

    fun get(): Observable<List<CityWeather>?> {
        return getFindAllObservable { it.where(CityWeather::class.java) }
    }

    fun get(id: Long): Observable<CityWeather> {
        return getFindAllObservable { it.where(CityWeather::class.java).equalTo("id", id) }
                .map { it?.firstOrNull() }
    }

    fun remove(obj: CityWeather) {
        val realmObj = getRealm().where(CityWeather::class.java).equalTo("id", obj.id).findFirst()
        if (realmObj != null) removeRealmObject(realmObj)
    }

}