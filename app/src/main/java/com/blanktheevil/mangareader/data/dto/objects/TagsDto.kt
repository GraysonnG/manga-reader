package com.blanktheevil.mangareader.data.dto.objects

import com.blanktheevil.mangareader.data.dto.MangaDexObject
import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TagsDto(
    override val id: String,
    override val type: String,
    override val attributes: Attributes,
    override val relationships: RelationshipList?,
) : MangaDexObject<TagsDto.Attributes> {

    @JsonClass(generateAdapter = true)
    data class Attributes(
        val name: Map<String, String>,
        val description: Map<String, String>,
        val group: String?,
        val version: Int?,
    )
}
