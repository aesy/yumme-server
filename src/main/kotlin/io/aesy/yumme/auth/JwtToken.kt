package io.aesy.yumme.auth

import io.aesy.yumme.entity.User
import org.apache.shiro.authc.AuthenticationToken

class JwtToken(
    private val user: User,
    private val accessToken: String
): AuthenticationToken {
    override fun getPrincipal(): Any {
        return user
    }

    override fun getCredentials(): Any {
        return accessToken
    }
}
