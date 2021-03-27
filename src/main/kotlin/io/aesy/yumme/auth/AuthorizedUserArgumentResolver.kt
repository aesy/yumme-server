package io.aesy.yumme.auth

import io.aesy.yumme.entity.User
import io.aesy.yumme.util.getLogger
import org.apache.shiro.SecurityUtils
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.server.ResponseStatusException

class AuthorizedUserArgumentResolver: HandlerMethodArgumentResolver {
    companion object {
        private val logger = getLogger()
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        if (!parameter.parameterType.isAssignableFrom(User::class.java)) {
            return false
        }

        return parameter.hasParameterAnnotation(AuthorizedUser::class.java)
    }

    @Throws(ResponseStatusException::class)
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): User? {
        logger.debug("About to try to resolve currently logged in user")
        val subject = SecurityUtils.getSubject()
        val principal = subject.principal ?: return null

        if (principal !is User) {
            logger.error(
                "Failed to resolve currently logged in user. Subject principal of type {} cannot be cast to type {}.",
                principal::class.java, User::class.java
            )
            return null
        }

        return principal
    }
}
