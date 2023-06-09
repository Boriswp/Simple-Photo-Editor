package com.bescom.simplephotoeditorapp.imageEdit

import android.graphics.Bitmap
import java.io.File

data class ViewImagesState(
    val isLoading: Boolean = false,
    val images: List<Pair<File, Bitmap>>? = null,
    val error: String? = null
)