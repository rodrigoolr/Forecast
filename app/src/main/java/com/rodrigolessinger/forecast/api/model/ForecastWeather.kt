package com.rodrigolessinger.forecast.api.model

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Rodrigo on 19/06/2016.
 */
data class ForecastWeather(
        @SerializedName("dt")
        val date: Date,

        @SerializedName("main")
        val temperature: Temperature,

        val weather: List<Weather>
)