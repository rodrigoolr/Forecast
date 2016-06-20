package com.rodrigolessinger.forecast.cache

import com.rodrigolessinger.forecast.model.CityWeather
import rx.Observable
import javax.inject.Inject

/**
 * Created by Rodrigo on 20/06/2016.
 */
class WeatherCache @Inject constructor() : RealmCache<CityWeather>() {

    fun get(): Observable<List<CityWeather>?> {
        return getFindAllObservable { it.where(CityWeather::class.java) }
    }

    fun get(id: Long): Observable<CityWeather> {
        return getFindAllObservable { it.where(CityWeather::class.java).equalTo("id", id) }
                .map { it?.firstOrNull() }
    }

}