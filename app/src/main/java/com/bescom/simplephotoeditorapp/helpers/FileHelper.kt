package com.bescom.simplephotoeditorapp.helpers

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.net.toUri
import java.io.*
import kotlin.math.roundToInt


class FileHelper {
    private fun convertBitmapToFile(imageBitmap: Bitmap, context: Context): File? {
        val f = File(context.cacheDir, "file.jpg")
        return try {
            val os = FileOutputStream(f);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
            f
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun convertBitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        return convertBitmapToFile(bitmap, context)?.toUri()
    }

    private fun resize(imaged: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap? {
        var image = imaged
        if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > 1) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).roundToInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).roundToInt()
            }
            return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, false).also {
                if (it != null) {
                    image = it
                }
            }
        }
        return image
    }

    fun convertUriToBitmap(imageUri: Uri, context: Context): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val ins = context.contentResolver.openInputStream(imageUri)
            val decoder: BitmapRegionDecoder? =
                ins?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        BitmapRegionDecoder.newInstance(it)
                    } else {
                        BitmapRegionDecoder.newInstance(it, false)
                    }
                }
            bitmap =
                decoder?.decodeRegion(Rect(0, 0, decoder.width, decoder.height), null)
                    ?.let { resize(it, 2000, 2000) }
            ins?.close()
        } catch (err: FileNotFoundException) {
            err.printStackTrace()
        }
        return bitmap
    }


    fun saveImage(uri: Uri, name: String, context: Context) {
        try {
            val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver: ContentResolver = context.contentResolver
                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.jpg")
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                contentValues.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES
                )
                val imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                resolver.openOutputStream(imageUri!!)
            } else {
                val imagesDir: String =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .toString()
                val image = File(imagesDir, "$name.jpg")
                FileOutputStream(image)
            }
            convertUriToBitmap(uri, context)?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos?.close()
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
        }
    }

    fun convertUriToFile(uri: Uri?, context: Context): File? {
        val bitmap = uri?.let { convertUriToBitmap(it, context) }
        return bitmap?.let { convertBitmapToFile(it, context) }
    }
}

