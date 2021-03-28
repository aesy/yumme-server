package io.aesy.yumme.controller

import io.aesy.yumme.dto.RoleDto
import io.aesy.yumme.exception.ResourceNotFound
import io.aesy.yumme.service.RoleService
import io.aesy.yumme.service.UserService
import io.aesy.yumme.util.ModelMapper.map
import io.swagger.v3.oas.annotations.tags.Tag
import org.modelmapper.ModelMapper
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@Tag(name = "Role")
@RestController
class RoleController(
    private val userService: UserService,
    private val roleService: RoleService,
    private val mapper: ModelMapper
) {
    @GetMapping("/role")
    @Transactional
    fun listRoles(
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<RoleDto> {
        return roleService.getAll()
            .asSequence()
            .sortedBy { it.name }
            .map { mapper.map<RoleDto>(it) }
            .toList()
    }

    @GetMapping("/role/{id}")
    @Transactional
    fun inspectRoleById(
        @PathVariable(required = true, value = "id") id: Long
    ): RoleDto {
        return roleService.getById(id)
            .map { mapper.map<RoleDto>(it) }
            .orElseThrow { ResourceNotFound() }
    }

    @GetMapping("/user/{id}/role")
    @Transactional
    @Tag(name = "User")
    fun listRolesByUser(
        @PathVariable(required = true, value = "id") id: Long,
        @RequestParam(required = false, defaultValue = "0") offset: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): List<RoleDto> {
        val user = userService.getById(id)
            .orElseThrow { ResourceNotFound() }

        return roleService.getAllByUser(user)
            .asSequence()
            .sortedBy { it.name }
            .map { mapper.map<RoleDto>(it) }
            .toList()
    }

    // TODO /user/me/role
}
