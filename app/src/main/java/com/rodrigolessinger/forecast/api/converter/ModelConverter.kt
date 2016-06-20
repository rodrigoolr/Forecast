package com.rodrigolessinger.forecast.api.converter

/**
 * Defines a contract that converts an API Model into an App model
 * For convenience use {@link Observable#convert}
 */
interface ModelConverter<T, V> {
    fun convert(obj: T): V
}