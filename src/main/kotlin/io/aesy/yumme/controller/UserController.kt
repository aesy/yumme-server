package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.dto.RegisterRequest
import io.aesy.yumme.dto.UserDto
import io.aesy.yumme.entity.Role
import io.aesy.yumme.entity.User
import io.aesy.yumme.mapper.UserMapper
import io.aesy.yumme.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresGuest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
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
    @RequiresAuthentication
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun inspectSelf(
        @AuthorizedUser user: User
    ): UserDto {
        return mapper.toDto(user)
    }

    @RequiresGuest
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun registerUser(
        @Valid @RequestBody request: RegisterRequest
    ) {
        val userName = request.userName!!
        val displayName = request.displayName!!
        val password = request.password!!

        users.register(userName, displayName, password).also { user ->
            users.addRoleToUser(user, Role.USER)
        }
    }
}
