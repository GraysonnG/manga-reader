package com.blanktheevil.mangareader.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUserListsResponse(
    val result: String,
    val data: List<UserListDto>,
)

@JsonClass(generateAdapter = true)
data class UserListDto(
    val id: String,
    val attributes: UserListAttributesDto,
    val relationships: List<UserListRelationship>
)

@JsonClass(generateAdapter = true)
data class UserListAttributesDto(
    val name: String,
    val visibility: String,
    val version: Int,
)

@JsonClass(generateAdapter = true)
data class UserListRelationship(
    val id: String,
    val type: String,
)

fun GetUserListsResponse.parseData(): Map<UserListDto, List<String>> {
    return data.associateWith {
        it.relationships.filter { rel -> rel.type == "manga" }
            .map { rel -> rel.id }
    }
}