package com.rodrigolessinger.forecast.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Rodrigo on 19/06/2016.
 */
data class Temperature(
        @SerializedName("temp")
        val temperature: Double,

        val humidity: Int,

        @SerializedName("temp_min")
        val minimumTemperature: Double,

        @SerializedName("temp_max")
        val maximumTemperature: Double
)