package com.blanktheevil.mangareader.data.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blanktheevil.mangareader.data.Chapter
import com.blanktheevil.mangareader.data.UpdatedChapterList
import com.blanktheevil.mangareader.data.dto.utils.ChapterList
import java.time.Instant

@Entity
data class ChapterListModel(
    @PrimaryKey
    override val key: String,
    override val data: List<Chapter>,
    override val lastUpdated: Long = 0L,
) : BaseModel<List<Chapter>>

@Entity
data class ChapterListUpdatedModel(
    @PrimaryKey override val key: String,
    override val data: UpdatedChapterList,
    override val lastUpdated: Long = 0L
) : BaseModel<UpdatedChapterList>

fun ChapterList.toModel(key: String): ChapterListModel =
    ChapterListModel(
        key = key,
        data = this,
        lastUpdated = Instant.now().toEpochMilli()
    )

fun UpdatedChapterList.toModel(key: String): ChapterListUpdatedModel =
    ChapterListUpdatedModel(
        key = key,
        data = this,
        lastUpdated = Instant.now().toEpochMilli()
    )