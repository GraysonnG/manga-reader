package com.blanktheevil.mangareader.viewmodels

import androidx.lifecycle.ViewModel
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.MangaList
import com.blanktheevil.mangareader.data.Tag
import com.blanktheevil.mangareader.data.TagsMode
import com.blanktheevil.mangareader.data.dto.TagList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchScreenViewModel(
    mangaDexRepository: MangaDexRepository,
) : ViewModel() {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            mangaDexRepository.getTags()
                .onSuccess {
                    _tags.value = it
                }
        }

        CoroutineScope(Dispatchers.IO).launch {
            mangaDexRepository.getMangaSearch(
                limit = 20,
                title = ""
            ).onSuccess {
                _mangaList.value = it.items
            }
        }
    }

    private val _uiState = MutableStateFlow(State())
    private val _filterState = MutableStateFlow(FilterState())
    private val _tags = MutableStateFlow(emptyList<Tag>())
    private val _mangaList = MutableStateFlow(emptyList<Manga>())
    val uiState = _uiState.asStateFlow()
    val filterState = _filterState.asStateFlow()
    val tags: StateFlow<TagList> = _tags.asStateFlow()
    val mangaList: StateFlow<MangaList> = _mangaList.asStateFlow()

    fun setFilterTitle(value: String) {
        _filterState.value = _filterState.value.copy(
            title = value
        )
    }

    fun setFilterVisible(value: Boolean = true) {
        _filterState.value = _filterState.value.copy(
            visible = value
        )
    }

    fun setFilterTags(
        includedTags: List<Tag>,
        excludedTags: List<Tag>,
    ) {
        _filterState.value = _filterState.value.copy(
            includedTagIds = includedTags.map { it.id },
            excludedTagIds = excludedTags.map { it.id },
        )
    }

    fun setFilterTagModes(
        includedTagsMode: TagsMode,
        excludedTagsMode: TagsMode,
    ) {
        _filterState.value = _filterState.value.copy(
            includedTagsMode = includedTagsMode,
            excludedTagsMode = excludedTagsMode,
        )
    }

    data class State(
        val mangaList: MangaList = emptyList(),
    )

    data class FilterState(
        val visible: Boolean = false,
        val title: String = "",
        val limit: Int = 20,
        val offset: Int = 0,
        val contentRating: List<String> = emptyList(),
        val order: List<String> = listOf("desc"),
        val publicationDemographic: List<String>? = null,
        val status: List<String>? = null,
        val includedTagIds: List<String>? = null,
        val excludedTagIds: List<String>? = null,
        val includedTagsMode: TagsMode = TagsMode.AND,
        val excludedTagsMode: TagsMode = TagsMode.OR,
        val authors: List<String>? = null,
        val artists: List<String>? = null,
        val year: String? = null,
    )
}