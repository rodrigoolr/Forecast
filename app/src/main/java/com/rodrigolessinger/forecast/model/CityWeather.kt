package com.rodrigolessinger.forecast.model

import com.rodrigolessinger.forecast.extension.toRealmList
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Rodrigo on 20/06/2016.
 */
open class CityWeather(
        id: Long = 0,
        cityName: String = "",
        countryName: String = "",
        weatherIcon: Int = 0,
        weatherColoredIcon: Int = 0,
        var temperature: Int = 0,
        forecast: List<Forecast> = arrayListOf(),
        var lastUpdate: Date? = null,
        var lastForecastUpdate: Date? = null
) : RealmObject() {

    @PrimaryKey
    var id: Long = id
        protected set

    var cityName: String = cityName
        protected set

    var countryName: String = countryName
        protected set

    var weatherIcon: Int = weatherIcon
        protected set

    var weatherColoredIcon: Int = weatherColoredIcon
        protected set

    private var forecastRealmList: RealmList<Forecast> = forecast.toRealmList()
    var forecast: List<Forecast>
        get() { return forecastRealmList }
        set(value) { forecastRealmList = value.toRealmList() }

}