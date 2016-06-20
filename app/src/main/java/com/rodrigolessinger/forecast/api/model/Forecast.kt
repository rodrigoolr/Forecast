package com.rodrigolessinger.forecast.api.model

/**
 * Created by Rodrigo on 19/06/2016.
 */
data class Forecast(
        val city: City,
        val list: List<ForecastWeather>
)