package com.rodrigolessinger.forecast.api.converter

import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.api.model.ForecastWeather
import com.rodrigolessinger.forecast.model.Forecast
import java.util.*

/**
 * Converts an Forecast API Model into a list of Forecast App models that can be persisted and displayed
 * For convenience use {@link Observable#convert}
 */
class ForecastConverter : ModelConverter<com.rodrigolessinger.forecast.api.model.Forecast, List<Forecast>> {

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

    private fun convertObject(obj: ForecastWeather): Forecast {
        return com.rodrigolessinger.forecast.model.Forecast(
                date = Date(obj.date * 1000),
                weatherIcon = getIcon(obj.weather[0].icon),
                temperature = obj.temperature.value.toInt()
        )
    }

    override fun convert(obj: com.rodrigolessinger.forecast.api.model.Forecast): List<Forecast> {
        return obj.list.map { convertObject(it) }
    }
}