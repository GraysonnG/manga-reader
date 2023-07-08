package com.blanktheevil.mangareader.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.AggregateVolumeDto
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.domain.MangaDetailDataStore
import com.blanktheevil.mangareader.domain.UserListsDataStore

class MangaDetailViewModel : ViewModel() {
    private val mangaDexRepository: MangaDexRepository = MangaDexRepository()

    val mangaDetail = MangaDetailDataStore(mangaDexRepository)
    val userLists = UserListsDataStore(mangaDexRepository)

    fun getMangaDetails(id: String, context: Context) {
        mangaDexRepository.initRepositoryManagers(context)
        mangaDetail.getById(id)
        userLists.get()
    }

    suspend fun getChaptersForVolume(volume: AggregateVolumeDto): List<ChapterDto> {
        val chapterIds = volume.chapters.map { (_, aggChapter) ->
            aggChapter.id
        }

        return when (val result = mangaDexRepository.getChapterList(ids = chapterIds)) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
    }
}