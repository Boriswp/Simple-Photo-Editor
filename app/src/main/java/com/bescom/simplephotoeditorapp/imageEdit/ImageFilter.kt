package com.bescom.simplephotoeditorapp.imageEdit

import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

data class ImageFilter(
    val name: String = "",
    val filter: GPUImageFilter? = null,
    val filterPreview: Bitmap? = null,
)
