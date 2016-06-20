package com.rodrigolessinger.forecast.extension

import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by Rodrigo on 20/06/2016.
 */
fun <T: RealmObject> Collection<T>.toRealmList(): RealmList<T> {
    val realmList = RealmList<T>()
    realmList.addAll(this)
    return realmList
}