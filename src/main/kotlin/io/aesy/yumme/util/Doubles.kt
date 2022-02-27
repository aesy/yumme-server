package io.aesy.yumme.util

import kotlin.math.pow
import kotlin.math.roundToInt

object Doubles {
    fun Double.roundToDouble(precision: Int = 0): Double {
        val pow = 10.0.pow(precision)

        return if (isFinite()) {
            return (this * pow).roundToInt() / pow
        } else {
            Double.NaN
        }
    }
}
