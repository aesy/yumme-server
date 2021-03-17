package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.auth.JwtService
import io.aesy.yumme.conversion.ResponseBodyType
import io.aesy.yumme.dto.UserDto
import io.aesy.yumme.entity.User
import io.aesy.yumme.exception.UserAlreadyPresent
import io.aesy.yumme.request.RegisterRequest
import io.aesy.yumme.response.RegisterResponse
import io.aesy.yumme.service.UserService
import io.aesy.yumme.util.getLogger
import org.apache.shiro.authz.annotation.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@RequestMapping("user")
class UserController(
    private val userService: UserService,
    private val authService: JwtService
) {
    companion object {
        private val logger = getLogger()
    }

    @RequiresAuthentication
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    @ResponseBodyType(type = UserDto::class)
    fun me(@AuthorizedUser user: User): User {
        return user
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): RegisterResponse {
        val email = request.email!!
        val password = request.password!!

        if (userService.getByEmail(email).isPresent) {
            throw UserAlreadyPresent()
        }

        val user = userService.save(User(email = email, password = password))
        val accessToken = authService.createToken(user)

        return RegisterResponse(accessToken, "")
    }
}
