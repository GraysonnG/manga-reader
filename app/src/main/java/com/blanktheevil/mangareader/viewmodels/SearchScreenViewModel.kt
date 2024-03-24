package com.blanktheevil.mangareader.viewmodels

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.blanktheevil.mangareader.data.Author
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.Tag
import com.blanktheevil.mangareader.data.TagsMode
import com.blanktheevil.mangareader.data.dto.utils.MangaList
import com.blanktheevil.mangareader.data.dto.utils.TagList
import com.blanktheevil.mangareader.ui.SORT_MAP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchScreenViewModel(
    val mangaDexRepository: MangaDexRepository,
) : ViewModel() {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val tagsJob = async {
                mangaDexRepository.getTags()
                    .onSuccess {
                        _tags.value = it
                    }
            }

            val searchJob = async {
                mangaDexRepository.getMangaSearch(
                    limit = 20,
                    title = ""
                ).onSuccess {
                    _mangaList.value = it.items
                    _uiState.value = uiState.value.copy(
                        loading = false
                    )
                }
            }

            listOf(tagsJob, searchJob)
                .forEach { it.await() }
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
            includedTags = includedTags.ifEmpty { null },
            excludedTags = excludedTags.ifEmpty { null },
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

    fun setFilterAuthors(list: List<Author>) {
        _filterState.value = _filterState.value.copy(
            authors = list.ifEmpty { null }
        )
    }

    fun setFilterArtists(list: List<Author>) {
        _filterState.value = _filterState.value.copy(
            artists = list.ifEmpty { null }
        )
    }

    fun setFilterDemographics(item: String) {
        _filterState.value.publicationDemographic?.let {
            val demographics = if (item in it) {
                it - item
            } else {
                it + item
            }

            _filterState.value = _filterState.value.copy(
                publicationDemographic = demographics.ifEmpty { null }
            )
        } ?: run {
            _filterState.value = _filterState.value.copy(
                publicationDemographic = listOf(item)
            )
        }
    }

    fun setFilterYear(item: Int?) {
        _filterState.value = _filterState.value.copy(
            year = item?.toString()
        )
    }

    fun setFilterContentRatings(item: String) {
        _filterState.value.contentRating.let {
            val ratings = if (item in it) {
                it - item
            } else {
                it + item
            }

            _filterState.value = _filterState.value.copy(
                contentRating = ratings
            )
        }
    }

    fun setFilterSortBy(key: String) {
        _filterState.value = _filterState.value.copy(
            order = key
        )
    }

    fun setFilterStatus(item: String) {
        _filterState.value.status?.let {
            val status = if (item in it) {
                it - item
            } else {
                it + item
            }

            _filterState.value = _filterState.value.copy(
                status = status.ifEmpty { null }
            )
        } ?: run {
            _filterState.value = _filterState.value.copy(
                status = listOf(item)
            )
        }
    }

    fun submit() {
        Log.d("FilterState", _filterState.value.toString())
        val state = _filterState.value

        _uiState.value = uiState.value.copy(
            loading = true
        )
        CoroutineScope(Dispatchers.IO).launch {
            mangaDexRepository.getMangaSearch(
                limit = state.limit,
                offset = state.offset,
                title = state.title,
                contentRating = state.contentRating,
                order = SORT_MAP[state.order],
                publicationDemographic = state.publicationDemographic,
                status = state.status,
                includedTags = state.includedTags?.map { it.id },
                excludedTags = state.excludedTags?.map { it.id },
                includedTagsMode = state.includedTagsMode,
                excludedTagsMode = state.excludedTagsMode,
                authors = state.authors?.map { it.id },
                artists = state.artists?.map { it.id },
                year = state.year
            ).onSuccess {
                _mangaList.value = it.items
                _uiState.value = uiState.value.copy(
                    loading = false
                )
            }.onError {
                Log.e("getMangaSearch", it.message.toString())
            }
        }
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    suspend fun getAuthorList(name: String): Result<List<Author>> =
        mangaDexRepository.getAuthorList(name, 10)

    @Immutable
    data class State(
        val mangaList: MangaList = emptyList(),
        val loading: Boolean = true,
    )

    @Immutable
    data class FilterState(
        val visible: Boolean = false,
        val title: String = "",
        val limit: Int = 20,
        val offset: Int = 0,
        val contentRating: List<String> = emptyList(),
        val order: String = SORT_MAP.keys.elementAt(1),
        val publicationDemographic: List<String>? = null,
        val status: List<String>? = null,
        val includedTags: List<Tag>? = null,
        val excludedTags: List<Tag>? = null,
        val includedTagsMode: TagsMode = TagsMode.AND,
        val excludedTagsMode: TagsMode = TagsMode.OR,
        val authors: List<Author>? = null,
        val artists: List<Author>? = null,
        val year: String? = null,
    ) {
        fun isModified(): Boolean =
            DEFAULT_FILTERS.let {
                this.title == it.title &&
                        this.contentRating == it.contentRating &&
                        this.order == it.order &&
                        this.publicationDemographic == it.publicationDemographic &&
                        this.status == it.status &&
                        this.includedTags == it.includedTags &&
                        this.excludedTags == it.excludedTags &&
                        this.includedTagsMode == it.includedTagsMode &&
                        this.excludedTagsMode == it.excludedTagsMode &&
                        this.authors == it.authors &&
                        this.artists == it.artists &&
                        this.year == it.year
            }.not()
    }

    companion object {
        private val DEFAULT_FILTERS = FilterState()
    }
}