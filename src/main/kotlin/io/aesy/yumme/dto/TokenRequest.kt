package io.aesy.yumme.dto

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Dto
data class TokenRequest(
    @field:NotNull(groups = [PasswordGrantType::class, RefreshTokenGrantType::class])
    var grant_type: String?,

    @field:NotEmpty(groups = [PasswordGrantType::class])
    var username: String?,

    @field:NotEmpty(groups = [PasswordGrantType::class])
    var password: String?,

    @field:NotEmpty(groups = [RefreshTokenGrantType::class])
    var refresh_token: String?
) {
    interface PasswordGrantType
    interface RefreshTokenGrantType
}
