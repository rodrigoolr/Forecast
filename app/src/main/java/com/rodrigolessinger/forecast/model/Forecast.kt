package com.rodrigolessinger.forecast.model

import io.realm.RealmObject
import java.util.*

/**
 * Created by Rodrigo on 20/06/2016.
 */
open class Forecast(
        date: Date = Date(),
        temperature: Int = 0
) : RealmObject() {

    var date: Date = date
        protected set

    var temperature: Int = temperature
        protected set
}