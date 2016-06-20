package com.rodrigolessinger.forecast.api.service

import com.rodrigolessinger.forecast.api.model.CityWeather
import com.rodrigolessinger.forecast.api.model.Forecast
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Provides methods to call the Open Weather Rest API to get forecast updates
 */
interface  WeatherService {

    /**
     * Look for a city with the specified id in the API and returns its weather
     * @return City weather (without forecast)
     */
    @GET("weather")
    fun getCurrentWeatherByCityId(@Query("id") id: Long): Observable<CityWeather>

    /**
     * Look for a city the approximate name in the API and returns its weather (can be very inaccurate)
     * @return City weather (without forecast)
     */
    @GET("weather")
    fun getCurrentWeatherByCityName(@Query("q") name: String): Observable<CityWeather>

    /**
     * Gets the forecast for a city with the specified id
     * @return City forecast list
     */
    @GET("forecast")
    fun getForecast(@Query("id") id: Long): Observable<Forecast>

}