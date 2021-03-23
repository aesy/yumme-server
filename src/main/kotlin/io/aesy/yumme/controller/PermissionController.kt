package io.aesy.yumme.controller

import io.aesy.yumme.conversion.ResponseBodyType
import io.aesy.yumme.dto.PermissionDto
import io.aesy.yumme.entity.Permission
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.service.*
import io.aesy.yumme.util.getLogger
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
class PermissionController(
    private val userService: UserService,
    private val roleService: RoleService,
    private val permissionService: PermissionService
) {
    companion object {
        private val logger = getLogger()
    }

    @GetMapping("/permission")
    @Transactional
    @ResponseBodyType(type = PermissionDto::class)
    fun list(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Permission> {
        return permissionService.getAll()
            .asSequence()
            .sortedBy { it.name }
            .toList()
    }

    @GetMapping("/permission/{id}")
    @Transactional
    @ResponseBodyType(type = PermissionDto::class)
    fun getRoleById(
        @PathVariable(required = true, value = "id") id: Long
    ): Permission {
        return permissionService.getById(id)
            .orElseThrow { ResourceNotFound() }
    }

    @GetMapping("/role/{id}/permission")
    @Transactional
    @ResponseBodyType(type = PermissionDto::class)
    fun listByRole(
        @PathVariable(required = true, value = "id") id: Long,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Permission> {
        val role = roleService.getById(id)
            .orElseThrow { ResourceNotFound() }

        return permissionService.getAllByRole(role)
            .asSequence()
            .sortedBy { it.name }
            .toList()
    }

    @GetMapping("/user/{id}/permission")
    @Transactional
    @ResponseBodyType(type = PermissionDto::class)
    fun listByUser(
        @PathVariable(required = true, value = "id") id: Long,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Permission> {
        val user = userService.getById(id)
            .orElseThrow { ResourceNotFound() }

        return permissionService.getAllByUser(user)
            .asSequence()
            .sortedBy { it.name }
            .toList()
    }

    // TODO /user/me/permission
}
