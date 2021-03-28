package io.aesy.yumme.controller

import io.aesy.yumme.dto.PermissionDto
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.service.*
import io.aesy.yumme.util.ModelMapper.map
import io.swagger.v3.oas.annotations.tags.Tag
import org.modelmapper.ModelMapper
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@Tag(name = "Permission")
@RestController
class PermissionController(
    private val userService: UserService,
    private val roleService: RoleService,
    private val permissionService: PermissionService,
    private val mapper: ModelMapper
) {
    @GetMapping("/permission")
    @Transactional
    fun listPermissions(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<PermissionDto> {
        return permissionService.getAll()
            .asSequence()
            .sortedBy { it.name }
            .map { mapper.map<PermissionDto>(it) }
            .toList()
    }

    @GetMapping("/permission/{id}")
    @Transactional
    fun inspectRoleById(
        @PathVariable(required = true, value = "id") id: Long
    ): PermissionDto {
        return permissionService.getById(id)
            .map { mapper.map<PermissionDto>(it) }
            .orElseThrow { ResourceNotFound() }
    }

    @GetMapping("/role/{id}/permission")
    @Transactional
    @Tag(name = "Role")
    fun listPermissionsByRole(
        @PathVariable(required = true, value = "id") id: Long,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<PermissionDto> {
        val role = roleService.getById(id)
            .orElseThrow { ResourceNotFound() }

        return permissionService.getAllByRole(role)
            .asSequence()
            .sortedBy { it.name }
            .map { mapper.map<PermissionDto>(it) }
            .toList()
    }

    @GetMapping("/user/{id}/permission")
    @Transactional
    @Tag(name = "User")
    fun listPermissionsByUser(
        @PathVariable(required = true, value = "id") id: Long,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<PermissionDto> {
        val user = userService.getById(id)
            .orElseThrow { ResourceNotFound() }

        return permissionService.getAllByUser(user)
            .asSequence()
            .sortedBy { it.name }
            .map { mapper.map<PermissionDto>(it) }
            .toList()
    }

    // TODO /user/me/permission
}
