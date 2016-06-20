package com.rodrigolessinger.forecast.api.service

import com.rodrigolessinger.forecast.api.model.CityWeather
import com.rodrigolessinger.forecast.api.model.ForecastWeather
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Created by Rodrigo on 19/06/2016.
 */
interface  WeatherService {

    @GET("weather")
    fun getCurrentWeatherByCityId(@Query("id") id: Int): Observable<CityWeather>

    @GET("weather")
    fun getCurrentWeatherByCityName(@Query("q") name: String): Observable<CityWeather>

    @GET("weather")
    fun getForecast(@Query("id") id: Int): Observable<ForecastWeather>

}