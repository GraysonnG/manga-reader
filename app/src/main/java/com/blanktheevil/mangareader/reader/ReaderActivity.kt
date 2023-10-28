package com.blanktheevil.mangareader.reader

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ReaderActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chapterId = intent?.extras?.getString("manga_id")

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            ReaderTheme {
                chapterId?.let {
                    ReaderScreenV2(chapterId = chapterId)
                }
            }
        }
    }
}