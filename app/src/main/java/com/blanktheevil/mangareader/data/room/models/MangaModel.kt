package com.blanktheevil.mangareader.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blanktheevil.mangareader.data.Manga
import com.squareup.moshi.JsonClass
import java.time.Instant

@Entity
@JsonClass(generateAdapter = true)
data class MangaModel(
    @PrimaryKey
    override val key: String,
    override val data: Manga,
    override val lastUpdated: Long,
) : BaseModel<Manga>

fun Manga.toModel(): MangaModel =
    MangaModel(
        key = this.id,
        data = this,
        lastUpdated = Instant.now().toEpochMilli()
    )