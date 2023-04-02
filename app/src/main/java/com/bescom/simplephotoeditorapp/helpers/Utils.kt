package com.bescom.simplephotoeditorapp.helpers

import android.graphics.Bitmap

object Utils {
    fun resize(image: Bitmap, maxWidth: Int, maxHeight: Int): Pair<Int, Int> {
        return if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            Pair(finalWidth, finalHeight)
        } else {
            return Pair(maxWidth, maxHeight)
        }
    }
}