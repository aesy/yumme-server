package io.aesy.yumme.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Logging {
    @Suppress("unused")
    inline fun <reified T> T.getLogger(): Logger {
        if (T::class.isCompanion) {
            return LoggerFactory.getLogger(T::class.java.enclosingClass)
        }

        return LoggerFactory.getLogger(T::class.java)
    }
}
