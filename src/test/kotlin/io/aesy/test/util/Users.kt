package io.aesy.test.util

import io.aesy.yumme.entity.Role
import io.aesy.yumme.entity.User
import io.aesy.yumme.service.UserService
import io.aesy.yumme.util.Strings

object Users {
    fun random(): User = User(
        userName = Strings.random(30),
        displayName = "woop",
        passwordHash = "secret123"
    )

    fun UserService.createUser(userName: String, displayName: String, password: String): User {
        return register(userName, displayName, password)
            .also { addRoleToUser(it, Role.USER) }
    }

    fun UserService.createAdmin(username: String, displayName: String, password: String): User {
        return register(username, displayName, password)
            .also { addRoleToUser(it, Role.ADMIN) }
    }
}
