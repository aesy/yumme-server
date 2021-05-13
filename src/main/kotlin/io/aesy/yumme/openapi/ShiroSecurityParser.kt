package io.aesy.yumme.openapi

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.apache.shiro.authz.annotation.*
import org.springdoc.core.PropertyResolverUtils
import org.springdoc.core.SecurityService
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Service
import org.springframework.web.method.HandlerMethod
import kotlin.reflect.KParameter
import kotlin.reflect.full.findParameterByName

@Service
class ShiroSecurityParser(
    propertyResolverUtils: PropertyResolverUtils
): SecurityService(propertyResolverUtils) {
    override fun getSecurityRequirements(handler: HandlerMethod): Array<SecurityRequirement> {
        val method = handler.method
        val authentication = AnnotationUtils.findAnnotation(method, RequiresAuthentication::class.java)
        val user = AnnotationUtils.findAnnotation(method, RequiresUser::class.java)
        val roles = AnnotationUtils.findAnnotation(method, RequiresRoles::class.java)
        val permissions = AnnotationUtils.findAnnotation(method, RequiresPermissions::class.java)
        val normalRequirements = super.getSecurityRequirements(handler)
            ?: emptyArray<SecurityRequirement>()

        if (arrayOf(authentication, user, roles, permissions).any { it != null }) {
            val scopes = mutableListOf<String>()

            if (roles != null) {
                scopes.addAll(roles.value.map { "role:$it" })
            }

            if (permissions != null) {
                scopes.addAll(permissions.value.map { "permission:$it" })
            }

            val shiroRequirements = arrayOf(
                constructRequirement(SecuritySchemeName.BASIC),
                constructRequirement(SecuritySchemeName.OAUTH2, scopes.toTypedArray())
            )

            return normalRequirements + shiroRequirements
        }

        return normalRequirements
    }

    private fun constructRequirement(name: String, scopes: Array<String> = arrayOf()): SecurityRequirement {
        val constructor = SecurityRequirement::class.constructors.first()

        val parameters = mutableMapOf<KParameter, Any>()
        parameters[constructor.findParameterByName("name")!!] = name
        parameters[constructor.findParameterByName("scopes")!!] = scopes

        return constructor.callBy(parameters)
    }
}
