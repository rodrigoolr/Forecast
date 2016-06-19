package com.rodrigolessinger.forecast.api.client

import android.content.res.Resources
import com.rodrigolessinger.forecast.BuildConfig
import com.rodrigolessinger.forecast.R
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rodrigo on 19/06/2016.
 */
@Singleton
class WeatherClient @Inject constructor(
        private val resources: Resources
) {

    private val API_KEY_PARAMETER = "appid"
    private val UNITS_PARAMETER = "units"
    private val METRIC_SYSTEM = "metric"

    private val apiKey = resources.getString(R.string.open_weather_key)

    private val builder: Retrofit
        get() {
            val client = OkHttpClient.Builder()

            client.interceptors().add(Interceptor { chain ->
                val original = chain.request();
                val originalUrl = original.url()

                val url = originalUrl.newBuilder()
                        .addQueryParameter(API_KEY_PARAMETER, apiKey)
                        .addQueryParameter(UNITS_PARAMETER, METRIC_SYSTEM)
                        .build()

                val requestBuilder = original.newBuilder().url(url)
                val request = requestBuilder.build()

                chain.proceed(request)
            })

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            client.interceptors().add(logging)

            return Retrofit.Builder()
                    .baseUrl(BuildConfig.API_URI)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()
        }

    fun <T> createService(type: Class<T>): T {
        return builder.create(type)
    }

}