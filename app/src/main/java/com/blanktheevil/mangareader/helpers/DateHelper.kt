package com.blanktheevil.mangareader.helpers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 2022-12-19T17:02:29
fun getCreatedAtSinceString(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val now = LocalDateTime.now()
    val threeMonthsAgo = now.minusMonths(6)
    return formatter.format(threeMonthsAgo)
}