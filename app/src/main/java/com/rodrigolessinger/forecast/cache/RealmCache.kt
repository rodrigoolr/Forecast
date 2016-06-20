package com.rodrigolessinger.forecast.cache

import android.os.Handler
import android.os.HandlerThread
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import rx.Observable
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by Rodrigo on 14/04/2016.
 */
open class RealmCache<T: RealmObject>() {

    protected fun getRealm(): Realm {
        return Realm.getDefaultInstance()
    }

    protected fun executeTransaction(execute: (Realm) -> Unit) {
        val realm = getRealm()
        realm.executeTransaction { execute(realm) }
        realm.close()
    }

    private var realmThread: HandlerThread
    private var handler: Handler
    private var scheduler: Scheduler

    init {
        realmThread = HandlerThread(this.javaClass.name)
        realmThread.start()

        val looper = realmThread.looper
        handler = Handler(looper)
        scheduler = AndroidSchedulers.from(looper)
    }

    protected final fun getFindAllObservable(query: (Realm) -> RealmQuery<T>): Observable<List<T>?> {
        val realmRef = AtomicReference<Realm?>(null);

        return Observable.defer {
            val realm = getRealm()
            realmRef.set(realm)

            query(realm)
                    .findAll()
                    .asObservable()
                    .filter { it?.isLoaded ?: true }
                    .map { if (it != null) realmRef.get()?.copyFromRealm(it) else null }
                    .subscribeOn(scheduler)
        }
                .doOnUnsubscribe { realmRef.get()?.close() }
                .unsubscribeOn(scheduler)
                .subscribeOn(scheduler)
    }

    fun add(obj: T) {
        handler.post {
            executeTransaction { realm ->
                realm.copyToRealmOrUpdate(obj)
            }
        }
    }

    fun update(obj: T) {
        handler.post {
            executeTransaction { realm ->
                realm.copyToRealmOrUpdate(obj)
            }
        }
    }

    protected fun removeRealmObject(obj: T) {
        executeTransaction {
            obj.deleteFromRealm()
        }
    }
}