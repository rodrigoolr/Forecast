package com.rodrigolessinger.forecast.api.converter

import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.model.CityWeather
import java.util.*

/**
 * Created by Rodrigo on 20/06/2016.
 */
class CityWeatherConverter : ModelConverter<com.rodrigolessinger.forecast.api.model.CityWeather, CityWeather> {

    private fun getCountryName(countryCode: String): String {
        return Locale("", countryCode).displayName
    }

    private fun getIcon(weatherCode: String): Int {
        return when (weatherCode) {
            "01d" -> R.drawable.clear
            "01n" -> R.drawable.clear_night
            "02d" -> R.drawable.few_clouds
            "02n" -> R.drawable.few_clouds_night
            "03d", "03n", "04d", "04n" -> R.drawable.cloudy
            "09d", "09n" -> R.drawable.shower_rain
            "10d" -> R.drawable.rain
            "10n" -> R.drawable.rain_night
            "11d", "11n" -> R.drawable.thunderstorm
            "13d", "13n" -> R.drawable.snow
            else -> 0
        }
    }

    private fun getColoredIcon(weatherCode: String): Int {
        return when (weatherCode) {
            "01d" -> R.drawable.clear_color
            "01n" -> R.drawable.clear_night_color
            "02d" -> R.drawable.few_clouds_color
            "02n" -> R.drawable.few_clouds_night_color
            "03d", "03n", "04d", "04n" -> R.drawable.cloudy_color
            "09d", "09n" -> R.drawable.shower_rain_color
            "10d" -> R.drawable.rain_color
            "10n" -> R.drawable.rain_night_color
            "11d", "11n" -> R.drawable.thunderstorm_color
            "13d", "13n" -> R.drawable.snow_color
            else -> 0
        }
    }


    override fun convert(obj: com.rodrigolessinger.forecast.api.model.CityWeather): CityWeather {
        return CityWeather(
                id = obj.id,
                cityName = obj.name,
                countryName = getCountryName(obj.systemInfo.contryCode),
                weatherIcon = getIcon(obj.weather[0].icon),
                weatherColoredIcon = getColoredIcon(obj.weather[0].icon),
                temperature = obj.temperature.value.toInt()
        )
    }
}