package com.rodrigolessinger.forecast.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.adapter.ForecastAdapter
import com.rodrigolessinger.forecast.extension.observeOnMainThread
import com.rodrigolessinger.forecast.repository.WeatherRepository
import javax.inject.Inject

class ForecastActivity : BaseActivity() {

    companion object {

        private val TAG = "FORECAST_ACTIVITY"

        private val ARG_CITY_ID = "ARG_CITY_ID"

        fun startActivity(activity: Activity, cityId: Long) {
            val intent = Intent(activity, ForecastActivity::class.java)
            intent.putExtra(ARG_CITY_ID, cityId)

            activity.startActivity(intent)
        }

    }

    @Inject protected lateinit var repository: WeatherRepository

    private val currentWeather by lazy { findViewById(R.id.weather_icon) as ImageView }
    private val currentTemperature by lazy { findViewById(R.id.current_temperature) as TextView }

    private val forecastList by lazy { findViewById(R.id.forecast_list) as RecyclerView }
    @Inject protected lateinit var adapter: ForecastAdapter

    private var cityId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        component = ActivityComponent.Initializer.init(this)
        component.inject(this)

        cityId = intent.getLongExtra(ARG_CITY_ID, 0)

        forecastList.layoutManager = LinearLayoutManager(this)
        forecastList.adapter = adapter
    }

    private fun onError() {
        Toast.makeText(this, "Ocorrreu um erro ao buscar a previsão para a cidade selecionada", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onSubscribable() {
        val detailObservable =
                repository.getDetail(cityId)
                        .doOnNext { if (it == null) onError() }
                        .observeOnMainThread()

        addSubscription(
                detailObservable
                        .map { it?.weatherColoredIcon }
                        .map { if (it != null && it != 0) getDrawable(it) else null }
                        .subscribe { currentWeather.setImageDrawable(it) }
        )

        addSubscription(
                detailObservable
                        .map { it?.temperature.toString() }
                        .subscribe { currentTemperature.text = it }
        )

        addSubscription(
                detailObservable
                        .map { it?.forecast }
                        .subscribe { adapter.setData(it) }
        )
    }
}
