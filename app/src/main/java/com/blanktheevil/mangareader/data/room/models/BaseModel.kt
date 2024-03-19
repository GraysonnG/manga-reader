package com.blanktheevil.mangareader.data.room.models

import java.time.Instant

interface BaseModel<T> {
    val lastUpdated: Long
    val data: T
    val key: String

    fun isExpired(time: Long = FIFTEEN_MINUTES): Boolean =
        lastUpdated + time < Instant.now().toEpochMilli()


    companion object {
        private const val FIFTEEN_MINUTES: Long = 15 * 60000
    }
}