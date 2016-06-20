package com.rodrigolessinger.forecast.api.converter

import com.rodrigolessinger.forecast.api.model.ForecastWeather
import com.rodrigolessinger.forecast.model.Forecast
import java.util.*

/**
 * Created by Rodrigo on 20/06/2016.
 */
class ForecastConverter : ModelConverter<com.rodrigolessinger.forecast.api.model.Forecast, List<Forecast>> {

    private fun convertObject(obj: ForecastWeather): Forecast {
        return com.rodrigolessinger.forecast.model.Forecast(
                date = Date(obj.date * 1000),
                temperature = obj.temperature.value.toInt()
        )
    }

    override fun convert(obj: com.rodrigolessinger.forecast.api.model.Forecast): List<Forecast> {
        return obj.list.map { convertObject(it) }
    }
}