package com.blanktheevil.mangareader.data.dto.objects

import com.blanktheevil.mangareader.data.dto.MangaDexObject
import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    override val id: String,
    override val type: String,
    override val attributes: UserAttributesDto,
    override val relationships: RelationshipList?,
) : MangaDexObject<UserDto.UserAttributesDto> {

    @JsonClass(generateAdapter = true)
    data class UserAttributesDto(
        val username: String,
    )
}
