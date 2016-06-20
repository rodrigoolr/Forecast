package com.rodrigolessinger.forecast.api.converter

/**
 * Created by Rodrigo on 20/06/2016.
 */
interface ModelConverter<T, V> {
    fun convert(obj: T): V
}