package io.aesy.yumme.auth

import io.aesy.yumme.logging.Logging.getLogger
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.web.filter.authc.AuthenticatingFilter
import org.apache.shiro.web.util.WebUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse

@Component
class BearerAuthFilter: AuthenticatingFilter() {
    @Value("\${yumme.security.realm}")
    private lateinit var realm: String

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val AUTHENTICATE_HEADER = "WWW-Authenticate"
        private const val AUTHC_SCHEME = "BEARER"
        private const val AUTHZ_SCHEME = "BEARER"

        private val logger = getLogger()
    }

    override fun isAccessAllowed(request: ServletRequest, response: ServletResponse, mappedValue: Any?): Boolean {
        @Suppress("UNCHECKED_CAST")
        val methods = httpMethodsFromOptions(mappedValue as Array<String>?)
        val httpRequest = WebUtils.toHttp(request)
        val httpMethod = httpRequest.method
        var authcRequired = methods.isEmpty()

        for (method in methods) {
            if (httpMethod.uppercase(Locale.ENGLISH) == method) {
                authcRequired = true
                break
            }
        }

        if (authcRequired) {
            return super.isAccessAllowed(request, response, mappedValue)
        }

        return true
    }

    private fun httpMethodsFromOptions(options: Array<String>?): Set<String> {
        val methods = HashSet<String>()

        if (options != null) {
            for (option in options) {
                if (!option.equals(PERMISSIVE, ignoreCase = true)) {
                    methods.add(option.uppercase(Locale.ENGLISH))
                }
            }
        }

        return methods
    }

    @Throws(Exception::class)
    override fun onAccessDenied(request: ServletRequest, response: ServletResponse): Boolean {
        var loggedIn = false

        if (isLoginAttempt(request, response)) {
            loggedIn = executeLogin(request, response)
        }

        if (!loggedIn) {
            sendChallenge(request, response)
        }

        return loggedIn
    }

    override fun isLoginRequest(request: ServletRequest, response: ServletResponse): Boolean {
        val authzHeader = getAuthzHeader(request)

        return isLoginAttempt(authzHeader)
    }

    protected fun getAuthzHeader(request: ServletRequest): String? {
        val httpRequest = WebUtils.toHttp(request)

        return httpRequest.getHeader(AUTHORIZATION_HEADER)
    }

    protected fun isLoginAttempt(request: ServletRequest, response: ServletResponse): Boolean {
        val authzHeader = getAuthzHeader(request)

        return isLoginAttempt(authzHeader)
    }

    protected fun isLoginAttempt(authzHeader: String?): Boolean {
        val authzScheme = AUTHZ_SCHEME.lowercase(Locale.ENGLISH)

        if (authzHeader == null) {
            return false
        }

        return authzHeader.lowercase(Locale.ENGLISH)
            .startsWith(authzScheme)
    }

    protected fun sendChallenge(request: ServletRequest, response: ServletResponse): Boolean {
        logger.debug("Authentication required: sending 401 Authentication challenge response.")

        val httpResponse = WebUtils.toHttp(response)
        val authcHeader = "$AUTHC_SCHEME realm=\"$realm\""

        httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
        httpResponse.setHeader(AUTHENTICATE_HEADER, authcHeader)

        return false
    }

    @Throws(Exception::class)
    override fun createToken(request: ServletRequest, response: ServletResponse): AuthenticationToken {
        val authorizationHeader = getAuthzHeader(request)

        if (authorizationHeader.isNullOrEmpty()) {
            return createToken("", "", request, response)
        }

        logger.debug("Attempting to execute login with auth header")

        val token = getToken(authorizationHeader, request)

        return JwtToken(token)
    }

    protected fun getToken(authzHeader: String, request: ServletRequest): String {
        val authzScheme = AUTHZ_SCHEME.lowercase(Locale.ENGLISH)

        return authzHeader
            .substring(authzScheme.length)
            .trim()
    }
}
