package com.blanktheevil.mangareader.data.session

interface SessionManager {
    var session: Session?

    class InvalidSessionException(message: String) : Exception(message)
}