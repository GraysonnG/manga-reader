package com.blanktheevil.mangareader.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.blanktheevil.mangareader.data.room.models.ChapterListModel
import com.blanktheevil.mangareader.data.room.models.ChapterListUpdatedModel

@Dao
interface ChapterDao {
    @Query("SELECT * FROM ChapterListModel WHERE `key` = :key")
    suspend fun getChapterList(key: String): ChapterListModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapterList(data: ChapterListModel)

    @Query("DELETE FROM ChapterListModel WHERE `key` = :key")
    suspend fun clearChapterList(key: String)

    @Query("SELECT * FROM ChapterListUpdatedModel WHERE `key` = :key")
    suspend fun getUpdatedChapterList(key: String): ChapterListUpdatedModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdatedChapterList(data: ChapterListUpdatedModel)

    @Query("DELETE FROM ChapterListUpdatedModel WHERE `key` = :key")
    suspend fun clearUpdatedChapterList(key: String)
}