package com.rodrigolessinger.forecast.api.model

/**
 * Created by Rodrigo on 19/06/2016.
 */
data class Weather(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
)