package com.rodrigolessinger.forecast.api.exception

/**
 * Error when the API uses a valid response code (Http 200) but returns a Not Found (Http 404) message
 */
class InvalidModelException(message: String) : Exception(message)
