package com.blanktheevil.mangareader.data.dto.objects

import com.blanktheevil.mangareader.data.dto.MangaDexObject
import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class ChapterDto(
    override val id: String,
    override val type: String,
    override val attributes: ChapterAttributesDto,
    override val relationships: RelationshipList?,
) : MangaDexObject<ChapterDto.ChapterAttributesDto> {

    @JsonClass(generateAdapter = true)
    data class ChapterAttributesDto(
        val volume: String?,
        val chapter: String?,
        val title: String?,
        val translatedLanguage: String?,
        val externalUrl: String?,
        val publishAt: Date?,
        val readableAt: Date?,
        val createdAt: Date?,
        val updatedAt: Date?,
        val pages: Int?,
        val version: Int?,
    )
}