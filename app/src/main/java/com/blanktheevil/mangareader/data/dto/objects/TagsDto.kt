package com.blanktheevil.mangareader.data.dto.objects

import com.blanktheevil.mangareader.data.dto.MangaDexObject
import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TagsDto(
    override val id: String,
    override val type: String,
    override val attributes: TagsAttributesDto,
    override val relationships: RelationshipList?
) : MangaDexObject<TagsDto.TagsAttributesDto> {

    @JsonClass(generateAdapter = true)
    data class TagsAttributesDto(
        val name: Map<String, String>,
        val description: Map<String, String>,
        val group: String?,
        val version: Int?,
    )
}

