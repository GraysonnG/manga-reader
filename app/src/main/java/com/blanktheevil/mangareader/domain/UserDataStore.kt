package com.blanktheevil.mangareader.domain

import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class UserDataStore(
    private val mangaDexRepository: MangaDexRepository,
    private val viewModelScope: CoroutineScope,
) : DataStore<UserDataState>(
    UserDataState()
) {
    override fun get() {
        getUserId {
            getUserData(it)
        }
    }

    override fun onRefresh() {
        _state.value = _state.value.copy(
            error = null,
        )
    }

    private fun getUserId(onSuccess: (String) -> Unit) {
        when (val result = mangaDexRepository.getUserId()) {
            is Result.Success -> {
                onSuccess(result.data)
            }

            is Result.Error -> {
                _state.value = _state.value.copy(
                    error = SimpleUIError(
                        title = "Error fetching user id",
                        throwable = result.error,
                    )
                )
            }
        }
    }

    private fun getUserData(userId: String) {
        viewModelScope.launch {
            when (val result = mangaDexRepository.getUserData(userId)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        username = result.data.attributes.username
                    )
                }

                is Result.Error -> {
                    _state.value = _state.value.copy(
                        error = SimpleUIError(
                            title = "Error fetching user data",
                            throwable = result.error,
                        )
                    )
                }
            }
        }
    }

    data class State(
        val username: String = "",
        val error: UIError? = null,
    )
}