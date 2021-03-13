package io.aesy.yumme.auth

import org.apache.shiro.authc.*
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.authz.SimpleAuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtRealm(
    private val jwtService: JwtService
): AuthorizingRealm() {
    companion object {
        private val logger = LoggerFactory.getLogger(JwtRealm::class.java)
    }

    @Value("\${spring.security.jwt.token.realm}")
    private lateinit var realm: String

    override fun supports(token: AuthenticationToken?): Boolean {
        return token is JwtToken
    }

    override fun doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo {
        val user = jwtService.getUser(principals.toString()).get()
        val authInfo = SimpleAuthorizationInfo()
        authInfo.addRole("ROLE_ONE") // TODO
        val permissions = setOf("PERMISSION_ONE", "PERMISSION_TWO")
        authInfo.addStringPermissions(permissions)

        return authInfo
    }

    @Throws(AuthenticationException::class)
    override fun doGetAuthenticationInfo(auth: AuthenticationToken): AuthenticationInfo {
        val accessToken = auth.credentials as String
        val user = jwtService.getUser(accessToken)
            .orElseThrow { AuthenticationException() }

        if (!jwtService.validateToken(accessToken, user)) {
            throw AuthenticationException()
        }

        return SimpleAuthenticationInfo(user, accessToken, realm)
    }
}
