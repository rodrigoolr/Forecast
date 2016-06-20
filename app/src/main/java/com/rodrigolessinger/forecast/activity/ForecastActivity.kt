package com.rodrigolessinger.forecast.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.TextView
import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.adapter.ForecastAdapter
import com.rodrigolessinger.forecast.api.client.WeatherClient
import com.rodrigolessinger.forecast.api.service.WeatherService
import com.rodrigolessinger.forecast.extension.observeOnMainThread
import com.rodrigolessinger.forecast.extension.subscribeOnIo
import javax.inject.Inject

class ForecastActivity : BaseActivity() {

    companion object {

        private val TAG = "FORECAST_ACTIVITY"

        private val ARG_CITY_ID = "ARG_CITY_ID"

        fun startActivity(activity: Activity, cityId: Int) {
            val intent = Intent(activity, ForecastActivity::class.java)
            intent.putExtra(ARG_CITY_ID, cityId)

            activity.startActivity(intent)
        }

    }

    @Inject protected lateinit var client: WeatherClient
    private val service: WeatherService by lazy { client.createService(WeatherService::class.java) }

    private val currentTemperature by lazy { findViewById(R.id.current_temperature) as TextView }

    private val forecastList by lazy { findViewById(R.id.forecast_list) as RecyclerView }
    @Inject protected lateinit var adapter: ForecastAdapter

    private var cityId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        component = ActivityComponent.Initializer.init(this)
        component.inject(this)

        cityId = intent.getIntExtra(ARG_CITY_ID, 0)

        forecastList.layoutManager = LinearLayoutManager(this)
        forecastList.adapter = adapter
    }

    override fun onSubscribable() {
        addSubscription(
                service.getCurrentWeatherByCityId(cityId)
                        .subscribeOnIo()
                        .observeOnMainThread()
                        .subscribe(
                                { currentTemperature.text = it.temperature.temperature.toInt().toString() },
                                { Log.e(TAG, "Error loading current temperature", it) }
                        )
        )

        addSubscription(
                service.getForecast(cityId)
                        .subscribeOnIo()
                        .observeOnMainThread()
                        .subscribe(
                                { adapter.setData(it) },
                                { Log.e(TAG, "Error getting forecast", it) }
                        )
        )
    }
}
