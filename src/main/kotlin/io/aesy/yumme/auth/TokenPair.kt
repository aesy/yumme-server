package io.aesy.yumme.auth

import io.aesy.yumme.entity.RefreshToken

data class TokenPair(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken
)
