package com.blanktheevil.mangareader.data.dto

import com.blanktheevil.mangareader.adapters.JSONObjectAdapter
import com.blanktheevil.mangareader.data.dto.objects.ChapterDto
import com.blanktheevil.mangareader.data.dto.objects.CoverArtDto
import com.blanktheevil.mangareader.data.dto.objects.MangaDto
import com.blanktheevil.mangareader.data.dto.objects.PersonDto
import com.blanktheevil.mangareader.data.dto.objects.ScanlationGroupDto
import com.blanktheevil.mangareader.data.dto.objects.TagsDto
import com.blanktheevil.mangareader.data.dto.objects.UserDto
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

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
            .add(JSONObject::class.java, JSONObjectAdapter())
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(RelationshipList::class.java, AdapterAdapter())
            .build()

        private val types = mapOf(
            "artist" to PersonDto::class.java,
            "author" to PersonDto::class.java,
            "manga" to MangaDto::class.java,
            "scanlation_group" to ScanlationGroupDto::class.java,
            "chapter" to ChapterDto::class.java,
            "cover_art" to CoverArtDto::class.java,
            "user" to UserDto::class.java,
            "tag" to TagsDto::class.java,
        )

        private inline fun <reified T : GenericMangaDexObject> T.toJson(): JSONObject =
            JSONObject(moshi.adapter(T::class.java).toJson(this))

        private fun <T : GenericMangaDexObject> JSONObject.fromJson(clazz: Class<T>): T =
            moshi.adapter(clazz).fromJson(this.toString())!!

        private fun RelationshipList?.toJson(): List<JSONObject> =
            this?.map { it.toJson() } ?: emptyList()

        override fun toJson(writer: JsonWriter, list: RelationshipList?) {
            writer.value(Buffer().writeUtf8(JSONArray(list.toJson()).toString()))
        }

        @Suppress("unchecked_cast")
        override fun fromJson(jsonReader: JsonReader): RelationshipList {
            val jsonArray = JSONArray((jsonReader.readJsonValue() as ArrayList<JSONObject>))
            val list = RelationshipList()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val type = jsonObject.getString("type")
                val hasAttributes = jsonObject.has("attributes")
                val relationship = types.getOrDefault(type, GenericRelationshipDto::class.java)
                    .let {
                        if (hasAttributes)
                            jsonObject.fromJson(it)
                        else
                            GenericRelationshipDto(
                                id = jsonObject.getString("id"),
                                type = type
                            )
                    }

                list.add(relationship)
            }

            return list
        }
    }

    private class AdapterAdapter : JsonAdapter<RelationshipList>() {

        @Suppress("unchecked_cast")
        override fun fromJson(reader: JsonReader): RelationshipList {
            val jsonArray = JSONArray((reader.readJsonValue() as ArrayList<JSONObject>))
            val list = RelationshipList()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                list.add(
                    GenericRelationshipDto(
                        id = jsonObject.getString("id"),
                        type = jsonObject.getString("type"),
                    )
                )
            }

            return list
        }

        override fun toJson(writer: JsonWriter, list: RelationshipList?) {
            val jsonObjectList = list?.map {
                JSONObject().apply {
                    put("id", it.id)
                    put("type", it.type)
                }
            }

            writer.value(Buffer().writeUtf8(JSONArray(jsonObjectList).toString()))
        }
    }

    fun <T> getByType(clazz: Class<T>): List<T> =
        this.filterIsInstance(clazz)

    fun <T> getFirstByType(clazz: Class<T>): T? =
        this.filterIsInstance(clazz).firstOrNull()

}

fun emptyRelationshipList() = RelationshipList()