package com.blanktheevil.mangareader.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUserResponse(
    val result: String,
    val response: String,
    val data: UserDto,
)

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: String,
    val type: String,
    val attributes: UserAttributesDto,
    val relationships: List<RelationshipDto>,
) {

}

@JsonClass(generateAdapter = true)
data class UserAttributesDto(
    val username: String,
)
