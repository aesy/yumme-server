package io.aesy.yumme.auth

import io.aesy.yumme.entity.Role
import io.aesy.yumme.entity.User
import org.apache.shiro.authc.*
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.authz.SimpleAuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.springframework.beans.factory.annotation.Value

class JwtRealm(
    private val jwtService: JwtService
): AuthorizingRealm() {
    @Value("\${yumme.security.realm}")
    private lateinit var realm: String

    override fun supports(token: AuthenticationToken?): Boolean {
        return token is JwtToken
    }

    override fun doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo? {
        if (principals.isEmpty) {
            return null
        }

        val principal = principals.primaryPrincipal

        if (principal !is User) {
            return null
        }

        val authInfo = SimpleAuthorizationInfo()
        val roles = principal.roles.map(Role::name)
        authInfo.addRoles(roles)

        return authInfo
    }

    @Throws(AuthenticationException::class)
    override fun doGetAuthenticationInfo(auth: AuthenticationToken): AuthenticationInfo? {
        val token = auth.credentials as String

        return jwtService.getUserByToken(token)
            .filter { jwtService.validateToken(token, it) }
            .map { SimpleAuthenticationInfo(it, token, realm) }
            .orElse(null)
    }
}
