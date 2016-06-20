package com.rodrigolessinger.forecast.repository

import com.rodrigolessinger.forecast.api.client.WeatherClient
import com.rodrigolessinger.forecast.api.converter.CityWeatherConverter
import com.rodrigolessinger.forecast.api.converter.ForecastConverter
import com.rodrigolessinger.forecast.api.service.WeatherService
import com.rodrigolessinger.forecast.cache.PreferencesCache
import com.rodrigolessinger.forecast.cache.WeatherCache
import com.rodrigolessinger.forecast.extension.convert
import com.rodrigolessinger.forecast.extension.subscribeOnIo
import com.rodrigolessinger.forecast.extension.subscribeOnce
import com.rodrigolessinger.forecast.model.CityWeather
import com.rodrigolessinger.forecast.model.Forecast
import rx.Observable
import java.util.*
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

        private val MINIMUM_CACHED_TIME = 15 * 60 * 1000
        private val MAXIMUM_CACHED_TIME = 40 * 60 * 1000
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

    private fun isAfter(lastUpdate: Date?, time: Int): Boolean {
        if (lastUpdate == null) return false

        val maxDate = Date(lastUpdate.time + time)
        return Date().after(maxDate)
    }

    private fun isExpired(lastUpdate: Date?): Boolean {
        return isAfter(lastUpdate, MAXIMUM_CACHED_TIME)
    }

    private fun isUpdatable(lastUpdate: Date?): Boolean {
        return isAfter(lastUpdate, MINIMUM_CACHED_TIME)
    }

    fun get(): Observable<List<CityWeather>?> {
        return cache.get()
                .map {
                    it?.forEach {
                        if (isExpired(it.lastUpdate)) it.temperature = 0
                        if (isUpdatable(it.lastUpdate)) update(it.id)
                    }

                    it
                }
                .doOnNext { if (it == null || it.isEmpty()) onEmpty() }
    }

    private fun updateForecast(cityWeather: CityWeather, forecast: List<Forecast>): CityWeather {
        cityWeather.forecast = forecast
        return cityWeather
    }

    private fun updateForecast(cityWeather: CityWeather) {
        service.getForecast(cityWeather.id)
                .convert(ForecastConverter())
                .zipWith(
                        Observable.just(cityWeather),
                        { forecast, cityWeather -> updateForecast(cityWeather, forecast) }
                )
                .doOnNext { it.lastForecastUpdate = Date() }
                .subscribeOnce { cache.update(it) }
    }

    fun getDetail(id: Long): Observable<CityWeather?> {
        return get().map { it?.firstOrNull { it.id == id } }
                .map {
                    if (it != null && isExpired(it.lastForecastUpdate)) updateForecast(it, ArrayList())
                    else it
                }
                .doOnNext {
                    if (it != null && (it.forecast.size == 0 || isUpdatable(it.lastForecastUpdate))) {
                        updateForecast(it)
                    }
                }
    }

    private fun update(id: Long) {
        service.getCurrentWeatherByCityId(id)
                .subscribeOnIo()
                .convert(CityWeatherConverter())
                .doOnNext { it.lastUpdate = Date() }
                .subscribeOnce { cache.update(it) }
    }

    fun add(cityName: String) {
        service.getCurrentWeatherByCityName(cityName)
                .subscribeOnIo()
                .convert(CityWeatherConverter())
                .doOnNext { it.lastUpdate = Date() }
                .subscribeOnce { cache.add(it) }
    }

}