package com.blanktheevil.mangareader.data

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.blanktheevil.mangareader.R

enum class TagsMode {
    AND,
    OR,
}

enum class Demographics(val key: String, @StringRes val nameResId: Int) {
    SHOUNEN("shounen", R.string.demographic_shounen),
    SHOUJO("shoujo", R.string.demographic_shoujo),
    JOSEI("josei", R.string.demographic_josei),
    SEINEN("seinen", R.string.demographic_seinen),
    NONE("none", R.string.none),
    ;

    companion object {
        @Composable
        fun toValueMap(): Map<String, String> =
            entries.associate {
                it.key to stringResource(id = it.nameResId)
            }
    }
}

enum class ContentRatings(val key: String, @StringRes val nameResId: Int) {
    SAFE("safe", R.string.settings_content_filter_safe),
    SUGGESTIVE("suggestive", R.string.settings_content_filter_suggestive),
    EROTICA("erotica", R.string.settings_content_filter_ero),
    ;

    companion object {
        @Composable
        fun toValueMap(): Map<String, String> =
            entries.associate {
                it.key to stringResource(id = it.nameResId)
            }
    }
}

enum class Sort(val key: String, @StringRes val nameResId: Int) {
    NONE("none", R.string.none),
    BEST_MATCH("bestMatch", R.string.sort_best_match),
    LATEST_UPLOAD("latestUpload", R.string.sort_latest_upload),
    OLDEST_UPLOAD("oldestUpload", R.string.sort_oldest_upload),
    TITLE_ASC("titleAsc", R.string.sort_title_asc),
    TITLE_DESC("titleDesc", R.string.sort_title_desc),
    RATING_HIGH("ratingHigh", R.string.sort_rating_high),
    RATING_LOW("ratingLow", R.string.sort_rating_low),
    FOLLOWS_HIGH("followsHigh", R.string.sort_follows_high),
    FOLLOWS_LOW("followsLow", R.string.sort_follows_low),
    RECENT_DESC("recentDesc", R.string.sort_recent_desc),
    RECENT_ASC("recentAsc", R.string.sort_recent_asc),
    YEAR_ASC("yearAsc", R.string.sort_year_asc),
    YEAR_DESC("yearDesc", R.string.sort_year_desc),
    ;

    companion object {
        @Composable
        fun toValueMap(): Map<String, String> =
            entries.associate {
                it.key to stringResource(id = it.nameResId)
            }
    }
}

enum class Status(val key: String, @StringRes val nameResId: Int) {
    ONGOING("ongoing", R.string.status_ongoing),
    COMPLETED("completed", R.string.status_completed),
    HIATUS("hiatus", R.string.status_hiatus),
    CANCELLED("cancelled", R.string.status_cancelled),
    ;

    companion object {
        @Composable
        fun toValueMap(): Map<String, String> =
            entries.associate {
                it.key to stringResource(id = it.nameResId)
            }
    }
}