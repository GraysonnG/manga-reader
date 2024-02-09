package com.blanktheevil.mangareader.data

import com.blanktheevil.mangareader.data.dto.objects.TagsDto

data class Tag(
    val id: String,
    val name: String,
    val group: String?,
)

fun TagsDto.toTag(): Tag =
    Tag(
        id = this.id,
        name = this.attributes.name["en"] ?: "Error",
        group = this.attributes.group
    )
