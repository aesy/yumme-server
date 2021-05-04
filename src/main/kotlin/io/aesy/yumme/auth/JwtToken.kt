package io.aesy.yumme.auth

import org.apache.shiro.authc.AuthenticationToken

class JwtToken(
    private val value: String
): AuthenticationToken {
    override fun getPrincipal(): Any? {
        return null
    }

    override fun getCredentials(): Any {
        return value
    }
}
