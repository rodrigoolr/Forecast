package com.rodrigolessinger.forecast.activity

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.rodrigolessinger.forecast.R
import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
 * Created by Rodrigo on 19/06/2016.
 */
abstract class BaseActivity: AppCompatActivity() {

    lateinit var component: ActivityComponent
        protected set

    protected val toolbar by lazy { findViewById(R.id.toolbar) as Toolbar }

    private var subscriptions: CompositeSubscription? = null

    open fun onSubscribable() { }

    fun addSubscription(subscription: Subscription) {
        subscriptions?.add(subscription)
    }

    override fun onStart() {
        super.onStart()

        subscriptions = CompositeSubscription()
        onSubscribable()
    }

    protected fun unsubscribe() {
        subscriptions?.unsubscribe()
        subscriptions = null
    }

    override fun onStop() {
        super.onStop()
        unsubscribe()
    }

}