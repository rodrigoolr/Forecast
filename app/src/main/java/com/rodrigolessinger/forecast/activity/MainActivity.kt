package com.rodrigolessinger.forecast.activity

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.adapter.CityListAdapter
import com.rodrigolessinger.forecast.api.client.WeatherClient
import com.rodrigolessinger.forecast.api.model.City
import com.rodrigolessinger.forecast.api.service.WeatherService
import com.rodrigolessinger.forecast.extension.filterNotNull
import com.rodrigolessinger.forecast.extension.observeOnMainThread
import com.rodrigolessinger.forecast.extension.subscribeOnIo
import com.rodrigolessinger.forecast.extension.subscribeOnce
import rx.Observable
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MAIN_ACTIVITY"
    }

    lateinit var component: ActivityComponent
        private set

    private val toolbar by lazy { findViewById(R.id.toolbar) as Toolbar }
    private val fab by lazy { findViewById(R.id.fab) as FloatingActionButton }

    private val cityList by lazy { findViewById(R.id.city_list) as RecyclerView }
    @Inject protected lateinit var adapter: CityListAdapter

    @Inject protected lateinit var client: WeatherClient
    private val service: WeatherService by lazy { client.createService(WeatherService::class.java) }

    private var subscriptions: CompositeSubscription? = null

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

    private fun getCityWeather(name: String): Observable<City?> {
        return service.getCurrentWeatherByCityName(name).subscribeOnIo().onErrorReturn(null)
    }

    override fun onStart() {
        super.onStart()

        subscriptions = CompositeSubscription()
        subscriptions?.add(
                Observable.from(CITIES_LIST)
                        .flatMap { getCityWeather(it) }
                        .subscribeOnIo()
                        .filterNotNull()
                        .toSortedList { cityA, cityB -> cityA.name.compareTo(cityB.name) }
                        .observeOnMainThread()
                        .subscribeOnce { adapter.setData(it) }
        )
    }

    override fun onStop() {
        super.onStop()

        subscriptions?.unsubscribe()
        subscriptions = null
    }
}
