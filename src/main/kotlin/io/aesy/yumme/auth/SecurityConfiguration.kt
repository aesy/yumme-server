package io.aesy.yumme.auth

import io.aesy.yumme.service.UserService
import org.apache.shiro.authz.*
import org.apache.shiro.cache.CacheManager
import org.apache.shiro.cache.MemoryConstrainedCacheManager
import org.apache.shiro.event.EventBus
import org.apache.shiro.event.support.DefaultEventBus
import org.apache.shiro.mgt.SessionsSecurityManager
import org.apache.shiro.realm.Realm
import org.apache.shiro.spring.LifecycleBeanPostProcessor
import org.apache.shiro.spring.ShiroEventBusBeanPostProcessor
import org.apache.shiro.spring.config.AbstractShiroConfiguration
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator
import org.springframework.context.annotation.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.Filter

@ControllerAdvice
@Configuration
class SecurityConfiguration: AbstractShiroConfiguration(), WebMvcConfigurer {
    override fun addArgumentResolvers(
        argumentResolvers: MutableList<HandlerMethodArgumentResolver>
    ) {
        argumentResolvers.add(AuthorizedUserArgumentResolver())
    }

    @Bean
    fun lifecycleBeanPostProcessor(): LifecycleBeanPostProcessor? {
        return LifecycleBeanPostProcessor()
    }

    @Bean
    fun eventBus(): EventBus? {
        return DefaultEventBus()
    }

    @Bean
    fun shiroEventBusAwareBeanPostProcessor(): ShiroEventBusBeanPostProcessor? {
        return ShiroEventBusBeanPostProcessor(eventBus())
    }

    @Bean
    fun cacheManager(): CacheManager {
        return MemoryConstrainedCacheManager()
    }

    // Bugfix SHIRO-627
    @Bean
    override fun authorizer(): Authorizer {
        return super.authorizer()
    }

    @Bean
    fun shiroFilterFactoryBean(
        securityManager: SessionsSecurityManager,
        bearerAuthFilter: BearerAuthFilter,
        basicAuthFilter: BasicAuthFilter
    ): ShiroFilterFactoryBean {
        val shiroFilterFactoryBean = ShiroFilterFactoryBean()

        shiroFilterFactoryBean.securityManager = securityManager
        shiroFilterFactoryBean.filters = HashMap<String, Filter>().apply {
            put("bearer", bearerAuthFilter)
            put("basic", basicAuthFilter)
        }
        shiroFilterFactoryBean.filterChainDefinitionMap = HashMap<String, String>().apply {
            // Use permissive to NOT require authentication, the controller annotations will decide that
            put("/**", "bearer[permissive], basic[permissive]")
        }

        return shiroFilterFactoryBean
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    fun defaultAdvisorAutoProxyCreator(): DefaultAdvisorAutoProxyCreator {
        return DefaultAdvisorAutoProxyCreator().apply {
            this.isProxyTargetClass = true
        }
    }

    @ExceptionHandler(UnauthenticatedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleException(e: UnauthenticatedException) {
        // TODO log
    }

    @ExceptionHandler(AuthorizationException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleException(e: AuthorizationException) {
        // TODO log
    }

    @Bean
    fun jwtRealm(jwtService: JwtService): Realm {
        return JwtRealm(jwtService)
    }

    @Bean
    fun passwordRealm(userService: UserService): Realm {
        return PasswordRealm(userService)
    }
}
