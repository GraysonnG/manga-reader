package com.blanktheevil.mangareader.data.room

import androidx.room.TypeConverter
import com.blanktheevil.mangareader.data.DataList
import com.blanktheevil.mangareader.data.Manga
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class Converters {
    @TypeConverter
    fun mangaDataListToJson(
        value: DataList<Manga>
    ): String {
        val type = Types.newParameterizedType(DataList::class.java, Manga::class.java)

        return Moshi.Builder()
            .build()
            .adapter<DataList<Manga>>(type)
            .toJson(value)
    }

    @TypeConverter
    fun jsonToMangaDataList(
        value: String
    ): DataList<Manga>? {
        val type = Types.newParameterizedType(DataList::class.java, Manga::class.java)

        return Moshi.Builder()
            .build()
            .adapter<DataList<Manga>?>(type)
            .fromJson(value)
    }
}