package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.conversion.ResponseBodyType
import io.aesy.yumme.dto.UserDto
import io.aesy.yumme.entity.Permission.Companion.READ_OWN_USER
import io.aesy.yumme.entity.User
import io.aesy.yumme.exception.UserAlreadyPresent
import io.aesy.yumme.request.LoginRequest
import io.aesy.yumme.request.RegisterRequest
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
    private val authController: AuthController
) {
    companion object {
        private val logger = getLogger()
    }

    @RequiresAuthentication
    @RequiresPermissions(READ_OWN_USER)
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
    ): String {
        val email = request.email!!
        val password = request.password!!

        if (userService.getByEmail(email).isPresent) {
            throw UserAlreadyPresent()
        }

        userService.save(User(email = email, password = password))

        return authController.getAccessToken(LoginRequest(email = email, password = password))
    }
}
