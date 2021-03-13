package io.aesy.yumme.auth

import org.apache.shiro.authc.AuthenticationToken

class JwtToken(
    private val token: String
): AuthenticationToken {
    override fun getPrincipal(): Any {
        return token
    }

    override fun getCredentials(): Any {
        return token
    }
}
