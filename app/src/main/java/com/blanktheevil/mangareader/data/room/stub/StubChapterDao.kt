package com.blanktheevil.mangareader.data.room.stub

import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.utils.chapter.toChapterList
import com.blanktheevil.mangareader.data.room.dao.ChapterDao
import com.blanktheevil.mangareader.data.room.models.ChapterListModel
import com.blanktheevil.mangareader.data.room.models.ChapterListUpdatedModel
import com.blanktheevil.mangareader.data.room.models.toModel

class StubChapterDao : ChapterDao {
    override suspend fun getChapterList(key: String): ChapterListModel? {
        return StubData.Data.CHAPTER_LIST.toChapterList().toModel(key)
    }

    override suspend fun insertChapterList(data: ChapterListModel) {

    }

    override suspend fun clearChapterList(key: String) {

    }

    override suspend fun getUpdatedChapterList(key: String): ChapterListUpdatedModel? {
        return null
    }

    override suspend fun insertUpdatedChapterList(data: ChapterListUpdatedModel) {

    }

    override suspend fun clearUpdatedChapterList(key: String) {

    }
}