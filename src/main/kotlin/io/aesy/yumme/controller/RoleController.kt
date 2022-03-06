package io.aesy.yumme.controller

import io.aesy.yumme.auth.AuthorizedUser
import io.aesy.yumme.dto.RoleDto
import io.aesy.yumme.entity.Role
import io.aesy.yumme.entity.User
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.mapper.RoleMapper
import io.aesy.yumme.service.RoleService
import io.aesy.yumme.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresRoles
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import kotlin.math.min

@Tag(name = "Role")
@RestController
@RequestMapping(
    consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Validated
class RoleController(
    private val userService: UserService,
    private val roleService: RoleService,
    private val mapper: RoleMapper
) {
    @RequiresAuthentication
    @RequiresRoles(Role.ADMIN)
    @GetMapping("/role")
    @Transactional
    fun listRoles(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<RoleDto> {
        val maxLimit = 100

        return roleService.getAll(min(limit, maxLimit), offset)
            .map(mapper::toDto)
    }

    @RequiresAuthentication
    @RequiresRoles(Role.ADMIN)
    @GetMapping("/user/{id}/role")
    @Transactional
    @Tag(name = "User")
    fun listRolesByUser(
        @PathVariable("id") id: Long
    ): List<RoleDto> {
        val user = userService.getById(id)
            .orElseThrow { ResourceNotFound.user(id) }

        return roleService.getAllByUser(user)
            .map(mapper::toDto)
    }

    @RequiresAuthentication
    @GetMapping("/user/me/role")
    @Transactional
    @Tag(name = "User")
    fun listRolesBySelf(
        @AuthorizedUser user: User
    ): List<RoleDto> {
        return roleService.getAllByUser(user)
            .map(mapper::toDto)
    }
}
