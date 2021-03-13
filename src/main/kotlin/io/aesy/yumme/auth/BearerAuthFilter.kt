package io.aesy.yumme.auth

import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.web.filter.AccessControlFilter
import org.apache.shiro.web.util.WebUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse

@Component
class BearerAuthFilter: AccessControlFilter() {
    companion object {
        private val logger = LoggerFactory.getLogger(BearerAuthFilter::class.java)
    }

    @Value("\${spring.security.jwt.token.realm}")
    private lateinit var realm: String

    override fun isAccessAllowed(
        request: ServletRequest,
        response: ServletResponse,
        mappedValue: Any?
    ): Boolean {
        if (!isLoginRequest(request, response)) {
            return false
        }

        return executeLogin(request, response)
    }

    override fun isLoginRequest(
        request: ServletRequest,
        response: ServletResponse
    ): Boolean {
        val httpRequest = WebUtils.toHttp(request)

        val auth = httpRequest.getHeader("Authorization")
            ?: return false

        return auth.startsWith("Bearer ")
    }

    override fun onAccessDenied(
        request: ServletRequest,
        response: ServletResponse
    ): Boolean {
        val httpResponse = WebUtils.toHttp(response)
        httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
        httpResponse.setHeader("WWW-Authenticate", "Bearer realm=\"$realm\"")

        return false
    }

    private fun executeLogin(
        request: ServletRequest,
        response: ServletResponse
    ): Boolean {
        val httpRequest = WebUtils.toHttp(request)
        val accessToken = httpRequest.getHeader("Authorization")
            .removePrefix("Bearer ")

        try {
            getSubject(request, response)
                .login(JwtToken(accessToken))
        } catch (e: AuthenticationException) {
            return false
        }

        return true
    }
}
