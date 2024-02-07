package com.blanktheevil.mangareader.data.dto.objects

import com.blanktheevil.mangareader.data.dto.MangaDexObject
import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoverDto(
    override val id: String,
    override val type: String,
    override val attributes: Attributes,
    override val relationships: RelationshipList?,
) : MangaDexObject<CoverDto.Attributes> {

    @JsonClass(generateAdapter = true)
    data class Attributes(
        val volume: String,
        val fileName: String,
        val description: String,
        val version: Int
    )
}
