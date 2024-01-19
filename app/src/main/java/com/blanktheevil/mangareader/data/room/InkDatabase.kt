package com.blanktheevil.mangareader.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.blanktheevil.mangareader.data.room.dao.MangaDao
import com.blanktheevil.mangareader.data.room.models.MangaListModel

@Database(
    entities = [MangaListModel::class], version = 1
)
@TypeConverters(Converters::class)
abstract class InkDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
}