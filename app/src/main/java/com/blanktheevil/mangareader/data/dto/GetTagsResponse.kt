package com.blanktheevil.mangareader.data.dto

import com.blanktheevil.mangareader.data.Tag
import com.blanktheevil.mangareader.data.toTag
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetTagsResponse(
    val result: String?,
    val data: List<TagsDto>,
    val limit: Int,
    val offset: Int,
    val total: Int,
)

typealias TagList = List<Tag>

fun GetTagsResponse.toTagList(): TagList =
    this.data.map { it.toTag() }