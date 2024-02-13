package com.blanktheevil.mangareader.data

class Volume(
    val number: Int,
    val name: String,
    val chapters: List<VolumeChapter>,
)

class VolumeChapter(
    val id: String,
    val chapter: String,
    val next: String?,
    val prev: String?,
)