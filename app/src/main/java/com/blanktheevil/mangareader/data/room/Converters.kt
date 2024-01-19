package com.blanktheevil.mangareader.data.room

import androidx.room.TypeConverter
import com.blanktheevil.mangareader.data.DataList
import com.blanktheevil.mangareader.data.Manga
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

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
}