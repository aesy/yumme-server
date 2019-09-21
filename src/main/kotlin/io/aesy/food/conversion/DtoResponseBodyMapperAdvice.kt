package io.aesy.food.conversion

import org.modelmapper.ModelMapper
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.Order
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice

@ControllerAdvice
@Order(Int.MAX_VALUE - 1)
class DtoResponseBodyMapperAdvice(
    private val mapper: ModelMapper
): AbstractMappingJacksonResponseBodyAdvice() {
    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean {
        if (!super.supports(returnType, converterType)) {
            return false
        }

        return returnType.hasMethodAnnotation(ResponseBodyType::class.java)
    }

    override fun beforeBodyWriteInternal(
        bodyContainer: MappingJacksonValue,
        contentType: MediaType,
        returnType: MethodParameter,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ) {
        val value = bodyContainer.value
        val annotation = returnType.getMethodAnnotation(ResponseBodyType::class.java)
        val type = annotation?.type?.java
                   ?: return

        bodyContainer.value = when (value) {
            is Page<*> -> value.map {
                mapper.map(it, type)
            }
            is Collection<*> -> value.map {
                mapper.map(it, type)
            }
            else -> mapper.map(value, type)
        }
    }
}
