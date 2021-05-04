package io.aesy.yumme.mapper

import io.aesy.yumme.dto.UserDto
import io.aesy.yumme.entity.User
import org.springframework.stereotype.Service

@Service
class UserMapper {
    fun toDto(user: User): UserDto = UserDto(
        name = user.userName
    )
}
