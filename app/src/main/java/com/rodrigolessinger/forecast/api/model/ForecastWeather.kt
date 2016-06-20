package com.rodrigolessinger.forecast.api.model

import com.google.gson.annotations.SerializedName

data class ForecastWeather(
        @SerializedName("dt")
        val date: Long,

        @SerializedName("main")
        val temperature: Temperature,

        val weather: List<Weather>
)