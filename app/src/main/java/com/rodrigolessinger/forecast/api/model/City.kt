package com.rodrigolessinger.forecast.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Rodrigo on 19/06/2016.
 */
class City(
        val id: Int,
        val name: String,
        val weather: List<Weather>,

        @SerializedName("main")
        val temperature: Temperature,

        @SerializedName("sys")
        val systemInfo: SystemInfo
)