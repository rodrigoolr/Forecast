package com.rodrigolessinger.forecast.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.rodrigolessinger.forecast.R

class ForecastActivity : AppCompatActivity() {

    companion object {

        private val TAG = "MAIN_ACTIVITY"

        private val ARG_CITY_ID = "ARG_CITY_ID"

        fun startActivity(activity: Activity, cityId: String) {
            val intent = Intent(activity, ForecastActivity::class.java)
            intent.putExtra(ARG_CITY_ID, cityId)

            activity.startActivity(intent)
        }

    }

    private lateinit var component: ActivityComponent

    private val toolbar by lazy { findViewById(R.id.toolbar) as Toolbar }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)
        setSupportActionBar(toolbar)

        component = ActivityComponent.Initializer.init(this)
        component.inject(this)
    }

}
