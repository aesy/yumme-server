package io.aesy.yumme.openapi

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
import org.springdoc.core.SpringDocUtils
import org.springframework.context.annotation.Configuration
import java.awt.image.BufferedImage
import java.time.Duration
import java.time.Instant

@OpenAPIDefinition(info = Info(title = "Yumme API"))
@SecuritySchemes(
    SecurityScheme(type = SecuritySchemeType.HTTP, name = SecuritySchemeName.BASIC),
    SecurityScheme(type = SecuritySchemeType.OAUTH2, name = SecuritySchemeName.OAUTH2)
)
@Configuration
class OpenApiConfiguration {
    init {
        SpringDocUtils
            .getConfig()
            .replaceWithClass(BufferedImage::class.java, ByteArray::class.java)
            .replaceWithClass(Duration::class.java, Long::class.java)
            .replaceWithClass(Instant::class.java, Long::class.java)
    }
}
