package com.rodrigolessinger.forecast.activity

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.adapter.CityListAdapter
import com.rodrigolessinger.forecast.extension.observeOnMainThread
import com.rodrigolessinger.forecast.repository.WeatherRepository
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        private val TAG = "MAIN_ACTIVITY"
    }

    private val addButton by lazy { findViewById(R.id.add_button) as FloatingActionButton }

    private val swipeRefresh by lazy { findViewById(R.id.swipe_refresh) as SwipeRefreshLayout }

    private val cityList by lazy { findViewById(R.id.city_list) as RecyclerView }
    @Inject protected lateinit var adapter: CityListAdapter

    @Inject protected lateinit var repository: WeatherRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        component = ActivityComponent.Initializer.init(this)
        component.inject(this)

        cityList.setHasFixedSize(true)
        cityList.layoutManager = LinearLayoutManager(this)
        cityList.adapter = adapter

        addButton.setOnClickListener { AddCityActivity.startActivity(this@MainActivity) }

        swipeRefresh.setOnRefreshListener {
            repository.refresh()
        }
    }

    override fun onSubscribable() {
        addSubscription(
                repository.get()
                        .map { it?.sortedBy { it.cityName  } }
                        .observeOnMainThread()
                        .subscribe { adapter.setData(it) }
        )

        addSubscription(
                repository.loadingObservable
                        .observeOnMainThread()
                        .subscribe { swipeRefresh.isRefreshing = it }
        )
    }

}
