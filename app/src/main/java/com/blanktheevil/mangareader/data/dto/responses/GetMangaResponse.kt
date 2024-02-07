package com.blanktheevil.mangareader.data.dto.responses

import com.blanktheevil.mangareader.data.dto.MangaDexResponse
import com.blanktheevil.mangareader.data.dto.objects.MangaDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMangaResponse(
    val result: String?,
    val response: String?,
    override val data: MangaDto,
) : MangaDexResponse<MangaDto>