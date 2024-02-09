package com.blanktheevil.mangareader.data.room

import androidx.room.TypeConverter
import com.blanktheevil.mangareader.data.Chapter
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.UpdatedChapterList
import com.blanktheevil.mangareader.data.dto.utils.DataList
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.json.JSONObject

class Converters {
    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun mangaDataListToJson(
        value: DataList<Manga>
    ): String {
        val type = Types.newParameterizedType(DataList::class.java, Manga::class.java)

        return moshi
            .adapter<DataList<Manga>>(type)
            .toJson(value)
    }

    @TypeConverter
    fun jsonToMangaDataList(
        value: String
    ): DataList<Manga>? {
        val type = Types.newParameterizedType(DataList::class.java, Manga::class.java)

        return moshi
            .adapter<DataList<Manga>?>(type)
            .fromJson(value)
    }

    @TypeConverter
    fun mangaToJson(
        value: Manga,
    ): String {
        return moshi
            .adapter(Manga::class.java)
            .toJson(value)
    }

    @TypeConverter
    fun jsonToManga(
        value: String
    ): Manga? {
        return moshi
            .adapter(Manga::class.java)
            .fromJson(value)
    }

    @TypeConverter
    fun chapterListToJson(
        value: List<Chapter>,
    ): String {

        val type = Types.newParameterizedType(List::class.java, Chapter::class.java)

        return moshi
            .adapter<List<Chapter>>(type)
            .toJson(value)
    }

    @TypeConverter
    fun jsonToChapterList(
        value: String,
    ): List<Chapter>? {
        val type = Types.newParameterizedType(List::class.java, Chapter::class.java)

        return moshi
            .adapter<List<Chapter>>(type)
            .fromJson(value)
    }

    @TypeConverter
    fun updatedChapterListToJson(
        value: UpdatedChapterList,
    ): String {

        val total = value.total

        val data = JSONObject()
            .put(
                "manga", moshi.adapter<List<Manga>>(
                    Types.newParameterizedType(List::class.java, Manga::class.java)
                ).toJson(value.data.keys.toList())
            ) // string of list of manga
            .put(
                "chapters", moshi.adapter<List<List<Chapter>>>(
                    Types.newParameterizedType(
                        List::class.java,
                        List::class.java,
                        Chapter::class.java
                    )
                ).toJson(value.data.values.toList())
            )

        val ucl = JSONObject()
            .put("data", data.toString())
            .put("total", total)

        return ucl.toString()
    }

    @TypeConverter
    fun jsonToUpdatedChapterList(
        value: String,
    ): UpdatedChapterList {
        val valueObj = JSONObject(value)

        val obj = Pair<String, Int>(
            valueObj.getString("data"),
            valueObj.getInt("total")
        )

        val mangaList = JSONObject(obj.first).getString("manga").let {
            moshi.adapter<List<Manga>>(
                Types.newParameterizedType(List::class.java, Manga::class.java)
            ).fromJson(it)
        } ?: emptyList()

        val chapterListList = JSONObject(obj.first).getString("chapters").let {
            moshi.adapter<List<List<Chapter>>>(
                Types.newParameterizedType(List::class.java, List::class.java, Chapter::class.java)
            ).fromJson(it)
        } ?: emptyList()

        val out = mangaList.associateWith { manga ->
            chapterListList.flatten().filter { c -> c.relatedManga?.id == manga.id }
        }

        return UpdatedChapterList(
            total = obj.second,
            data = out
        )
    }
}