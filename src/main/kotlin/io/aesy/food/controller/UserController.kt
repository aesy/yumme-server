package io.aesy.food.controller

import io.aesy.food.auth.AuthorizedUser
import io.aesy.food.auth.JwtService
import io.aesy.food.conversion.ResponseBodyType
import io.aesy.food.dto.UserDto
import io.aesy.food.entity.User
import io.aesy.food.exception.UserAlreadyPresent
import io.aesy.food.request.RegisterRequest
import io.aesy.food.response.RegisterResponse
import io.aesy.food.service.UserService
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.slf4j.LoggerFactory
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
        private val logger = LoggerFactory.getLogger(UserController::class.java)
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
