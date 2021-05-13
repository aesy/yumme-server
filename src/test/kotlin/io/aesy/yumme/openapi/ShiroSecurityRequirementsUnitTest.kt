package io.aesy.yumme.openapi

import io.aesy.test.TestType
import io.aesy.yumme.controller.UserController
import io.aesy.yumme.mapper.UserMapper
import io.aesy.yumme.service.UserService
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import org.springdoc.core.PropertyResolverUtils
import org.springframework.web.method.HandlerMethod
import strikt.api.expectThat
import strikt.assertions.isNotEmpty
import kotlin.reflect.jvm.javaMethod

@TestType.Unit
class ShiroSecurityRequirementsUnitTest {
    @MockK
    private lateinit var userService: UserService

    @MockK
    private lateinit var mapper: UserMapper

    @MockK
    private lateinit var propertyResolverUtils: PropertyResolverUtils

    @Test
    fun `It should include RequiresAuthentication annotations when parsing security requirements`() {
        val parser = ShiroSecurityParser(propertyResolverUtils)
        val controller = UserController(userService, mapper)
        val handler = HandlerMethod(controller, UserController::inspectSelf.javaMethod!!)
        val requirements = parser.getSecurityRequirements(handler)

        expectThat(requirements.toList()).isNotEmpty()
    }
}
