package com.blanktheevil.mangareader.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.blanktheevil.mangareader.data.room.models.MangaListModel
import com.blanktheevil.mangareader.data.room.models.MangaListType

@Dao
interface MangaDao {
    suspend fun getMangaList(type: MangaListType): MangaListModel? {
        return getMangaList(type.toString())
    }

    @Query("SELECT * FROM MangaListModel WHERE `key` = :key")
    suspend fun getMangaList(key: String): MangaListModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: MangaListModel)

    @Query("DELETE FROM MangaListModel WHERE `key` = :key")
    suspend fun clearList(key: String)

    suspend fun clearList(type: MangaListType) =
        clearList(type.toString())

}






