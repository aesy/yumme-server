package io.aesy.yumme.service

import io.aesy.yumme.auth.PasswordService
import io.aesy.yumme.entity.User
import io.aesy.yumme.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class UserService(
    private val passwordService: PasswordService,
    private val userRepository: UserRepository
) {
    @Transactional
    fun getAll(): List<User> {
        return userRepository.findAll()
            .toList()
    }

    @Transactional
    fun getById(id: Long): Optional<User> {
        return userRepository.findById(id)
    }

    @Transactional
    fun getByEmail(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }

    @Transactional
    fun getByUserNameAndPassword(userName: String, password: String): Optional<User> {
        return userRepository.findByUserName(userName)
            .filter { passwordService.verifyPassword(password, it.passwordHash) }
    }

    @Transactional
    fun save(user: User): User {
        return userRepository.save(user)
    }

    @Transactional
    fun delete(user: User): Boolean {
        userRepository.delete(user)

        return true
    }

    @Transactional
    fun delete(id: Long): Boolean {
        userRepository.deleteById(id)

        return true
    }
}
