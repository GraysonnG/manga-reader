package com.blanktheevil.mangareader.data.dto.responses

import com.blanktheevil.mangareader.data.dto.MangaDexListResponse
import com.blanktheevil.mangareader.data.dto.objects.TagsDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetTagsResponse(
    override val data: List<TagsDto>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int,
) : MangaDexListResponse<TagsDto>
