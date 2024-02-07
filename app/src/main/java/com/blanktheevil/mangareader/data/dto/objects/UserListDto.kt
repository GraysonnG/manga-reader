package com.blanktheevil.mangareader.data.dto.objects

import com.blanktheevil.mangareader.data.dto.MangaDexObject
import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserListDto(
    override val id: String,
    override val type: String,
    override val attributes: UserListAttributesDto,
    override val relationships: RelationshipList?,
) : MangaDexObject<UserListDto.UserListAttributesDto> {
    @JsonClass(generateAdapter = true)
    data class UserListAttributesDto(
        val name: String,
        val visibility: String,
        val version: Int,
    )
}

