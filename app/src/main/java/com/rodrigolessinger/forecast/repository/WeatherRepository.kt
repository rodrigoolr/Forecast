package com.rodrigolessinger.forecast.repository

import android.content.res.Resources
import android.util.Log
import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.api.client.WeatherClient
import com.rodrigolessinger.forecast.api.converter.CityWeatherConverter
import com.rodrigolessinger.forecast.api.converter.ForecastConverter
import com.rodrigolessinger.forecast.api.service.WeatherService
import com.rodrigolessinger.forecast.cache.PreferencesCache
import com.rodrigolessinger.forecast.cache.WeatherCache
import com.rodrigolessinger.forecast.di.ForApplication
import com.rodrigolessinger.forecast.extension.convert
import com.rodrigolessinger.forecast.extension.subscribeOnIo
import com.rodrigolessinger.forecast.extension.subscribeOnce
import com.rodrigolessinger.forecast.model.CityWeather
import com.rodrigolessinger.forecast.model.Forecast
import rx.Observable
import rx.subjects.BehaviorSubject
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rodrigo on 20/06/2016.
 */
@Singleton
class WeatherRepository @Inject constructor(
        @ForApplication private val resources: Resources,

        private val cache: WeatherCache,
        private val preferencesCache: PreferencesCache,

        private val client: WeatherClient
) {

    companion object {
        private val TAG = "WEATHER_REP"

        private val MINIMUM_UPDATE_TIME = 15 * 1000

        private val MINIMUM_CACHED_TIME = 20 * 60 * 1000
        private val MAXIMUM_CACHED_TIME = 40 * 60 * 1000
    }

    private val service: WeatherService by lazy { client.createService(WeatherService::class.java) }

    private val recentUpdates: MutableMap<Long, Date> = mutableMapOf()
    private val ongoingUpdates: MutableSet<Long> = mutableSetOf()
    private val loadingSubject = BehaviorSubject.create<Boolean>(false)
    val loadingObservable: Observable<Boolean> = loadingSubject

    private val recentForecastUpdates: MutableMap<Long, Date> = mutableMapOf()
    private val ongoingForecastUpdates: MutableSet<Long> = mutableSetOf()
    private val loadingForecastSubject = BehaviorSubject.create<Boolean>(false)
    val loadingForecastObservable: Observable<Boolean> = loadingForecastSubject

    private val initialCitiesList = resources.getStringArray(R.array.initial_cities)

    private fun updateLoadingSubject() {
        loadingSubject.onNext(ongoingUpdates.size != 0)
    }

    private fun onUpdateFinished(id: Long) {
        synchronized(ongoingUpdates) {
            ongoingUpdates.remove(id)
            updateLoadingSubject()
        }
    }

    private fun canUpdate(id: Long): Boolean {
        synchronized(ongoingUpdates) {
            if (recentUpdates.contains(id) && ! isAfter(recentUpdates[id], MINIMUM_UPDATE_TIME)) {
                updateLoadingSubject()
                return false

            } else {
                recentUpdates.put(id, Date())
                ongoingUpdates.add(id)

                updateLoadingSubject()

                return true;
            }
        }
    }

    private fun updateForecastLoadingSubject() {
        loadingForecastSubject.onNext(ongoingForecastUpdates.size != 0)
    }

    private fun onForecastUpdateFinished(id: Long) {
        synchronized(ongoingForecastUpdates) {
            ongoingForecastUpdates.remove(id)
            updateForecastLoadingSubject()
        }
    }

    private fun canUpdateForecast(id: Long): Boolean {
        synchronized(ongoingForecastUpdates) {
            if (recentForecastUpdates.contains(id) && ! isAfter(recentForecastUpdates[id], MINIMUM_UPDATE_TIME)) {
                updateForecastLoadingSubject()
                return false

            } else {
                recentForecastUpdates.put(id, Date())
                ongoingForecastUpdates.add(id)

                updateForecastLoadingSubject()

                return true;
            }
        }
    }

    private fun obtainInitialCities() {
        initialCitiesList.forEach { add(it) }
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
                        if (isExpired(it.lastUpdate)) it.temperature = Int.MIN_VALUE
                        if (isUpdatable(it.lastUpdate)) update(it)
                    }

                    it
                }
                .doOnNext { if (it == null || it.isEmpty()) onEmpty() }
    }

    private fun updateForecast(cityWeather: CityWeather, forecast: List<Forecast>): CityWeather {
        cityWeather.forecast = forecast
        return cityWeather
    }

    fun getDetail(id: Long): Observable<CityWeather?> {
        return cache.get()
                .map { it?.firstOrNull { it.id == id } }
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

    private fun updateForecast(cityWeather: CityWeather) {
        if (! canUpdateForecast(cityWeather.id)) return

        service.getForecast(cityWeather.id)
                .subscribeOnIo()
                .convert(ForecastConverter())
                .zipWith(
                        Observable.just(cityWeather),
                        { forecast, cityWeather -> updateForecast(cityWeather, forecast) }
                )
                .doOnNext { it.lastForecastUpdate = Date() }
                .subscribeOnce(
                        {
                            cache.update(it)
                            onForecastUpdateFinished(cityWeather.id)
                        },
                        {
                            Log.e(TAG, "Error adding new city", it)
                            onForecastUpdateFinished(cityWeather.id)
                        }
                )
    }

    fun refresh() {
        get().flatMapIterable { it }.subscribeOnce { update(it) }
    }

    fun refreshForecast(id: Long) {
        get().flatMapIterable { it }.filter { it.id == id }.subscribeOnce { updateForecast(it) }
    }

    private fun update(cityWeather: CityWeather) {
        if (! canUpdate(cityWeather.id)) return

        service.getCurrentWeatherByCityId(cityWeather.id)
                .subscribeOnIo()
                .convert(CityWeatherConverter())
                .doOnNext { it.lastUpdate = Date() }
                .doOnNext { it.forecast = cityWeather.forecast }
                .doOnNext { it.lastForecastUpdate = cityWeather.lastForecastUpdate }
                .subscribeOnce(
                        {
                            cache.update(it)
                            onUpdateFinished(cityWeather.id)
                        },
                        {
                            Log.e(TAG, "Error updating weather information", it)
                            onUpdateFinished(cityWeather.id)
                        }
                )
    }

    fun add(cityName: String, onSuccess: ((CityWeather) -> Unit)? = null, onError: (() -> Unit)? = null) {
        service.getCurrentWeatherByCityName(cityName)
                .subscribeOnIo()
                .convert(CityWeatherConverter())
                .doOnNext { it.lastUpdate = Date() }
                .subscribeOnce(
                        { cache.add(it); if (onSuccess != null) onSuccess(it) },
                        { Log.e(TAG, "Error adding new city", it); if (onError != null) onError() }
                )
    }

    fun remove(cityWeather: CityWeather) {
        cache.remove(cityWeather)
    }

}