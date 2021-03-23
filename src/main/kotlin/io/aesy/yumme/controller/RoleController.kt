package io.aesy.yumme.controller

import io.aesy.yumme.conversion.ResponseBodyType
import io.aesy.yumme.dto.RoleDto
import io.aesy.yumme.entity.Role
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.service.RoleService
import io.aesy.yumme.service.UserService
import io.aesy.yumme.util.getLogger
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
class RoleController(
    private val userService: UserService,
    private val roleService: RoleService
) {
    companion object {
        private val logger = getLogger()
    }

    @GetMapping("/role")
    @Transactional
    @ResponseBodyType(type = RoleDto::class)
    fun list(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Role> {
        return roleService.getAll()
            .asSequence()
            .sortedBy { it.name }
            .toList()
    }

    @GetMapping("/role/{id}")
    @Transactional
    @ResponseBodyType(type = RoleDto::class)
    fun getRoleById(
        @PathVariable(required = true, value = "id") id: Long
    ): Role {
        return roleService.getById(id)
            .orElseThrow { ResourceNotFound() }
    }

    @GetMapping("/user/{id}/role")
    @Transactional
    @ResponseBodyType(type = RoleDto::class)
    fun listByUser(
        @PathVariable(required = true, value = "id") id: Long,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<Role> {
        val user = userService.getById(id)
            .orElseThrow { ResourceNotFound() }

        return roleService.getAllByUser(user)
            .asSequence()
            .sortedBy { it.name }
            .toList()
    }

    // TODO /user/me/role
}
