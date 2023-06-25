package com.blanktheevil.mangareader.data.session

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Session(
    val token: String,
    val refresh: String,
    val expires: Date,
) {
    fun isExpired() = Date().after(expires)
}

@JsonClass(generateAdapter = true)
data class Refresh(
    val token: String,
)
