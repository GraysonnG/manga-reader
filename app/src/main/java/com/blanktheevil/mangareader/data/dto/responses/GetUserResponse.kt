package com.blanktheevil.mangareader.data.dto.responses

import com.blanktheevil.mangareader.data.dto.MangaDexResponse
import com.blanktheevil.mangareader.data.dto.objects.UserDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUserResponse(
    override val data: UserDto,
) : MangaDexResponse<UserDto>

