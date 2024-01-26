package com.blanktheevil.mangareader.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetAuthorListResponse(
    val result: String?,
    val data: List<PersonDto>,
    val limit: Int,
    val offset: Int,
    val total: Int,
)

@JsonClass(generateAdapter = true)
data class PersonDto(
    val id: String,
    val attributes: PersonAttributesDto,
)

@JsonClass(generateAdapter = true)
data class PersonAttributesDto(
    val name: String,
)
