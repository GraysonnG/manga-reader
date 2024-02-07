package com.blanktheevil.mangareader.data.dto.responses

import com.blanktheevil.mangareader.data.dto.MangaDexListResponse
import com.blanktheevil.mangareader.data.dto.objects.PersonDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetAuthorListResponse(
    override val data: List<PersonDto>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int,
) : MangaDexListResponse<PersonDto>
