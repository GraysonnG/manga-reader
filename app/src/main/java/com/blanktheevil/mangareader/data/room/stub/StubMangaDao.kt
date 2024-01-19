package com.blanktheevil.mangareader.data.room.stub

import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.room.dao.MangaDao
import com.blanktheevil.mangareader.data.room.models.MangaListModel
import com.blanktheevil.mangareader.data.room.models.MangaModel
import com.blanktheevil.mangareader.data.room.models.toModel
import com.blanktheevil.mangareader.data.toDataList
import com.blanktheevil.mangareader.data.toManga

class StubMangaDao : MangaDao {
    override suspend fun getMangaList(key: String): MangaListModel? {
        return StubData.Responses.GET_MANGA_LIST.toDataList().toModel(key)
    }

    override suspend fun getManga(key: String): MangaModel? {
        return StubData.MANGA.toManga().toModel()
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
}