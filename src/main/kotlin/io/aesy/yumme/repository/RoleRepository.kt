package io.aesy.yumme.repository

import io.aesy.yumme.entity.*
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RoleRepository: CrudRepository<Role, Long> {
    fun findAll(pageable: Pageable = Pageable.unpaged()): List<Role>
    fun findByName(name: String): Optional<Role>

    @Query("SELECT user.roles FROM User user WHERE user = :user")
    fun findAllByUser(user: User): Set<Role>
}
