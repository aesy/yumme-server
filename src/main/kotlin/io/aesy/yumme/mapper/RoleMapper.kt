package io.aesy.yumme.mapper

import io.aesy.yumme.dto.RoleDto
import io.aesy.yumme.entity.Role
import org.springframework.stereotype.Service

@Service
class RoleMapper {
    fun toDto(role: Role): RoleDto = RoleDto(
        name = role.name
    )
}
