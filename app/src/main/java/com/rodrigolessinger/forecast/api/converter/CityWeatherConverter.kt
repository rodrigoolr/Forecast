package com.rodrigolessinger.forecast.api.converter

import com.rodrigolessinger.forecast.model.CityWeather
import java.util.*

/**
 * Created by Rodrigo on 20/06/2016.
 */
class CityWeatherConverter : ModelConverter<com.rodrigolessinger.forecast.api.model.CityWeather, CityWeather> {

    private fun getCountryName(countryCode: String): String {
        return Locale("", countryCode).displayName
    }

    override fun convert(obj: com.rodrigolessinger.forecast.api.model.CityWeather): CityWeather {
        return CityWeather(
                id = obj.id,
                cityName = obj.name,
                countryName = getCountryName(obj.systemInfo.contryCode),
                temperature = obj.temperature.value.toInt()
        )
    }
}