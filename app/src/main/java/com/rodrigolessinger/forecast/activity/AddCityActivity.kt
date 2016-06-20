package com.rodrigolessinger.forecast.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.jakewharton.rxbinding.widget.textChanges
import com.rodrigolessinger.forecast.R
import com.rodrigolessinger.forecast.extension.clearError
import com.rodrigolessinger.forecast.extension.debounceIfFalse
import com.rodrigolessinger.forecast.extension.observeOnMainThread
import com.rodrigolessinger.forecast.extension.setError
import com.rodrigolessinger.forecast.model.CityWeather
import com.rodrigolessinger.forecast.repository.WeatherRepository
import rx.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddCityActivity : BaseActivity() {

    companion object {

        private val TAG = "ADD_CITY_ACTIVITY"

        fun startActivity(activity: Activity) {
            val intent = Intent(activity, AddCityActivity::class.java)
            activity.startActivity(intent)
        }

    }

    @Inject protected lateinit var repository: WeatherRepository

    private val cityNameLayout by lazy { findViewById(R.id.city_name_layout) as TextInputLayout }
    private val cityNameView by lazy { findViewById(R.id.city_name) as EditText }
    private val cityNameObservable: Observable<Boolean>
        get() = cityNameView.textChanges()
                .map { it.length >= 3 }

    private val countryNameLayout by lazy { findViewById(R.id.country_name_layout) as TextInputLayout }
    private val countryNameView by lazy { findViewById(R.id.country_name) as EditText }
    private val countryNameObservable: Observable<Boolean>
        get() = countryNameView.textChanges()
                .map { it.length == 0 || it.length >= 2 }

    private val searchButton by lazy { findViewById(R.id.search_button) as Button }

    private fun onObtained(cityWeather: CityWeather) {
        var message = getString(R.string.add_city_message)
        message = message.replace("{city}", cityWeather.cityName)
        message = message.replace("{country}", cityWeather.countryName)

        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.add_city_confirm) { dialog, which ->
                    finish()
                }
                .setNegativeButton(R.string.add_city_cancel) { dialog, which ->
                    repository.remove(cityWeather)
                }
                .create()
                .show()
    }

    private fun onError() {
        Toast.makeText(this, R.string.error_adding_city, Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_city)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        component = ActivityComponent.Initializer.init(this)
        component.inject(this)

        searchButton.setOnClickListener {
            val cityName = cityNameView.text.toString()
            val countryName = countryNameView.text.toString()

            var query = cityName
            if (countryName.length > 0) query += "," + countryName
            repository.add(
                    cityName = query,
                    onSuccess = { runOnUiThread { onObtained(it) } },
                    onError = { runOnUiThread { onError() } }
            )
        }
    }

    override fun onSubscribable() {
        addSubscription(
                cityNameObservable
                        .skipWhile { ! it }
                        .debounceIfFalse(2, TimeUnit.SECONDS)
                        .distinctUntilChanged()
                        .observeOnMainThread()
                        .subscribe { isValid ->
                            if (isValid) cityNameLayout.clearError()
                            else cityNameLayout.setError(R.string.invalid_city_name)
                        }
        )

        addSubscription(
                countryNameObservable
                        .skipWhile { ! it }
                        .debounceIfFalse(2, TimeUnit.SECONDS)
                        .distinctUntilChanged()
                        .observeOnMainThread()
                        .subscribe { isValid ->
                            if (isValid) countryNameLayout.clearError()
                            else countryNameLayout.setError(R.string.invalid_country_name)
                        }
        )

        addSubscription(
                Observable.combineLatest(
                        cityNameObservable,
                        countryNameObservable,
                        { cityNameValid, countryNameValid -> cityNameValid && countryNameValid }
                ).subscribe { searchButton.isEnabled = it }
        )
    }
}
