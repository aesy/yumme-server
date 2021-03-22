package io.aesy.yumme.util

import kotlin.math.*

object Doubles {
    fun Double.round(precision: Int): Double {
        val pow = 10.0.pow(precision)

        return if (isFinite()) {
            return (this * pow).roundToInt() / pow
        } else {
            Double.NaN
        }
    }
}
