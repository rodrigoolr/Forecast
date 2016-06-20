package com.rodrigolessinger.forecast.model

import io.realm.annotations.PrimaryKey

/**
 * Created by Rodrigo on 20/06/2016.
 */
open class CityWeather(
        @PrimaryKey
        var id: Long,
        var cityName: String,
        var countryName: String,
        var temperature: Int,
        var forecast: List<Forecast> = arrayListOf()
)