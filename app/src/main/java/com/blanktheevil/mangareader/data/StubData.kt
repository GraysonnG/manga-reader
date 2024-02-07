package com.blanktheevil.mangareader.data

import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.blanktheevil.mangareader.data.dto.emptyRelationshipList
import com.blanktheevil.mangareader.data.dto.objects.ChapterDto
import com.blanktheevil.mangareader.data.dto.objects.MangaDto
import com.blanktheevil.mangareader.data.dto.objects.ScanlationGroupDto
import com.blanktheevil.mangareader.data.dto.objects.TagsDto
import com.blanktheevil.mangareader.data.dto.objects.UserDto
import com.blanktheevil.mangareader.data.dto.objects.UserListDto
import com.blanktheevil.mangareader.data.dto.responses.AggregateChapterDto
import com.blanktheevil.mangareader.data.dto.responses.AggregateVolumeDto
import com.blanktheevil.mangareader.data.dto.responses.GetChapterIdsResponse
import com.blanktheevil.mangareader.data.dto.responses.GetChapterListResponse
import com.blanktheevil.mangareader.data.dto.responses.GetChapterPagesResponse
import com.blanktheevil.mangareader.data.dto.responses.GetChapterResponse
import com.blanktheevil.mangareader.data.dto.responses.GetMangaAggregateResponse
import com.blanktheevil.mangareader.data.dto.responses.GetMangaListResponse
import com.blanktheevil.mangareader.data.dto.responses.GetMangaResponse
import com.blanktheevil.mangareader.data.dto.responses.GetSeasonalDataResponse
import com.blanktheevil.mangareader.data.dto.responses.GetUserListsResponse
import com.blanktheevil.mangareader.data.dto.responses.GetUserResponse
import com.blanktheevil.mangareader.data.dto.utils.chapter.toChapterList
import com.blanktheevil.mangareader.data.dto.utils.manga.toMangaList
import com.blanktheevil.mangareader.data.session.Session
import java.util.Calendar
import java.util.Date
import java.util.UUID

object StubData {
    private val TOMORROW = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
    }.time

    object Data {
        const val LONG_TEXT =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus finibus porta mauris, non placerat justo. Nulla aliquet venenatis mi, et hendrerit mauris volutpat eget. Quisque cursus elementum interdum. Morbi elementum nisi eu convallis aliquam. Nulla eu libero lacus. Curabitur mollis nec massa sit amet efficitur. Aliquam tincidunt nec ipsum sollicitudin dapibus. Donec at finibus nibh, ut efficitur elit. Vestibulum nec scelerisque magna. "

        //create a scanlationgroup object from the json
        private val scanlationGroup1 = ScanlationGroupDto(
            id = "155d7139-8d9a-49eb-bceb-d5e26db08b72",
            type = "scanlation_group",
            attributes = ScanlationGroupDto.Attributes(
                name = "Ecchi No Doujinshi Scans",
                altNames = emptyList(),
                website = "https://www.patreon.com/luigiymario2",
            ),
            relationships = emptyRelationshipList()
        )

        private val scanlationGroup2 = ScanlationGroupDto(
            id = "155d7139-8d9a-49eb-bceb-d5e26db08b80",
            type = "scanlation_group",
            attributes = ScanlationGroupDto.Attributes(
                name = "Blanky Scans",
                altNames = emptyList(),
                website = "https://www.patreon.com/luigiymario2",
            ),
            relationships = emptyRelationshipList()
        )

        val MANGA = MangaDto(
            id = "123",
            type = "manga",
            attributes = MangaDto.Attributes(
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
                        attributes = TagsDto.Attributes(
                            name = mapOf("en" to "Action"),
                            description = mapOf("en" to "Manga with action scenes"),
                            group = "genre",
                            version = 1
                        ),
                        relationships = emptyRelationshipList()
                    ),
                    TagsDto(
                        id = "tag2",
                        type = "tag",
                        attributes = TagsDto.Attributes(
                            name = mapOf("en" to "Romance"),
                            description = mapOf("en" to "Manga with romantic elements"),
                            group = "genre",
                            version = 1
                        ),
                        relationships = emptyRelationshipList()
                    ),
                    TagsDto(
                        id = "tag3",
                        type = "tag",
                        attributes = TagsDto.Attributes(
                            name = mapOf("en" to "Comedy"),
                            description = mapOf("en" to "Manga with comedic elements"),
                            group = "genre",
                            version = 1
                        ),
                        relationships = emptyRelationshipList()
                    ),
                ),
                state = "published",
                createdAt = null,
                updatedAt = null,
                latestUploadedChapter = "Chapter 50"
            ),
            relationships = emptyRelationshipList()
        )
        val CHAPTER = ChapterDto(
            id = "4c1e62ec-8f54-4d88-97d6-bf9e5683d1b8",
            type = "chapter",
            attributes = ChapterDto.Attributes(
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
            relationships = RelationshipList().apply {
                add(scanlationGroup1)
            }
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
            CHAPTER.copy(
                id = "0001",
                attributes = CHAPTER.attributes.copy(volume = "1", chapter = "1")
            ),
            CHAPTER.copy(
                id = "0002",
                attributes = CHAPTER.attributes.copy(volume = "1", chapter = "2")
            ),
            CHAPTER.copy(
                id = "0003",
                attributes = CHAPTER.attributes.copy(volume = "2", chapter = "1")
            ),
            CHAPTER.copy(
                id = "0004",
                attributes = CHAPTER.attributes.copy(volume = "2", chapter = "2")
            ),
            CHAPTER.copy(
                id = "0004",
                attributes = CHAPTER.attributes.copy(volume = "2", chapter = "2"),
                relationships = RelationshipList().apply {
                    add(scanlationGroup2)
                }
            ),
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

        private val FEED_MAP_CHAPTERS2 = CHAPTER_LIST.toChapterList()
        val FEED_MAP = MANGA_LIST.toMangaList().associateWith { FEED_MAP_CHAPTERS2 }
        val SESSION = Session(
            token = "stub-token",
            refresh = "stub-refresh",
            expires = TOMORROW
        )

        val TAGS = listOf(
            Tag("id1", "Tag 1", "Group 1"),
            Tag("id2", "Tag 2", "Group 1"),
            Tag("id3", "Tag 3", "Group 2"),
            Tag("id4", "Tag 4", "Group 2"),
            Tag("id5", "Tag 5", null),
            Tag("id6", "Tag 6", "Group 1"),
            Tag("id7", "Tag 7", "Group 1"),
            Tag("id8", "Tag 8", "Group 2"),
            Tag("id9", "Tag 9", "Group 2"),
            Tag("id10", "Tag 10", null),
            Tag("id11", "Tag 11", "Group 1"),
            Tag("id12", "Tag 12", "Group 1"),
            Tag("id13", "Tag 13", "Group 2"),
            Tag("id14", "Tag 14", "Group 2"),
            Tag("id15", "Tag 15", null),
            Tag("id16", "Tag 16", "Group 1"),
            Tag("id17", "Tag 17", "Group 1"),
            Tag("id18", "Tag 18", "Group 2"),
            Tag("id19", "Tag 19", "Group 2"),
            Tag("id20", "Tag 20", null),
            Tag("id21", "Tag 21", "Group 1"),
            Tag("id22", "Tag 22", "Group 1"),
            Tag("id23", "Tag 23", "Group 2"),
            Tag("id24", "Tag 24", "Group 2"),
            Tag("id25", "Tag 25", null),
            Tag("id26", "Tag 26", "Group 1"),
            Tag("id27", "Tag 27", "Group 1"),
            Tag("id28", "Tag 28", "Group 2"),
            Tag("id29", "Tag 29", "Group 2"),
            Tag("id30", "Tag 30", null),
            Tag("id31", "Tag 31", "Group 1"),
            Tag("id32", "Tag 32", "Group 1"),
            Tag("id33", "Tag 33", "Group 2"),
            Tag("id34", "Tag 34", "Group 2"),
            Tag("id35", "Tag 35", null),
            Tag("id36", "Tag 36", "Group 1"),
            Tag("id37", "Tag 37", "Group 1"),
            Tag("id38", "Tag 38", "Group 2"),
            Tag("id39", "Tag 39", "Group 2"),
            Tag("id40", "Tag 40", null),
        )
    }

    object Responses {
        val GET_MANGA = GetMangaResponse(
            result = "success",
            response = "yeet",
            data = Data.MANGA
        )

        val GET_MANGA_LIST = GetMangaListResponse(
            data = Data.MANGA_LIST,
            limit = Data.MANGA_LIST.size,
            offset = 0,
            total = Data.MANGA_LIST.size
        )

        val GET_SEASONAL_DATA = GetSeasonalDataResponse(
            id = "some-id",
            name = "Winter",
            mangaIds = Data.MANGA_LIST.map { it.id }
        )

        val GET_MANGA_AGGREGATE = GetMangaAggregateResponse(
            result = "success",
            volumes = mapOf(
                "abcd-1234" to Data.VOLUME_AGGREGATE
            )
        )

        val GET_CHAPTER = GetChapterResponse(
            data = Data.CHAPTER
        )

        val GET_CHAPTER_PAGES = GetChapterPagesResponse(
            result = "success",
            baseUrl = "http://example.com",
            chapter = GetChapterPagesResponse.ChapterPagesDataDto(
                hash = "abc-123",
                data = listOf("listofimagenames"),
                dataSaver = listOf("listofimagenamesdatasaver")
            )
        )

        val GET_CHAPTER_LIST = GetChapterListResponse(
            data = Data.CHAPTER_LIST,
            limit = Data.CHAPTER_LIST.size,
            offset = 0,
            total = Data.CHAPTER_LIST.size,
        )

        val GET_CHAPTER_IDS = GetChapterIdsResponse(
            result = "success",
            data = Data.CHAPTER_LIST.map { it.id }
        )

        val GET_USER_LISTS = GetUserListsResponse(
            data = listOf(
                UserListDto(
                    id = "list-1234",
                    type = "user_list",
                    attributes = UserListDto.Attributes(
                        name = "Stub List Name",
                        visibility = "public",
                        version = 1,
                    ),
                    relationships = emptyRelationshipList()
                )
            ),
            limit = 100,
            offset = 0,
            total = 1,
        )

        val GET_USER = GetUserResponse(
            data = UserDto(
                id = "user-id-1234",
                type = "user",
                attributes = UserDto.Attributes(username = "Stub User"),
                relationships = emptyRelationshipList()
            )
        )
    }
}