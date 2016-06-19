package com.rodrigolessinger.forecast.activity

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Toast
import com.rodrigolessinger.forecast.ActivityComponent
import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.api.client.WeatherClient
import com.rodrigolessinger.forecast.api.extensions.observeOnMainThread
import com.rodrigolessinger.forecast.api.extensions.subscribeOnIo
import com.rodrigolessinger.forecast.api.extensions.subscribeOnce
import com.rodrigolessinger.forecast.api.service.WeatherService
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MAIN_ACTIVITY"
    }

    private val toolbar by lazy { findViewById(R.id.toolbar) as Toolbar }
    private val fab by lazy { findViewById(R.id.fab) as FloatingActionButton }

    private lateinit var component: ActivityComponent

    @Inject protected lateinit var client: WeatherClient
    private val service: WeatherService by lazy { client.createService(WeatherService::class.java) }

    private var subscriptions: CompositeSubscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        component = ActivityComponent.Initializer.init(this)
        component.inject(this)
    }

    override fun onStart() {
        super.onStart()

        subscriptions = CompositeSubscription()
        subscriptions?.add(
                service.getCurrentWeatherByCityName("Porto Alegre")
                        .subscribeOnIo()
                        .observeOnMainThread()
                        .subscribeOnce(
                                {
                                    AlertDialog.Builder(this@MainActivity)
                                            .setTitle(it.name)
                                            .setMessage(
                                                    "Main: %s\nTemp: %.0f\nMin: %.0f\nMax: %.0f\n Hum: %d%%".format(
                                                            it.weather[0].main,
                                                            it.temperature.temperature,
                                                            it.temperature.minimumTemperature,
                                                            it.temperature.maximumTemperature,
                                                            it.temperature.humidity
                                                    )
                                            )
                                            .create()
                                            .show()
                                },
                                {
                                    Log.e(TAG, "Error getting weather", it)
                                    Toast.makeText(this@MainActivity, "Erro ao processar", Toast.LENGTH_LONG).show()
                                }
                        )
        )
    }

    override fun onStop() {
        super.onStop()

        subscriptions?.unsubscribe()
        subscriptions = null
    }
}
