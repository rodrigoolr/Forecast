package com.rodrigolessinger.forecast.api.model

data class Forecast(
        val city: City,
        val list: List<ForecastWeather>
)