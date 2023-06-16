package com.blanktheevil.mangareader.data

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Session(
    val token: String,
    val refresh: String,
    val expires: Date,
)

@JsonClass(generateAdapter = true)
data class Refresh(
    val token: String,
)
