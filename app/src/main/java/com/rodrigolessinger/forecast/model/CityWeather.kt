package com.rodrigolessinger.forecast.model

import com.rodrigolessinger.forecast.extension.toRealmList
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Rodrigo on 20/06/2016.
 */
open class CityWeather(
        id: Long = 0,
        cityName: String = "",
        countryName: String = "",
        temperature: Int = 0,
        forecast: List<Forecast> = arrayListOf()
) : RealmObject() {

    @PrimaryKey
    var id: Long = id
        protected set

    var cityName: String = cityName
        protected set

    var countryName: String = countryName
        protected set

    var temperature: Int = temperature
        protected set

    private var forecastRealmList: RealmList<Forecast> = forecast.toRealmList()
    val forecast: List<Forecast>
        get() = forecastRealmList

}