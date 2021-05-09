package io.aesy.yumme.service

import io.aesy.yumme.entity.Role
import io.aesy.yumme.entity.User
import io.aesy.yumme.repository.RoleRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class RoleService(
    private val roleRepository: RoleRepository
) {
    @Transactional
    fun getAll(limit: Int = 0, offset: Int = 0): List<Role> {
        val page = if (limit > 0) {
            PageRequest.of(offset, limit)
        } else {
            Pageable.unpaged()
        }

        return roleRepository.findAll(page)
    }

    @Transactional
    fun getAllByUser(user: User): List<Role> {
        return roleRepository.findAllByUser(user)
            .toList()
    }

    @Transactional
    fun getByName(name: String): Optional<Role> {
        return roleRepository.findByName(name)
    }
}
