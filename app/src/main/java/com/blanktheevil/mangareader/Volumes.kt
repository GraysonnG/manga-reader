package com.blanktheevil.mangareader

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.blanktheevil.mangareader.data.Chapter
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.toChapterList
import com.squareup.moshi.Moshi


// VolumeNumber -> Chapters
typealias Volumes = MutableMap<String, Chapters>
// ChapterNumber -> ChapterMap
typealias Chapters = MutableMap<String, ChapterMap>
// scanlationGroupId -> Chapter
typealias ChapterMap = MutableMap<String, Chapter>

val ChapterMap.title
    get() = this.values.first().title


fun thing(): Volumes {
    val volumes: Volumes = mutableMapOf()

    val moshi = Moshi.Builder().build()

    StubData.Responses.GET_CHAPTER_LIST.data
        .toChapterList(
            moshi = moshi
        )
        .reversed()
        .forEach {
            val volume = it.volume ?: "none"
            val chapter = it.chapter ?: "none"
            val scanlationGroupId = it.relatedScanlationGroupId ?: "none"

            if (volumes[volume] == null) {
                volumes[volume] = mutableMapOf(
                    chapter to mutableMapOf(
                        scanlationGroupId to it
                    )
                )
            }

            if (volumes[volume]?.get(chapter) == null) {
                volumes[volume]?.set(
                    chapter, mutableMapOf(
                        scanlationGroupId to it
                    )
                )
            }

            if (volumes[volume]?.get(chapter)?.get(scanlationGroupId) == null) {
                volumes[volume]?.get(chapter)?.set(scanlationGroupId, it)
            }
        }

    println(volumes)
    Log.d("Volumes", volumes.toString())
    return volumes
}

@Preview
@Composable
fun ComposeThing() {
    val volumes by remember {
        mutableStateOf(thing() as Map<String, Chapters>)
    }

    Surface {
        Column {
            volumes.forEach { (k, v) ->
                Text(text = "Volume $k")
                v.forEach { (k2, v2) ->
                    Text(text = "\t\tChapter $k2")
                    v2.forEach { (k3, v3) ->
                        Text(text = "\t\t\t\t${v3.relatedScanlationGroup?.name}")
                        Text(text = "\t\t\t\t${v3.title}", maxLines = 1)
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

