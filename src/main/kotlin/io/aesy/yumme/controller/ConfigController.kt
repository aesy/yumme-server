package io.aesy.yumme.controller

import io.aesy.yumme.dto.ConfigDto
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@Tag(name = "Config")
@RestController
@RequestMapping(
    "config",
    consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Validated
class ConfigController {
    @Value("\${yumme.registration.enabled}")
    private val registrationEnabled: Boolean = false

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun inspectConfig(): ConfigDto {
        return ConfigDto(registrationEnabled)
    }
}
