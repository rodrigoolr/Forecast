package com.rodrigolessinger.forecast.api.model

import com.google.gson.annotations.SerializedName

data class Temperature(
        @SerializedName("temp")
        val value: Double
)