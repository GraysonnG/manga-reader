package com.blanktheevil.mangareader.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.dto.utils.DataList
import com.squareup.moshi.JsonClass
import java.time.Instant

@Entity
@JsonClass(generateAdapter = true)
data class MangaListModel(
    @PrimaryKey
    override val key: String,
    override val data: DataList<Manga>,
    override val lastUpdated: Long = 0L,
) : BaseModel<DataList<Manga>>

enum class MangaListType {
    SEASONAL,
    POPULAR,
    RECENT,
    FOLLOWS,
    SEARCH,
}

fun DataList<Manga>.toModel(key: String): MangaListModel =
    MangaListModel(
        key = key,
        data = this,
        lastUpdated = Instant.now().toEpochMilli()
    )

fun DataList<Manga>.toModel(key: MangaListType): MangaListModel =
    MangaListModel(
        key = key.toString(),
        data = this,
        lastUpdated = Instant.now().toEpochMilli()
    )