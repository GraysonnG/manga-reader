package com.blanktheevil.mangareader.data.dto.objects

import com.blanktheevil.mangareader.data.dto.MangaDexObject
import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class MangaDto(
    override val id: String,
    override val type: String,
    override val attributes: Attributes,
    override val relationships: RelationshipList?,
) : MangaDexObject<MangaDto.Attributes> {

    @JsonClass(generateAdapter = true)
    data class Attributes(
        val title: Map<String, String>,
        val description: Map<String, String>?,
        val isLocked: Boolean?,
        val links: Map<String, String>?,
        val originalLanguage: String?,
        val lastVolume: String?,
        val lastChapter: String?,
        val publicationDemographic: String?,
        val status: String?, // convert to enum
        val year: Int?,
        val tags: List<TagsDto>?,
        val state: String?, // convert to enum
        val createdAt: Date?,
        val updatedAt: Date?,
        val latestUploadedChapter: String?,
    )
}
