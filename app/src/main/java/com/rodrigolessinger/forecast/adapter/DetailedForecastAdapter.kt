package com.rodrigolessinger.forecast.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.di.ForActivity
import com.rodrigolessinger.forecast.model.Forecast
import java.util.*
import javax.inject.Inject

/**
 * Created by Rodrigo on 19/06/2016.
 */
class DetailedForecastAdapter @Inject constructor(
        @ForActivity private val context: Context
): RecyclerView.Adapter<DetailedForecastAdapter.DetailViewHolder>() {

    private var data: List<Forecast> = arrayListOf()

    fun setData(data: List<Forecast>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_forecast_detail, parent, false);
        return DetailViewHolder(view)
    }

    private fun getDateHour(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date

        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val forecast = data[position]

        val time = getDateHour(forecast.date).toString() + "h"
        val temperature = forecast.temperature.toString() + "ºC"

        holder.bind(time, temperature)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class DetailViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val timeView by lazy { view.findViewById(R.id.time) as TextView }
        private val temperatureView by lazy { view.findViewById(R.id.temperature) as TextView }

        fun bind(time: String, temperature: String) {
            timeView.text = time
            temperatureView.text = temperature
        }

    }

}