package io.aesy.food.dto

import kotlin.reflect.KClass


@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Dto(
    val type: KClass<*>
)
