package io.aesy.yumme.service

import io.aesy.yumme.entity.Role
import io.aesy.yumme.entity.User
import io.aesy.yumme.repository.RoleRepository
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class RoleService(
    private val roleRepository: RoleRepository
) {
    @Transactional
    fun getAll(): List<Role> {
        return roleRepository.findAll()
            .toList()
    }

    @Transactional
    fun getAllByUser(user: User): List<Role> {
        return roleRepository.findAllByUser(user)
            .toList()
    }

    @Transactional
    fun getById(id: Long): Optional<Role> {
        return roleRepository.findById(id)
    }

    @Transactional
    fun getByName(name: String): Optional<Role> {
        return roleRepository.findByName(name)
    }

    @Transactional
    fun save(role: Role): Role {
        return roleRepository.save(role)
    }

    @Transactional
    fun delete(role: Role): Boolean {
        roleRepository.delete(role)

        return true
    }

    @Transactional
    fun delete(id: Long): Boolean {
        roleRepository.deleteById(id)

        return true
    }
}
