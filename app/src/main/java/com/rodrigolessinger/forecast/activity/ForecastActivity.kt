package com.rodrigolessinger.forecast.activity

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.adapter.ForecastAdapter
import com.rodrigolessinger.forecast.extension.observeOnMainThread
import com.rodrigolessinger.forecast.model.CityWeather
import com.rodrigolessinger.forecast.repository.WeatherRepository
import javax.inject.Inject

class ForecastActivity : BaseActivity() {

    companion object {

        private val TAG = "FORECAST_ACTIVITY"

        private val ARG_CITY_ID = "ARG_CITY_ID"
        private val ARG_COLOR_ID = "ARG_COLOR_ID"

        fun startActivity(activity: Activity, colorId: Int, cityId: Long) {
            val intent = Intent(activity, ForecastActivity::class.java)
            intent.putExtra(ARG_CITY_ID, cityId)
            intent.putExtra(ARG_COLOR_ID, colorId)

            activity.startActivity(intent)
        }

    }

    @Inject protected lateinit var repository: WeatherRepository

    private val swipeRefresh by lazy { findViewById(R.id.swipe_refresh) as SwipeRefreshLayout }

    private val content by lazy { findViewById(R.id.content) as View }

    private val currentWeather by lazy { findViewById(R.id.weather_icon) as ImageView }
    private val currentTemperature by lazy { findViewById(R.id.current_temperature) as TextView }
    private val weatherDescription by lazy { findViewById(R.id.weather_description) as TextView }

    private val forecastList by lazy { findViewById(R.id.forecast_list) as RecyclerView }
    @Inject protected lateinit var adapter: ForecastAdapter

    private var cityId: Long = 0

    private var lightColor: Int = 0
    private var color: Int = 0
    private var darkColor: Int = 0

    private fun deleteItem() {
        AlertDialog.Builder(this)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.confirm_delete_positive) { dialog, which ->
                    unsubscribe()
                    repository.remove(CityWeather(id = cityId))
                    finish()
                }
                .setNegativeButton(R.string.confirm_delete_negative, null)
                .create()
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_forecast, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> {
                deleteItem()
                return true;
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    @TargetApi(21)
    private fun setupStatusBar() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = darkColor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        component = ActivityComponent.Initializer.init(this)
        component.inject(this)

        cityId = intent.getLongExtra(ARG_CITY_ID, 0)
        val colorId = intent.getIntExtra(ARG_COLOR_ID, 0)

        lightColor = resources.getIntArray(R.array.weather_light_colors)[colorId]
        color = resources.getIntArray(R.array.weather_colors)[colorId]
        darkColor = resources.getIntArray(R.array.weather_dark_colors)[colorId]

        content.background = ColorDrawable(color)

        toolbar.setBackgroundColor(lightColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) setupStatusBar()

        forecastList.layoutManager = LinearLayoutManager(this)
        forecastList.adapter = adapter
        forecastList.isNestedScrollingEnabled = false

        adapter.setColor(lightColor)

        swipeRefresh.setOnRefreshListener {
            repository.refreshForecast(cityId)
        }
    }

    private fun onError() {
        Toast.makeText(this, R.string.error_getting_forecast, Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onSubscribable() {
        val detailObservable =
                repository.getDetail(cityId)
                        .doOnNext { if (it == null) onError() }
                        .observeOnMainThread()

        addSubscription(
                detailObservable
                        .map { it?.cityName }
                        .subscribe { title = it }
        )

        addSubscription(
                detailObservable
                        .map { it?.weatherIcon }
                        .map { if (it != null && it != 0) getDrawable(it) else null }
                        .subscribe { currentWeather.setImageDrawable(it) }
        )

        addSubscription(
                detailObservable
                        .map { if (it != null && it.temperature != Int.MIN_VALUE) it.temperature.toString() else "" }
                        .subscribe { currentTemperature.text = it }
        )

        addSubscription(
                detailObservable
                        .map { it?.weatherDescription?.capitalize() }
                        .subscribe { weatherDescription.text = it }
        )

        addSubscription(
                detailObservable
                        .map { it?.forecast }
                        .subscribe { adapter.setData(it) }
        )

        addSubscription(
                repository.loadingForecastObservable
                        .observeOnMainThread()
                        .subscribe { swipeRefresh.isRefreshing = it }
        )
    }
}
