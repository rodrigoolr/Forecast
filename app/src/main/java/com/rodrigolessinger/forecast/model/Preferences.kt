package com.rodrigolessinger.forecast.model

import io.realm.RealmObject

/**
 * Created by Rodrigo on 20/06/2016.
 */
open class Preferences(
        hasObtainedInitialCities: Boolean = false
) : RealmObject() {

    var hasObtainedInitialCities: Boolean = hasObtainedInitialCities
        protected set
}