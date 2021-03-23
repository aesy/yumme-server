package io.aesy.yumme.repository

import io.aesy.yumme.entity.Permission
import org.springframework.data.repository.CrudRepository
import java.util.*

interface PermissionRepository: CrudRepository<Permission, Long> {
    // TODO fun findAllByUser(user: User): Set<Permission>
    // TODO fun findAllByRole(role: Role): Set<Permission>
    fun findByName(name: String): Optional<Permission>
}
