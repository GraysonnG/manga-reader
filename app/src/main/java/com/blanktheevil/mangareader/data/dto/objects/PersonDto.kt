package com.blanktheevil.mangareader.data.dto.objects

import com.blanktheevil.mangareader.data.dto.MangaDexObject
import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PersonDto(
    override val id: String,
    override val type: String,
    override val attributes: Attributes,
    override val relationships: RelationshipList?,
) : MangaDexObject<PersonDto.Attributes> {

    @JsonClass(generateAdapter = true)
    data class Attributes(
        val name: String,
    )
}
