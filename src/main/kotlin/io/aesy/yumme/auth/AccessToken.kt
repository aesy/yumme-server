package io.aesy.yumme.auth

data class AccessToken(
    val value: String,
    val expiresInSeconds: Long
)
