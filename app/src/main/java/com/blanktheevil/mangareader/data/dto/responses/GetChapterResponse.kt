package com.blanktheevil.mangareader.data.dto.responses

import com.blanktheevil.mangareader.data.dto.MangaDexResponse
import com.blanktheevil.mangareader.data.dto.objects.ChapterDto
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class GetChapterResponse(
    override val data: ChapterDto,
) : MangaDexResponse<ChapterDto>
