package com.rodrigolessinger.forecast.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.api.model.Forecast
import com.rodrigolessinger.forecast.api.model.ForecastWeather
import com.rodrigolessinger.forecast.di.ForActivity
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

/**
 * Created by Rodrigo on 19/06/2016.
 */
class ForecastAdapter @Inject constructor(
        @ForActivity private val context: Context,
        val adapterProvider: Provider<DetailedForecastAdapter>
): RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    companion object {
        private val MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000
    }

    private var data: List<Pair<Date, List<ForecastWeather>>> = arrayListOf()

    private fun getDateDay(date: Long): Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date(date)

        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 0)

        return calendar.time
    }

    fun setData(forecast: Forecast) {
        this.data = forecast.list
                .groupBy { getDateDay(it.date * 1000) }
                .toList()
                .sortedBy { it.first }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_forecast, parent, false);
        return ForecastViewHolder(view, adapterProvider.get())
    }

    private fun getWeekDayString(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date

        return when(calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Segunda-Feira"
            Calendar.TUESDAY -> "Terça-Feira"
            Calendar.WEDNESDAY -> "Quarta-Feira"
            Calendar.THURSDAY -> "Quinta-Feira"
            Calendar.FRIDAY -> "Sexta-Feira"
            Calendar.SATURDAY -> "Sábado"
            Calendar.SUNDAY -> "Domingo"
            else -> ""
        }
    }

    private fun getTitle(date: Date): String {
        val difference = date.time - getDateDay(Date().time).time
        val days = (difference / MILLISECONDS_PER_DAY).toInt()

        return when (days) {
            0 -> "Hoje"
            1 -> "Amanhã"
            else -> getWeekDayString(date)
        }
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = data[position]

        val title = getTitle(item.first)
        val forecast = item.second

        holder.bind(title, forecast)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ForecastViewHolder(
            private val view: View,
            private val adapter: DetailedForecastAdapter
    ) : RecyclerView.ViewHolder(view) {

        private val titleView by lazy { view.findViewById(R.id.title) as TextView }
        private val detailedForecast by lazy { view.findViewById(R.id.detailed_forecast_list) as RecyclerView }

        init {
            detailedForecast.setHasFixedSize(true)
            detailedForecast.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            detailedForecast.adapter = adapter
            detailedForecast.isNestedScrollingEnabled = false
        }

        fun bind(title: String, forecast: List<ForecastWeather>) {
            titleView.text = title
            adapter.setData(forecast)
        }

    }

}