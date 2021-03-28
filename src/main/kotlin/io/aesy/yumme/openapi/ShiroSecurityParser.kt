package io.aesy.yumme.openapi

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.apache.shiro.authz.annotation.*
import org.springdoc.core.SecurityParser
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.web.method.HandlerMethod
import java.util.*
import kotlin.reflect.KParameter
import kotlin.reflect.full.findParameterByName

class ShiroSecurityParser: SecurityParser() {
    override fun getSecurityRequirements(handler: HandlerMethod): Optional<Array<SecurityRequirement>> {
        val method = handler.method
        val authentication = AnnotationUtils.findAnnotation(method, RequiresAuthentication::class.java)
        val user = AnnotationUtils.findAnnotation(method, RequiresUser::class.java)
        val roles = AnnotationUtils.findAnnotation(method, RequiresRoles::class.java)
        val permissions = AnnotationUtils.findAnnotation(method, RequiresPermissions::class.java)

        if (arrayOf(authentication, user, roles, permissions).any { it != null }) {
            val scopes = mutableListOf<String>()

            if (roles != null) {
                scopes.addAll(roles.value)
            }

            if (permissions != null) {
                scopes.addAll(permissions.value)
            }

            val requirements = arrayOf(
                constructRequirement(SecuritySchemeName.BASIC),
                constructRequirement(SecuritySchemeName.OAUTH2, scopes.toTypedArray())
            )

            return super.getSecurityRequirements(handler)
                .map { it + requirements }
                .or { Optional.of(requirements) }
        }

        return super.getSecurityRequirements(handler)
    }

    private fun constructRequirement(name: String, scopes: Array<String> = arrayOf()): SecurityRequirement {
        val constructor = SecurityRequirement::class.constructors.first()

        val parameters = mutableMapOf<KParameter, Any>()
        parameters[constructor.findParameterByName("name")!!] = name
        parameters[constructor.findParameterByName("scopes")!!] = scopes

        return constructor.callBy(parameters)
    }
}
