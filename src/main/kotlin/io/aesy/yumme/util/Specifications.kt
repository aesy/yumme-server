package io.aesy.yumme.util

import org.springframework.data.jpa.domain.Specification

object Specifications {
    /**
     * Returns a specification that is always true.
     */
    fun <T> default(): Specification<T> {
        return Specification<T> { _, _, builder -> builder.conjunction() }
    }
}
