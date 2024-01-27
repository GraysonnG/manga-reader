package com.blanktheevil.mangareader.ui

val SORT_MAP = mapOf(
    "none" to null,
    "bestMatch" to ("relevance" to "desc"),
    "latestUpload" to ("latestUploadedChapter" to "desc"),
    "oldestUpload" to ("latestUploadedChapter" to "asc"),
    "titleAsc" to ("title" to "asc"),
    "titleDesc" to ("title" to "desc"),
    "ratingHigh" to ("rating" to "desc"),
    "ratingLow" to ("rating" to "asc"),
    "followsHigh" to ("followedCount" to "desc"),
    "followsLow" to ("followedCount" to "asc"),
    "recentDesc" to ("createdAt" to "desc"),
    "recentAsc" to ("createdAt" to "asc"),
    "yearAsc" to ("year" to "asc"),
    "yearDesc" to ("year" to "desc"),
)