package com.rodrigolessinger.forecast.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.activity.ForecastActivity
import com.rodrigolessinger.forecast.model.CityWeather
import javax.inject.Inject

/**
 * Created by Rodrigo on 19/06/2016.
 */
class CityListAdapter @Inject constructor(
        private val activity: Activity
) : RecyclerView.Adapter<CityListAdapter.CityViewHolder>() {

    private var data: List<CityWeather> = arrayListOf()

    fun setData(data: List<CityWeather>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val item = data[position]

        holder.bind(
                id = item.id,
                name = item.cityName,
                country = item.countryName,
                temperature = item.temperature.toString() + "ºC"
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_city, parent, false);
        return CityViewHolder(view)
    }

    inner class CityViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val nameView by lazy { view.findViewById(R.id.city_name) as TextView }
        private val countryView by lazy { view.findViewById(R.id.country_code) as TextView }
        private val temperatureView by lazy { view.findViewById(R.id.city_temperature) as TextView }

        fun bind(id: Long, name: String, country: String, temperature: String) {
            nameView.text = name
            countryView.text = country
            temperatureView.text = temperature

            view.setOnClickListener { ForecastActivity.startActivity(activity, id) }
        }

    }

}