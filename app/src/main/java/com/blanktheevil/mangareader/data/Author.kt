package com.blanktheevil.mangareader.data

import com.blanktheevil.mangareader.data.dto.PersonDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Author(
    val id: String,
    val name: String,
) {
    override fun toString(): String {
        return this.name
    }
}

fun PersonDto.toAuthor(): Author = Author(
    id = this.id,
    name = this.attributes.name
)
