package com.blanktheevil.mangareader.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.blanktheevil.mangareader.data.room.dao.ChapterDao
import com.blanktheevil.mangareader.data.room.dao.MangaDao
import com.blanktheevil.mangareader.data.room.models.ChapterListModel
import com.blanktheevil.mangareader.data.room.models.ChapterListUpdatedModel
import com.blanktheevil.mangareader.data.room.models.MangaListModel
import com.blanktheevil.mangareader.data.room.models.MangaModel

@Database(
    version = 3,
    exportSchema = true,
    entities = [
        MangaListModel::class,
        MangaModel::class,
        ChapterListModel::class,
        ChapterListUpdatedModel::class,
    ],
)
@TypeConverters(Converters::class)
abstract class InkDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun chapterDao(): ChapterDao

    companion object {
        const val NAME = "ink-database"
    }
}