package io.aesy.yumme.mapper

import io.aesy.yumme.dto.UserDto
import io.aesy.yumme.entity.User
import org.springframework.stereotype.Service

@Service
class UserMapper {
    fun toDto(user: User): UserDto = UserDto(
        id = user.id,
        userName = user.userName,
        displayName = user.displayName
    )
}
