package com.blanktheevil.mangareader.data.room.stub

import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.room.dao.MangaDao
import com.blanktheevil.mangareader.data.room.models.MangaListModel
import com.blanktheevil.mangareader.data.room.models.toModel
import com.blanktheevil.mangareader.data.toDataList

class StubMangaDao : MangaDao {
    override suspend fun getMangaList(key: String): MangaListModel? {
        return StubData.Responses.GET_MANGA_LIST.toDataList().toModel(key)
    }

    override suspend fun insert(data: MangaListModel) {
        // Do Nothing
    }

    override suspend fun clearList(key: String) {
        // Do Nothing
    }
}