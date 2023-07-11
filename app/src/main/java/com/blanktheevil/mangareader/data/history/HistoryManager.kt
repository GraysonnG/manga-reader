package com.blanktheevil.mangareader.data.history

interface HistoryManager {
    var history: History

    companion object {
        const val HISTORY_KEY = "history"
    }
}