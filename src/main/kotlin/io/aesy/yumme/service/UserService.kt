package io.aesy.yumme.service

import io.aesy.yumme.auth.PasswordService
import io.aesy.yumme.entity.Role
import io.aesy.yumme.entity.User
import io.aesy.yumme.exception.ResourceAlreadyExists
import io.aesy.yumme.repository.UserRepository
import io.aesy.yumme.util.Logging.getLogger
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct
import javax.transaction.Transactional

@Service
class UserService(
    private val passwordService: PasswordService,
    private val roleService: RoleService,
    private val userRepository: UserRepository
) {
    companion object {
        private val logger = getLogger()
    }

    @PostConstruct
    internal fun addAdminUser() {
        val userCount = userRepository.count()

        if (userCount == 0L) {
            logger.info("Creating ADMIN user")

            register("admin", "Admin", "admin").also { user ->
                addRoleToUser(user, Role.ADMIN)
            }
        }
    }

    @Transactional
    fun getById(id: Long): Optional<User> {
        return userRepository.findById(id)
    }

    @Transactional
    fun getByUserName(userName: String): Optional<User> {
        return userRepository.findByUserName(userName)
    }

    @Transactional
    fun getByUserNameAndPassword(userName: String, password: String): Optional<User> {
        return userRepository.findByUserName(userName)
            .filter { passwordService.verifyPassword(password, it.passwordHash) }
    }

    @Transactional
    fun register(userName: String, displayName: String, password: String): User {
        if (getByUserName(userName).isPresent) {
            throw ResourceAlreadyExists()
        }

        val passwordHash = passwordService.encodePassword(password)
        val user = User(userName = userName, displayName = displayName, passwordHash = passwordHash)

        return userRepository.save(user)
    }

    @Throws(NoSuchElementException::class)
    @Transactional
    fun addRoleToUser(userName: User, role: String) {
        roleService.getByName(role)
            .orElseThrow()
            .run(userName.roles::add)

        userRepository.save(userName)
    }
}
