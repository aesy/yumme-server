package io.aesy.food.auth

import io.aesy.food.service.UserService
import org.apache.shiro.authc.*
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.authz.SimpleAuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PasswordRealm(
    private val userService: UserService
): AuthorizingRealm() {
    companion object {
        private val logger = LoggerFactory.getLogger(PasswordRealm::class.java)
    }

    @Value("\${spring.security.jwt.token.realm}")
    private lateinit var realm: String

    override fun supports(token: AuthenticationToken?): Boolean {
        return token is UsernamePasswordToken
    }

    override fun doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo {
        val authInfo = SimpleAuthorizationInfo()
        authInfo.addRole("ROLE_ONE") // TODO
        val permissions = setOf("PERMISSION_ONE", "PERMISSION_TWO")
        authInfo.addStringPermissions(permissions)

        return authInfo
    }

    @Throws(AuthenticationException::class)
    override fun doGetAuthenticationInfo(auth: AuthenticationToken): AuthenticationInfo {
        val email = auth.principal as String
        val password = auth.credentials as String
        val user = userService.getByEmailAndPassword(email, password)
            .orElseThrow { AuthenticationException() }

        return SimpleAuthenticationInfo(user, password, realm)
    }
}
