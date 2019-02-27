package io.aesy.food.conversion

import org.springframework.web.bind.annotation.ResponseBody
import kotlin.reflect.KClass

@ResponseBody
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ResponseBodyType(
    val type: KClass<*>
)
