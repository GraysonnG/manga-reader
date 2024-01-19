package com.blanktheevil.mangareader.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.blanktheevil.mangareader.data.DataList
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.room.models.MangaListModel
import com.blanktheevil.mangareader.data.room.models.MangaListType
import com.blanktheevil.mangareader.data.room.models.MangaModel
import com.blanktheevil.mangareader.data.room.models.toModel

@Dao
interface MangaDao {
    suspend fun getMangaList(type: MangaListType): MangaListModel? {
        return getMangaList(type.toString())
    }

    @Query("SELECT * FROM MangaListModel WHERE `key` = :key")
    suspend fun getMangaList(key: String): MangaListModel?

    @Query("SELECT * FROM MangaModel WHERE `key` = :key")
    suspend fun getManga(key: String): MangaModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(data: MangaListModel)

    suspend fun insertList(key: MangaListType, data: DataList<Manga>) {
        insertList(key.toString(), data)
    }

    suspend fun insertList(key: String, data: DataList<Manga>) {
        data.items.forEach {
            insertManga(it.toModel())
        }

        insertList(data.toModel(key))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManga(data: MangaModel)

    suspend fun insertManga(data: Manga) {
        insertManga(data.toModel())
    }

    @Query("DELETE FROM MangaListModel WHERE `key` = :key")
    suspend fun clearList(key: String)

    suspend fun clearList(type: MangaListType) =
        clearList(type.toString())

}






