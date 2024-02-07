package com.blanktheevil.mangareader.data.dto.objects

import com.blanktheevil.mangareader.data.dto.MangaDexObject
import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScanlationGroupDto(
    override val id: String,
    override val type: String,
    override val attributes: ScanlationGroupAttributesDto,
    override val relationships: RelationshipList?,
) : MangaDexObject<ScanlationGroupDto.ScanlationGroupAttributesDto> {

    @JsonClass(generateAdapter = true)
    data class ScanlationGroupAttributesDto(
        val name: String,
        val altNames: List<Any>?,
        val website: String?,
    )
}

