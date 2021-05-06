package io.aesy.yumme.util

import kotlin.random.Random

object Strings {
    private val RANDOM_CHARS: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun random(count: Int): String {
        return (0 until count)
            .map { Random.nextInt(0, RANDOM_CHARS.size) }
            .map(RANDOM_CHARS::get)
            .joinToString("")
    }
}
