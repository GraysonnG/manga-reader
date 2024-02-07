package com.blanktheevil.mangareader.data.dto.responses

import com.blanktheevil.mangareader.data.dto.MangaDexListResponse
import com.blanktheevil.mangareader.data.dto.objects.CoverDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMangaCoversResponse(
    override val data: List<CoverDto>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int
) : MangaDexListResponse<CoverDto>
