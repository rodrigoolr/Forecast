package com.rodrigolessinger.forecast.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.api.model.City
import java.util.*
import javax.inject.Inject

/**
 * Created by Rodrigo on 19/06/2016.
 */
class CityListAdapter @Inject constructor() : RecyclerView.Adapter<CityListAdapter.CityViewHolder>() {

    private var data: List<City> = arrayListOf()

    fun setData(data: List<City>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = data[position]

        val name = city.name

        val countryCode = city.systemInfo.contryCode
        val countryName = Locale("", countryCode).displayCountry

        val temperature = city.temperature.temperature.toInt().toString() + "ºC"

        holder.bind(name, countryName, temperature)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val inflater = LayoutInflater.from(parent.getContext())
        val view = inflater.inflate(R.layout.list_item_city, parent, false);
        return CityViewHolder(view)
    }

    class CityViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val nameView by lazy { view.findViewById(R.id.city_name) as TextView }
        private val countryView by lazy { view.findViewById(R.id.country_code) as TextView }
        private val temperatureView by lazy { view.findViewById(R.id.city_temperature) as TextView }

        fun bind(name: String, country: String, temperature: String) {
            nameView.text = name
            countryView.text = country
            temperatureView.text = temperature
        }

    }

}