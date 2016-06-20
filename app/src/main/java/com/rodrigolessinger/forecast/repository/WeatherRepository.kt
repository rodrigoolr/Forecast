package com.rodrigolessinger.forecast.repository

import com.rodrigolessinger.forecast.api.client.WeatherClient
import com.rodrigolessinger.forecast.api.converter.CityWeatherConverter
import com.rodrigolessinger.forecast.api.service.WeatherService
import com.rodrigolessinger.forecast.cache.PreferencesCache
import com.rodrigolessinger.forecast.cache.WeatherCache
import com.rodrigolessinger.forecast.extension.convert
import com.rodrigolessinger.forecast.extension.subscribeOnIo
import com.rodrigolessinger.forecast.extension.subscribeOnce
import com.rodrigolessinger.forecast.model.CityWeather
import rx.Observable
import javax.inject.Inject

/**
 * Created by Rodrigo on 20/06/2016.
 */
class WeatherRepository @Inject constructor(
        private val cache: WeatherCache,
        private val preferencesCache: PreferencesCache,
        private val client: WeatherClient
) {

    companion object {
        private val CITIES_LIST = arrayOf("Sao Paulo,BR", "Recife,BR", "Lima,PE", "Dublin,IE")
    }

    private val service: WeatherService by lazy { client.createService(WeatherService::class.java) }

    private fun obtainInitialCities() {
        CITIES_LIST.forEach { add(it) }
    }

    private fun onEmpty() {
        preferencesCache.get()
                .map { it.hasObtainedInitialCities }
                .subscribeOnce { if (! it) obtainInitialCities() }
    }

    fun get(): Observable<List<CityWeather>?> {
        return cache.get()
                .doOnNext { if (it == null || it.isEmpty()) onEmpty() }
    }

    fun add(cityName: String) {
        service.getCurrentWeatherByCityName(cityName)
                .subscribeOnIo()
                .convert(CityWeatherConverter())
                .subscribeOnce { cache.add(it) }
    }

}