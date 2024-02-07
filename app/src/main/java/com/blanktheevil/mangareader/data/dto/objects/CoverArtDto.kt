package com.blanktheevil.mangareader.data.dto.objects

import com.blanktheevil.mangareader.data.dto.MangaDexObject
import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoverArtDto(
    override val id: String,
    override val type: String,
    override val attributes: CoverArtAttributesDto,
    override val relationships: RelationshipList?,
) : MangaDexObject<CoverArtDto.CoverArtAttributesDto> {

    @JsonClass(generateAdapter = true)
    data class CoverArtAttributesDto(
        val volume: String?,
        val fileName: String,
        val description: String?,
    )
}


