package com.rodrigolessinger.forecast.activity

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.adapter.CityListAdapter
import com.rodrigolessinger.forecast.api.client.WeatherClient
import com.rodrigolessinger.forecast.api.converter.CityWeatherConverter
import com.rodrigolessinger.forecast.api.model.CityWeather
import com.rodrigolessinger.forecast.api.service.WeatherService
import com.rodrigolessinger.forecast.extension.*
import rx.Observable
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        private val TAG = "MAIN_ACTIVITY"
    }

    private val fab by lazy { findViewById(R.id.fab) as FloatingActionButton }

    private val cityList by lazy { findViewById(R.id.city_list) as RecyclerView }
    @Inject protected lateinit var adapter: CityListAdapter

    @Inject protected lateinit var client: WeatherClient
    private val service: WeatherService by lazy { client.createService(WeatherService::class.java) }

    private val CITIES_LIST = arrayOf("Sao Paulo", "Recife", "Lima")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        component = ActivityComponent.Initializer.init(this)
        component.inject(this)

        cityList.setHasFixedSize(true)
        cityList.layoutManager = LinearLayoutManager(this)
        cityList.adapter = adapter
    }

    private fun getCityWeather(name: String): Observable<CityWeather?> {
        return service.getCurrentWeatherByCityName(name).subscribeOnIo().onErrorReturn(null)
    }

    override fun onSubscribable() {
        addSubscription(
                Observable.from(CITIES_LIST)
                        .flatMap { getCityWeather(it) }
                        .filterNotNull()
                        .convert(CityWeatherConverter())
                        .subscribeOnIo()
                        .toSortedList { cityA, cityB -> cityA.cityName.compareTo(cityB.cityName) }
                        .observeOnMainThread()
                        .subscribeOnce { adapter.setData(it) }
        )
    }

}
