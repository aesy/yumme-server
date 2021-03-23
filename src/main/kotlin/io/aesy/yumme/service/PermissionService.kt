package io.aesy.yumme.service

import io.aesy.yumme.entity.*
import io.aesy.yumme.repository.PermissionRepository
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class PermissionService(
    private val permissionRepository: PermissionRepository
) {
    @Transactional
    fun getAll(): List<Permission> {
        return permissionRepository.findAll()
            .toList()
    }

    @Transactional
    fun getAllByUser(user: User): List<Permission> {
        TODO()
        // TODO return permissionRepository.findAllByUser(user)
        //     .toList()
    }

    @Transactional
    fun getAllByRole(role: Role): List<Permission> {
        return role.permissions.toList()
    }

    @Transactional
    fun getById(id: Long): Optional<Permission> {
        return permissionRepository.findById(id)
    }

    @Transactional
    fun getByName(name: String): Optional<Permission> {
        return permissionRepository.findByName(name)
    }

    @Transactional
    fun save(tag: Permission): Permission {
        return permissionRepository.save(tag)
    }

    @Transactional
    fun delete(tag: Permission): Boolean {
        permissionRepository.delete(tag)

        return true
    }

    @Transactional
    fun delete(id: Long): Boolean {
        permissionRepository.deleteById(id)

        return true
    }
}
