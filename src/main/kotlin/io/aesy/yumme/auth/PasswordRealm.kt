package io.aesy.yumme.auth

import io.aesy.yumme.entity.*
import io.aesy.yumme.service.UserService
import org.apache.shiro.authc.*
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.authz.SimpleAuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.springframework.beans.factory.annotation.Value

class PasswordRealm(
    private val userService: UserService
): AuthorizingRealm() {
    @Value("\${spring.security.jwt.token.realm}")
    private lateinit var realm: String

    override fun supports(token: AuthenticationToken?): Boolean {
        return token is UsernamePasswordToken
    }

    override fun doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo? {
        if (principals.isEmpty) {
            return null
        }

        val principal = principals.primaryPrincipal

        if (principal !is User) {
            return null
        }

        // TODO get roles and permissions from database
        val authInfo = SimpleAuthorizationInfo()
        authInfo.addRoles(setOf(Role.USER))
        val permissions = setOf(Permission.READ_OWN_USER, Permission.WRITE_OWN_USER)
        authInfo.addStringPermissions(permissions)

        return authInfo
    }

    @Throws(AuthenticationException::class)
    override fun doGetAuthenticationInfo(auth: AuthenticationToken): AuthenticationInfo? {
        val token = auth as UsernamePasswordToken
        val name = token.username
        val password = String(token.password)

        return userService.getByUserNameAndPassword(name, password)
            .map { SimpleAuthenticationInfo(it, password, realm) }
            .orElse(null)
    }
}
