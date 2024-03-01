package com.blanktheevil.mangareader.data

object ContentFilter {
    const val SAFE: ContentRating = "safe"
    const val SUGGESTIVE: ContentRating = "suggestive"
    const val EROTICA: ContentRating = "erotica"
    const val NSFW: ContentRating = "pornographic"
}

typealias ContentRating = String