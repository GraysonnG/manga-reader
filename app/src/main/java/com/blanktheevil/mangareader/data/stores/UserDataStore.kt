package com.blanktheevil.mangareader.data.stores

import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.domain.UserDataState
import kotlinx.coroutines.launch

class UserDataStore(
    private val mangaDexRepository: MangaDexRepository,
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
        dataStoreScope.launch {
            when (val result = mangaDexRepository.getCurrentUserId()) {
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
    }

    private fun getUserData(userId: String) {
        dataStoreScope.launch {
            when (val result = mangaDexRepository.getUserData(userId)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        username = result.data.data.attributes.username,
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
        override val loading: Boolean = false,
        override val error: UIError? = null,
        val username: String = "",
    ) : DataStoreState()
}