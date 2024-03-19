package com.blanktheevil.mangareader.data.room.stub

import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.utils.manga.toDataList
import com.blanktheevil.mangareader.data.dto.utils.manga.toManga
import com.blanktheevil.mangareader.data.room.dao.MangaDao
import com.blanktheevil.mangareader.data.room.models.MangaListModel
import com.blanktheevil.mangareader.data.room.models.MangaModel
import com.blanktheevil.mangareader.data.room.models.toModel

class StubMangaDao : MangaDao {
    override suspend fun getMangaList(key: String): MangaListModel {
        return StubData.Responses.GET_MANGA_LIST.toDataList().toModel(key)
    }

    override suspend fun getManga(key: String): MangaModel {
        return StubData.Data.MANGA.toManga().toModel()
    }

    override suspend fun getMangaList(keys: List<String>): List<MangaModel> {
        return listOf(
            StubData.Data.MANGA.toManga().toModel()
        )
    }

    override suspend fun insertList(data: MangaListModel) {
        // Do Nothing
    }

    override suspend fun insertManga(data: MangaModel) {
        // Do Nothing
    }

    override suspend fun clearList(key: String) {
        // Do Nothing
    }

    override suspend fun getSearchLists(): List<MangaListModel> {
        return emptyList()
    }

    override suspend fun clearLists(keys: List<String>) {

    }
}