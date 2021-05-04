package io.aesy.yumme.dto

import javax.validation.constraints.NotEmpty

@Dto
data class RevokeRefreshTokenRequest(
    @field:NotEmpty(groups = [TokenRequest.RefreshTokenGrantType::class])
    var refresh_token: String?
)
