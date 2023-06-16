package com.blanktheevil.mangareader.data.dto

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val result: String,
    val token: AuthTokenDto
)

@JsonClass(generateAdapter = true)
data class AuthTokenDto(
    val session: String,
    val refresh: String,
)