package io.aesy.yumme.auth

import io.aesy.yumme.entity.User
import org.apache.shiro.SecurityUtils
import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class AuthorizedUserArgumentResolver: HandlerMethodArgumentResolver {
    companion object {
        private val logger = LoggerFactory.getLogger(AuthorizedUserArgumentResolver::class.java)
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        if (!parameter.parameterType.isAssignableFrom(User::class.java)) {
            return false
        }

        return parameter.hasParameterAnnotation(AuthorizedUser::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): User? {
        logger.debug("About to try to resolve currently logged in user")
        val subject = SecurityUtils.getSubject()
        val principal = subject.principal as User?

        if (principal == null) {
            logger.debug("Failed to resolve currently logged in user. Subject principal is null.")
        }

        return principal
    }
}
