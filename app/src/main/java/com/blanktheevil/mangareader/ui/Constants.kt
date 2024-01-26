package com.blanktheevil.mangareader.ui

val CONTENT_RATINGS_MAP = mapOf(
    "safe" to "Safe",
    "suggestive" to "Suggestive",
    "erotica" to "Erotica"
)

val DEMOGRAPHICS_MAP = mapOf(
    "shounen" to "Shounen",
    "shoujo" to "Shoujo",
    "seinen" to "Seinen",
    "josei" to "Josei",
    "none" to "None"
)

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

val SORT_NAMES = mapOf(
    "none" to "None",
    "bestMatch" to "Best Match",
    "latestUpload" to "Latest Upload",
    "oldestUpload" to "Oldest Upload",
    "titleAsc" to "Title (A-Z)",
    "titleDesc" to "Title (Z-A)",
    "ratingHigh" to "Highest Rating",
    "ratingLow" to "Lowest Rating",
    "followsHigh" to "Most Follows",
    "followsLow" to "Least Follows",
    "recentDesc" to "Recently Added",
    "recentAsc" to "Oldest Added",
    "yearAsc" to "Year Ascending",
    "yearDesc" to "Year Descending",
)

val STATUS_MAP = mapOf(
    "ongoing" to "Ongoing",
)