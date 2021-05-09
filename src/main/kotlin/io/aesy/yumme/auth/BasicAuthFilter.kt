package io.aesy.yumme.auth

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class BasicAuthFilter: BasicHttpAuthenticationFilter() {
    @Value("\${yumme.security.realm}")
    private lateinit var realm: String

    override fun getApplicationName(): String {
        return realm
    }
}
