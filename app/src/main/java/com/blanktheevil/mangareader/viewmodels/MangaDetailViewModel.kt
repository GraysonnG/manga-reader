package com.blanktheevil.mangareader.viewmodels

import androidx.lifecycle.ViewModel
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.AggregateVolumeDto
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.stores.MangaDetailDataStore
import com.blanktheevil.mangareader.data.stores.UserListsDataStore

class MangaDetailViewModel(
    private val mangaDexRepository: MangaDexRepository,
    val mangaDetail: MangaDetailDataStore,
    val userLists: UserListsDataStore,
) : ViewModel() {

    fun getMangaDetails(id: String) {
        mangaDetail.getById(id)
        userLists.get()
    }

    suspend fun getChaptersForVolume(volume: AggregateVolumeDto): List<ChapterDto> {
        val chapterIds = volume.chapters.map { (_, aggChapter) ->
            aggChapter.id
        }

        return when (val result = mangaDexRepository.getChapterList(ids = chapterIds)) {
            is Result.Success -> result.data.data
            is Result.Error -> emptyList()
        }
    }
}