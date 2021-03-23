package io.aesy.yumme.repository

import io.aesy.yumme.entity.Role
import io.aesy.yumme.entity.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RoleRepository: CrudRepository<Role, Long> {
    @Query("SELECT user.roles FROM User user WHERE user = :user")
    fun findAllByUser(user: User): Set<Role>
    fun findByName(name: String): Optional<Role>
}
