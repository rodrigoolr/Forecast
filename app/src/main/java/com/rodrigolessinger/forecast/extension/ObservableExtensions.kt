package com.rodrigolessinger.forecast.extension

import com.rodrigolessinger.forecast.api.converter.ModelConverter
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by Rodrigo on 19/06/2016.
 */
fun <T> Observable<T>.observeOnMainThread(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.observeOnIo(): Observable<T> {
    return this.observeOn(Schedulers.io())
}

fun <T> Observable<T>.subscribeOnIo(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
}

fun <T> Observable<T?>.filterNotNull(): Observable<T> {
    return this.filter { it != null }.map { it!! }
}

fun <T, V> Observable<T>.convert(converter: ModelConverter<T, V>): Observable<V> {
    return this.map { converter.convert(it) }
}

fun <T> Observable<T>.debounceIf(condition: (T) -> Boolean, delay: Long, unit: TimeUnit): Observable<T> {
    return this.debounce { obj ->
        if (condition(obj)) Observable.timer(delay, unit)
        else Observable.just(0L)
    }
}

fun Observable<Boolean>.debounceIfTrue(delay: Long, unit: TimeUnit): Observable<Boolean> {
    return this.debounceIf({ boolObj -> boolObj }, delay, unit)
}

fun Observable<Boolean>.debounceIfFalse(delay: Long, unit: TimeUnit): Observable<Boolean> {
    return this.debounceIf({ boolObj -> ! boolObj }, delay, unit)
}

private abstract class OneShotSubscriber<T>() : Subscriber<T>() {

    abstract fun onErrorReceived(ex: Throwable?)
    abstract fun onNextReceived(obj: T)

    override final fun onError(ex: Throwable?) {
        if (!isUnsubscribed) {
            unsubscribe()
            onErrorReceived(ex)
        }
    }

    override final fun onNext(obj: T) {
        if (!isUnsubscribed) {
            unsubscribe()
            onNextReceived(obj)
        }
    }

    override final fun onCompleted() {
        if (!isUnsubscribed) unsubscribe()
    }
}

fun <T> Observable<T>.subscribeOnce(onNext: (T) -> Unit, onError: ((Throwable?) -> Unit)?): Subscription {
    return this.subscribe(object : OneShotSubscriber<T>() {

        override fun onErrorReceived(ex: Throwable?) {
            if (onError != null) onError(ex)
        }

        override fun onNextReceived(obj: T) {
            onNext(obj)
        }
    })
}

fun <T> Observable<T>.subscribeOnce(onNext: (T) -> Unit): Subscription {
    return this.subscribeOnce(onNext, null);
}
