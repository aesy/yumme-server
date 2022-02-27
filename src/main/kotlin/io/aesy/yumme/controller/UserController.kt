package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.dto.RegisterRequest
import io.aesy.yumme.dto.UserDto
import io.aesy.yumme.entity.Role
import io.aesy.yumme.entity.User
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.mapper.UserMapper
import io.aesy.yumme.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.transaction.Transactional
import javax.validation.Valid

@Tag(name = "User")
@RestController
@RequestMapping(
    "user",
    consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Validated
class UserController(
    private val users: UserService,
    private val mapper: UserMapper
) {
    @Value("\${yumme.registration.enabled}")
    val registrationEnabled: Boolean = false

    @RequiresAuthentication
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun inspectSelf(
        @AuthorizedUser user: User
    ): UserDto {
        return mapper.toDto(user)
    }

    @RequiresAuthentication
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun inspectUserById(
        @PathVariable("id") id: Long,
    ): UserDto {
        val user = users.getById(id)
            .orElseThrow { ResourceNotFound() }

        return mapper.toDto(user)
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun registerUser(
        @Valid @RequestBody request: RegisterRequest
    ) {
        if (!registrationEnabled) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Registration is disabled")
        }

        val userName = request.userName!!
        val displayName = request.displayName!!
        val password = request.password!!

        users.register(userName, displayName, password)
            .also { users.addRoleToUser(it, Role.USER) }
    }
}
