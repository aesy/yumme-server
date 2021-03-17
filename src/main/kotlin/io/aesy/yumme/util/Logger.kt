package io.aesy.yumme.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
inline fun <reified T> T.getLogger(): Logger {
    if (T::class.isCompanion) {
        return LoggerFactory.getLogger(T::class.java.enclosingClass)
    }

    return LoggerFactory.getLogger(T::class.java)
}
