package com.blanktheevil.mangareader.data.dto

import com.blanktheevil.mangareader.data.dto.objects.ChapterDto
import com.blanktheevil.mangareader.data.dto.objects.CoverArtDto
import com.blanktheevil.mangareader.data.dto.objects.MangaDto
import com.blanktheevil.mangareader.data.dto.objects.PersonDto
import com.blanktheevil.mangareader.data.dto.objects.ScanlationGroupDto
import com.blanktheevil.mangareader.data.dto.objects.TagsDto
import com.blanktheevil.mangareader.data.dto.objects.UserDto
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject

interface MangaDexObject<T> : GenericMangaDexObject {
    override val id: String
    override val type: String
    val attributes: T
    val relationships: RelationshipList?
}

interface GenericMangaDexObject {
    val id: String
    val type: String
}

@JsonClass(generateAdapter = true)
data class GenericRelationshipDto(
    override val id: String,
    override val type: String,
) : GenericMangaDexObject

class RelationshipList : ArrayList<GenericMangaDexObject>() {
    class Adapter : JsonAdapter<RelationshipList>() {
        private val moshi: Moshi = Moshi.Builder()
            .add(AdapterAdapter())
            .build()

        override fun toJson(writer: JsonWriter, list: RelationshipList?) {
            val jsonObjectList = list?.map {
                val jsonString = when (it) {
                    is PersonDto -> moshi.adapter(PersonDto::class.java)
                        .toJson(it)

                    is MangaDto -> moshi.adapter(MangaDto::class.java)
                        .toJson(it)

                    is ScanlationGroupDto -> moshi.adapter(ScanlationGroupDto::class.java)
                        .toJson(it)

                    is CoverArtDto -> moshi.adapter(CoverArtDto::class.java)
                        .toJson(it)

                    is ChapterDto -> moshi.adapter(ChapterDto::class.java)
                        .toJson(it)

                    is UserDto -> moshi.adapter(UserDto::class.java)
                        .toJson(it)

                    is TagsDto -> moshi.adapter(TagsDto::class.java)
                        .toJson(it)

                    is GenericRelationshipDto -> moshi.adapter(GenericRelationshipDto::class.java)
                        .toJson(it)

                    else -> throw IllegalArgumentException("Invalid type: ${it::class.java}")
                }

                JSONObject(jsonString)
            }

            jsonObjectList?.let {
                writer.value(
                    Buffer().writeUtf8(JSONArray(it).toString())
                )
            }
        }

        override fun fromJson(jsonReader: JsonReader): RelationshipList? {

            val jsonArray = JSONArray((jsonReader.readJsonValue() as ArrayList<JSONObject>))
            val list = RelationshipList()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val type = jsonObject.getString("type")

                val hasAttributes = jsonObject.has("attributes")

                val item = if (hasAttributes) {
                    when (type) {
                        "artist",
                        "author" -> moshi.adapter(PersonDto::class.java)
                            .fromJson(jsonObject.toString())

                        "manga" -> moshi.adapter(MangaDto::class.java)
                            .fromJson(jsonObject.toString())

                        "scanlation_group" -> moshi.adapter(ScanlationGroupDto::class.java)
                            .fromJson(jsonObject.toString())

                        "chapter" -> moshi.adapter(ChapterDto::class.java)
                            .fromJson(jsonObject.toString())

                        "cover_art" -> moshi.adapter(CoverArtDto::class.java)
                            .fromJson(jsonObject.toString())

                        "user" -> moshi.adapter(UserDto::class.java)
                            .fromJson(jsonObject.toString())

                        "tag" -> moshi.adapter(TagsDto::class.java)
                            .fromJson(jsonObject.toString())

                        else -> null
                    }
                } else {
                    null
                } ?: GenericRelationshipDto(
                    id = jsonObject.getString("id"),
                    type = type
                )

                list.add(item)
            }

            return list
        }
    }

    private class AdapterAdapter() {
        private val moshi = Moshi.Builder()
            .build()

        @ToJson
        fun toJson(list: RelationshipList): String {
            val type =
                Types.newParameterizedType(List::class.java, GenericMangaDexObject::class.java)

            return moshi.adapter<List<GenericRelationshipDto>>(type)
                .toJson(list.map { it as GenericRelationshipDto })
        }

        @FromJson
        fun fromJson(string: String): RelationshipList {
            val type =
                Types.newParameterizedType(List::class.java, GenericRelationshipDto::class.java)

            return moshi.adapter<List<GenericRelationshipDto>>(type)
                .fromJson(string) as RelationshipList
        }
    }

    fun <T> getByType(clazz: Class<T>): List<T> =
        this.filterIsInstance(clazz)

    fun <T> getFirstByType(clazz: Class<T>): T? =
        this.filterIsInstance(clazz).firstOrNull()

}

fun emptyRelationshipList() = RelationshipList()