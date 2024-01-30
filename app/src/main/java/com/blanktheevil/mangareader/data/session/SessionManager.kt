package com.blanktheevil.mangareader.data.session

import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    var session: Session?

    // Haha this is tech debt
    val isLoggedIn: StateFlow<Boolean>

    class InvalidSessionException(message: String) : Exception(message)
}