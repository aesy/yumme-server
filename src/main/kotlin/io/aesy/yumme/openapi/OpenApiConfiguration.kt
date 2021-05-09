package io.aesy.yumme.openapi

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
import org.springdoc.core.SecurityParser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(info = Info(title = "Yumme API"))
@SecuritySchemes(
    SecurityScheme(type = SecuritySchemeType.HTTP, name = SecuritySchemeName.BASIC),
    SecurityScheme(type = SecuritySchemeType.OAUTH2, name = SecuritySchemeName.OAUTH2)
)
@Configuration
class OpenApiConfiguration {
    // Override existing security parser to support shiro annotations such as `@RequiresAuthentication`
    @Bean
    fun securityParser(): SecurityParser {
        return ShiroSecurityParser()
    }
}
