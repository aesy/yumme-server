package io.aesy.yumme.repository

import io.aesy.yumme.entity.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository: CrudRepository<User, Long> {
    fun findByUserName(userName: String): Optional<User>
    fun findByUserNameAndPasswordHash(userName: String, hash: String): Optional<User>
}
