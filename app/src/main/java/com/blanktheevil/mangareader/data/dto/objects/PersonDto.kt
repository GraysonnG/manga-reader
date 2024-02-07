package com.blanktheevil.mangareader.data.dto.objects

import com.blanktheevil.mangareader.data.dto.MangaDexObject
import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PersonDto(
    override val id: String,
    override val type: String,
    override val attributes: PersonAttributesDto,
    override val relationships: RelationshipList?,
) : MangaDexObject<PersonDto.PersonAttributesDto> {

    @JsonClass(generateAdapter = true)
    data class PersonAttributesDto(
        val name: String,
    )
}

