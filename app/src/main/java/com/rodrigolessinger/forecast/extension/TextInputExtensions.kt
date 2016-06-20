package com.rodrigolessinger.forecast.extension

import android.support.annotation.StringRes
import android.support.design.widget.TextInputLayout

/**
 * Created by Rodrigo on 20/06/2016.
 */
fun TextInputLayout.clearError() {
    this.error = null
    this.isErrorEnabled = false
}

fun TextInputLayout.setError(@StringRes errorId: Int) {
    this.error = this.context.getString(errorId)
}