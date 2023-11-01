package com.blanktheevil.mangareader.data

import com.blanktheevil.mangareader.data.dto.AggregateChapterDto
import com.blanktheevil.mangareader.data.dto.AggregateVolumeDto
import com.blanktheevil.mangareader.data.dto.ChapterAttributesDto
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.ChapterPagesDataDto
import com.blanktheevil.mangareader.data.dto.GetChapterIdsResponse
import com.blanktheevil.mangareader.data.dto.GetChapterListResponse
import com.blanktheevil.mangareader.data.dto.GetChapterPagesResponse
import com.blanktheevil.mangareader.data.dto.GetChapterResponse
import com.blanktheevil.mangareader.data.dto.GetMangaAggregateResponse
import com.blanktheevil.mangareader.data.dto.GetMangaListResponse
import com.blanktheevil.mangareader.data.dto.GetMangaResponse
import com.blanktheevil.mangareader.data.dto.GetSeasonalDataResponse
import com.blanktheevil.mangareader.data.dto.GetUserListsResponse
import com.blanktheevil.mangareader.data.dto.GetUserResponse
import com.blanktheevil.mangareader.data.dto.MangaAttributesDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.dto.TagsAttributesDto
import com.blanktheevil.mangareader.data.dto.TagsDto
import com.blanktheevil.mangareader.data.dto.UserAttributesDto
import com.blanktheevil.mangareader.data.dto.UserDto
import com.blanktheevil.mangareader.data.dto.UserListAttributesDto
import com.blanktheevil.mangareader.data.dto.UserListDto
import com.blanktheevil.mangareader.data.session.Session
import org.json.JSONObject
import java.util.Calendar
import java.util.Date
import java.util.UUID

object StubData {
    private val TOMORROW = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
    }.time
    private val scanlationGroupJson = """
        {
          "id": "155d7139-8d9a-49eb-bceb-d5e26db08b72",
          "type": "scanlation_group",
          "attributes": {
            "name": "Ecchi No Doujinshi Scans",
            "altNames": [],
            "locked": true,
            "website": "https://www.patreon.com/luigiymario2",
            "ircServer": null,
            "ircChannel": null,
            "discord": "FTAdmbuq",
            "contactEmail": "guzman.luis10@gmail.com",
            "description": "We focus on translating upcoming mangakas from twitter or other social medias, usually for ecchi mangas.",
            "twitter": "https://twitter.com/luigiyking2",
            "mangaUpdates": null,
            "focusedLanguages": [
              "en"
            ],
            "official": false,
            "verified": false,
            "inactive": false,
            "publishDelay": null,
            "exLicensed": false,
            "createdAt": "2022-10-02T08:54:24+00:00",
            "updatedAt": "2023-06-02T04:35:10+00:00",
            "version": 8
          }
        }
    """.trimIndent()
    val LONG_TEXT =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus finibus porta mauris, non placerat justo. Nulla aliquet venenatis mi, et hendrerit mauris volutpat eget. Quisque cursus elementum interdum. Morbi elementum nisi eu convallis aliquam. Nulla eu libero lacus. Curabitur mollis nec massa sit amet efficitur. Aliquam tincidunt nec ipsum sollicitudin dapibus. Donec at finibus nibh, ut efficitur elit. Vestibulum nec scelerisque magna. "
    val MANGA = MangaDto(
        id = "123",
        type = "manga",
        attributes = MangaAttributesDto(
            title = mapOf("en" to "My Manga"),
            description = mapOf("en" to LONG_TEXT),
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
                        group = "genre",
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
                        group = "genre",
                        version = 1
                    ),
                    relationships = emptyList()
                ),
                TagsDto(
                    id = "tag3",
                    type = "tag",
                    attributes = TagsAttributesDto(
                        name = mapOf("en" to "Comedy"),
                        description = mapOf("en" to "Manga with comedic elements"),
                        group = "genre",
                        version = 1
                    ),
                    relationships = emptyList()
                ),
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
        relationships = listOf(
            JSONObject(scanlationGroupJson)
        )
    )
    val MANGA_LIST = listOf(
        MANGA.copy(id = "0001"),
        MANGA.copy(id = "0002"),
        MANGA.copy(id = "0003"),
        MANGA.copy(id = "0004"),
        MANGA.copy(id = "0005"),
        MANGA.copy(id = "0006"),
    )
    val CHAPTER_LIST = listOf(
        CHAPTER.copy(id = "0001"),
        CHAPTER.copy(id = "0002"),
        CHAPTER.copy(id = "0003"),
        CHAPTER.copy(id = "0004"),
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

    val FEED_MAP_CHAPTERS = CHAPTER_LIST.mapIndexed { index, chapterDto ->
        Pair(chapterDto, index % 2 == 0)
    }
    val FEED_MAP_CHAPTERS2 = CHAPTER_LIST.toChapterList()
    val FEED_MAP = MANGA_LIST.toMangaList().associateWith { FEED_MAP_CHAPTERS2 }
    val SESSION = Session(
        token = "stub-token",
        refresh = "stub-refresh",
        expires = TOMORROW
    )

    object Responses {
        val GET_MANGA = GetMangaResponse(
            result = "success",
            response = "yeet",
            data = MANGA
        )

        val GET_MANGA_LIST = GetMangaListResponse(
            result = "success",
            response = "yeet",
            data = MANGA_LIST,
            limit = MANGA_LIST.size,
            offset = 0,
            total = MANGA_LIST.size
        )

        val GET_SEASONAL_DATA = GetSeasonalDataResponse(
            id = "some-id",
            name = "Winter",
            mangaIds = MANGA_LIST.map { it.id }
        )

        val GET_MANGA_AGGREGATE = GetMangaAggregateResponse(
            result = "success",
            volumes = mapOf(
                "abcd-1234" to VOLUME_AGGREGATE
            )
        )

        val GET_CHAPTER = GetChapterResponse(
            result = "success",
            response = "yeet",
            data = CHAPTER
        )

        val GET_CHAPTER_PAGES = GetChapterPagesResponse(
            result = "success",
            baseUrl = "http://example.com",
            chapter = ChapterPagesDataDto(
                hash = "abc-123",
                data = listOf("listofimagenames"),
                dataSaver = listOf("listofimagenamesdatasaver")
            )
        )

        val GET_CHAPTER_LIST = GetChapterListResponse(
            result = "success",
            response = "yeet",
            data = CHAPTER_LIST,
            limit = CHAPTER_LIST.size,
            offset = 0,
            total = CHAPTER_LIST.size,
        )

        val GET_CHAPTER_IDS = GetChapterIdsResponse(
            result = "success",
            data = CHAPTER_LIST.map { it.id }
        )

        val GET_USER_LISTS = GetUserListsResponse(
            result = "success",
            data = listOf(
                UserListDto(
                    id = "list-1234",
                    attributes = UserListAttributesDto(
                        name = "Stub List Name",
                        visibility = "public",
                        version = 1,
                    ),
                    relationships = listOf()
                )
            )
        )

        val GET_USER = GetUserResponse(
            result = "success",
            response = "yeet",
            data = UserDto(
                id = "user-id-1234",
                type = "user",
                attributes = UserAttributesDto(username = "Stub User"),
                relationships = listOf()
            )
        )
    }
}