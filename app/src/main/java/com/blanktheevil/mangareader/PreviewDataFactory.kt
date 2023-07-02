package com.blanktheevil.mangareader

import com.blanktheevil.mangareader.data.dto.AggregateChapterDto
import com.blanktheevil.mangareader.data.dto.AggregateVolumeDto
import com.blanktheevil.mangareader.data.dto.ChapterAttributesDto
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaAttributesDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.dto.RelationshipAttributesDto
import com.blanktheevil.mangareader.data.dto.RelationshipDto
import com.blanktheevil.mangareader.data.dto.TagsAttributesDto
import com.blanktheevil.mangareader.data.dto.TagsDto
import java.util.Date
import java.util.UUID

object PreviewDataFactory {
    val LONG_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus finibus porta mauris, non placerat justo. Nulla aliquet venenatis mi, et hendrerit mauris volutpat eget. Quisque cursus elementum interdum. Morbi elementum nisi eu convallis aliquam. Nulla eu libero lacus. Curabitur mollis nec massa sit amet efficitur. Aliquam tincidunt nec ipsum sollicitudin dapibus. Donec at finibus nibh, ut efficitur elit. Vestibulum nec scelerisque magna. "
    val MANGA_LIST = listOf(
        MangaDto(
            id = "0953cced-141c-490f-a5d2-a359587be7f1",
            type = "manga",
            attributes = MangaAttributesDto(
                title = mapOf(
                    "en" to "Futari Ashita mo Sorenari ni"
                ),
                description = mapOf(
                    "en" to "A slice-of-life romantic comedy about Yuuya and Rio, a young couple where she's about 4 years his senior, who have just started living together."
                ),
                isLocked = false,
                links = emptyMap(),
                originalLanguage = "ja",
                lastVolume = "5",
                lastChapter = "87",
                publicationDemographic = "seinen",
                status = "completed",
                year = 2019,
                tags = emptyList(),
                state = "published",
                createdAt = null,
                updatedAt = null,
                latestUploadedChapter = "6d358b9b-96be-4cff-a55b-04090a4e8431"
            ),
            relationships = listOf(
                RelationshipDto(
                    id = "c7dffeba-81fe-4fe7-bea3-8e21106a428d",
                    type = "cover_art",
                    attributes = RelationshipAttributesDto(
                        name = null,
                        fileName = "5992df71-e03f-4589-8f7f-7b64a44c5c79.png",
                        description = null,
                        group = null
                    ),
                    relationships = null
                )
            )
        ),
        MangaDto(
            id = "0953cced-141c-490f-a5d2-a359587be7f2",
            type = "manga",
            attributes = MangaAttributesDto(
                title = mapOf(
                    "en" to "Futari Ashita mo Sorenari ni"
                ),
                description = mapOf(
                    "en" to "A slice-of-life romantic comedy about Yuuya and Rio, a young couple where she's about 4 years his senior, who have just started living together."
                ),
                isLocked = false,
                links = emptyMap(),
                originalLanguage = "ja",
                lastVolume = "5",
                lastChapter = "87",
                publicationDemographic = "seinen",
                status = "completed",
                year = 2019,
                tags = emptyList(),
                state = "published",
                createdAt = null,
                updatedAt = null,
                latestUploadedChapter = "6d358b9b-96be-4cff-a55b-04090a4e8431"
            ),
            relationships = listOf(
                RelationshipDto(
                    id = "c7dffeba-81fe-4fe7-bea3-8e21106a428d",
                    type = "cover_art",
                    attributes = RelationshipAttributesDto(
                        name = null,
                        fileName = "5992df71-e03f-4589-8f7f-7b64a44c5c79.png",
                        description = null,
                        group = null
                    ),
                    relationships = null
                )
            )
        ),
        MangaDto(
            id = "0953cced-141c-490f-a5d2-a359587be7f3",
            type = "manga",
            attributes = MangaAttributesDto(
                title = mapOf(
                    "en" to "Futari Ashita mo Sorenari ni"
                ),
                description = mapOf(
                    "en" to "A slice-of-life romantic comedy about Yuuya and Rio, a young couple where she's about 4 years his senior, who have just started living together."
                ),
                isLocked = false,
                links = emptyMap(),
                originalLanguage = "ja",
                lastVolume = "5",
                lastChapter = "87",
                publicationDemographic = "seinen",
                status = "completed",
                year = 2019,
                tags = emptyList(),
                state = "published",
                createdAt = null,
                updatedAt = null,
                latestUploadedChapter = "6d358b9b-96be-4cff-a55b-04090a4e8431"
            ),
            relationships = listOf(
                RelationshipDto(
                    id = "c7dffeba-81fe-4fe7-bea3-8e21106a428d",
                    type = "cover_art",
                    attributes = RelationshipAttributesDto(
                        name = null,
                        fileName = "5992df71-e03f-4589-8f7f-7b64a44c5c79.png",
                        description = null,
                        group = null
                    ),
                    relationships = null
                )
            )
        ),
        MangaDto(
            id = "0953cced-141c-490f-a5d2-a359587be7f4",
            type = "manga",
            attributes = MangaAttributesDto(
                title = mapOf(
                    "en" to "Futari Ashita mo Sorenari ni"
                ),
                description = mapOf(
                    "en" to "A slice-of-life romantic comedy about Yuuya and Rio, a young couple where she's about 4 years his senior, who have just started living together."
                ),
                isLocked = false,
                links = emptyMap(),
                originalLanguage = "ja",
                lastVolume = "5",
                lastChapter = "87",
                publicationDemographic = "seinen",
                status = "completed",
                year = 2019,
                tags = emptyList(),
                state = "published",
                createdAt = null,
                updatedAt = null,
                latestUploadedChapter = "6d358b9b-96be-4cff-a55b-04090a4e8431"
            ),
            relationships = listOf(
                RelationshipDto(
                    id = "c7dffeba-81fe-4fe7-bea3-8e21106a428d",
                    type = "cover_art",
                    attributes = RelationshipAttributesDto(
                        name = null,
                        fileName = "5992df71-e03f-4589-8f7f-7b64a44c5c79.png",
                        description = null,
                        group = null
                    ),
                    relationships = null
                )
            )
        )
    )
    val CHAPTER_LIST = listOf(
        ChapterDto(
            id = "4c1e62ec-8f54-4d88-97d6-bf9e5683d1b1",
            type = "chapter",
            attributes = ChapterAttributesDto(
                volume = "1",
                chapter = "1",
                title = "Uzlaşma ruhu",
                translatedLanguage = "tr",
                externalUrl = null,
                publishAt = Date(),
                readableAt = Date(),
                createdAt = Date(),
                updatedAt = Date(),
                pages = 8,
                version = 3
            ),
            relationships = emptyList()
        ),
        ChapterDto(
            id = "4c1e62ec-8f54-4d88-97d6-bf9e5683d1b2",
            type = "chapter",
            attributes = ChapterAttributesDto(
                volume = "1",
                chapter = "2",
                title = "Uzlaşma ruhu",
                translatedLanguage = "tr",
                externalUrl = null,
                publishAt = Date(),
                readableAt = Date(),
                createdAt = Date(),
                updatedAt = Date(),
                pages = 8,
                version = 3
            ),
            relationships = emptyList()
        ),
        ChapterDto(
            id = "4c1e62ec-8f54-4d88-97d6-bf9e5683d1b3",
            type = "chapter",
            attributes = ChapterAttributesDto(
                volume = "1",
                chapter = "3",
                title = "Uzlaşma ruhu",
                translatedLanguage = "tr",
                externalUrl = null,
                publishAt = Date(),
                readableAt = Date(),
                createdAt = Date(),
                updatedAt = Date(),
                pages = 8,
                version = 3
            ),
            relationships = emptyList()
        )
    )
    val VOLUME_AGGREGATE = AggregateVolumeDto(
        volume = "1",
        chapters = mapOf(
            "1" to AggregateChapterDto(
                chapter = "1",
                id = "${UUID.randomUUID()}",
            ),
            "2" to AggregateChapterDto(
                chapter = "2",
                id = "${UUID.randomUUID()}",
            ),
            "3" to AggregateChapterDto(
                chapter = "3",
                id = "${UUID.randomUUID()}",
            ),
        )
    )
    val MANGA = MangaDto(
        id = "123",
        type = "manga",
        attributes = MangaAttributesDto(
            title = mapOf("en" to "My Manga"),
            description = mapOf("en" to "This is a manga"),
            isLocked = false,
            links = mapOf("website" to "https://example.com"),
            originalLanguage = "Japanese",
            lastVolume = "5",
            lastChapter = "50",
            publicationDemographic = "Shonen",
            status = "ongoing",
            year = 2022,
            tags = listOf(
                TagsDto(
                    id = "tag1",
                    type = "tag",
                    attributes = TagsAttributesDto(
                        name = mapOf("en" to "Action"),
                        description = mapOf("en" to "Manga with action scenes"),
                        group = "Genre",
                        version = 1
                    ),
                    relationships = emptyList()
                ),
                TagsDto(
                    id = "tag2",
                    type = "tag",
                    attributes = TagsAttributesDto(
                        name = mapOf("en" to "Romance"),
                        description = mapOf("en" to "Manga with romantic elements"),
                        group = "Genre",
                        version = 1
                    ),
                    relationships = emptyList()
                )
            ),
            state = "published",
            createdAt = null,
            updatedAt = null,
            latestUploadedChapter = "Chapter 50"
        ),
        relationships = emptyList()
    )
    val CHAPTER = ChapterDto(
        id = "4c1e62ec-8f54-4d88-97d6-bf9e5683d1b8",
        type = "chapter",
        attributes = ChapterAttributesDto(
            volume = "1",
            chapter = "1",
            title = "Uzlaşma ruhu with a really really long name that cant be shown",
            translatedLanguage = "tr",
            externalUrl = null,
            publishAt = Date(),
            readableAt = Date(),
            createdAt = Date(),
            updatedAt = Date(),
            pages = 8,
            version = 3
        ),
        relationships = emptyList()
    )
}