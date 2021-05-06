package io.aesy.yumme.util

import org.springframework.core.ParameterizedTypeReference

object Types {
    inline fun <reified T> listOf(): ParameterizedTypeReference<List<T>> =
        object: ParameterizedTypeReference<List<T>>() {}
}
