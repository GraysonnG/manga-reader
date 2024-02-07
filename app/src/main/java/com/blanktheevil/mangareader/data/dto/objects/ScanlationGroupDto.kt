package com.blanktheevil.mangareader.data.dto.objects

import com.blanktheevil.mangareader.data.dto.MangaDexObject
import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScanlationGroupDto(
    override val id: String,
    override val type: String,
    override val attributes: Attributes,
    override val relationships: RelationshipList?,
) : MangaDexObject<ScanlationGroupDto.Attributes> {

    @JsonClass(generateAdapter = true)
    data class Attributes(
        val name: String,
        val altNames: List<Any>?,
        val website: String?,
    )
}
