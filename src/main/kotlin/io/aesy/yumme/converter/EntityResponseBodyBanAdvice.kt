package io.aesy.yumme.converter

import io.aesy.yumme.util.Logging.getLogger
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.Order
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice
import javax.persistence.Entity

@ControllerAdvice
@Order(Int.MAX_VALUE)
class EntityResponseBodyBanAdvice: AbstractMappingJacksonResponseBodyAdvice() {
    companion object {
        private val logger = getLogger()
    }

    override fun beforeBodyWriteInternal(
        bodyContainer: MappingJacksonValue,
        contentType: MediaType,
        returnType: MethodParameter,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ) {
        val value = bodyContainer.value

        val hasEntity = when (value) {
            is Page<*> -> value.any {
                it::class.java.isAnnotationPresent(Entity::class.java)
            }
            is Collection<*> -> value.stream().anyMatch {
                if (it == null) {
                    return@anyMatch false
                }

                it::class.java.isAnnotationPresent(Entity::class.java)
            }
            else -> bodyContainer.value::class.java.isAnnotationPresent(Entity::class.java)
        }

        if (!hasEntity) {
            return
        }

        logger.error(
            "A response containing a @Entity object detected. " +
            "The response has been set to status 500. " +
            "Use a custom DTO object to avoid leaking data by accident.")
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
        bodyContainer.value = Unit
    }
}
