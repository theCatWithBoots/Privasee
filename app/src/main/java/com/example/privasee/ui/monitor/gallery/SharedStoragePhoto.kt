package com.example.privasee.ui.monitor.gallery

import android.graphics.Bitmap
import android.net.Uri

data class SharedStoragePhoto(
    val path: String,
    val id: Long,
    val name: String,
    val width: Int,
    val height: Int,
    val contentUri: Uri
)
