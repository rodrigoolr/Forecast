package com.rodrigolessinger.forecast.api.model

import com.google.gson.annotations.SerializedName

data class SystemInfo(
        @SerializedName("country")
        val contryCode: String
)