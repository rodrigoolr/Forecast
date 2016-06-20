package com.rodrigolessinger.forecast.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.rodrigolessinger.forecast.R

class ForecastActivity : BaseActivity() {

    companion object {

        private val TAG = "FORECAST_ACTIVITY"

        private val ARG_CITY_ID = "ARG_CITY_ID"

        fun startActivity(activity: Activity, cityId: String) {
            val intent = Intent(activity, ForecastActivity::class.java)
            intent.putExtra(ARG_CITY_ID, cityId)

            activity.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)
        setSupportActionBar(toolbar)

        component = ActivityComponent.Initializer.init(this)
        component.inject(this)
    }

}
