package com.rodrigolessinger.forecast.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Rodrigo on 19/06/2016.
 */
data class CityWeather(
        val id: Long,
        val code: Int,
        val name: String,
        val weather: List<Weather>,

        @SerializedName("main")
        val temperature: Temperature,

        @SerializedName("sys")
        val systemInfo: SystemInfo
)