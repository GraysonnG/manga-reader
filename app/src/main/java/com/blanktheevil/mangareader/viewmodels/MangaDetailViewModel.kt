package com.blanktheevil.mangareader.viewmodels

import androidx.lifecycle.ViewModel
import com.blanktheevil.mangareader.data.Chapter
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Volume
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

    suspend fun getChaptersByVolume(volume: Volume): List<Chapter> {
        return mangaDexRepository.getChapterList(
            volume.chapters.map { it.id }
        )
            .collectOrNull()?.map {
                it.copy(
                    isRead = it.id in mangaDetail.state.value.readIds
                )
            } ?: emptyList()
    }
}