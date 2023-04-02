package com.bescom.simplephotoeditorapp.imageEdit

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.bescom.simplephotoeditorapp.elements.TextToolbar


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ImageEditorScreen(
    bitmap: Bitmap,
    viewModel: ImageEditorViewModel,
    onExitClick: () -> Unit,
    sendPhotoClick: (newBitmap: Bitmap) -> Unit
) {
    var bitmapAfterCrop by remember { mutableStateOf(bitmap) }

    when (viewModel.screenState.collectAsState().value) {
        ImageEditViewState.CropView -> {
            Scaffold(topBar = {
                TextToolbar(
                    text = "Crop"
                )
            }) {
                ImageCropScreen(
                    bitmap,
                    nextClick = { croppedBitmap: Bitmap ->
                        bitmapAfterCrop = croppedBitmap
                        viewModel.setWhichScreen(ImageEditViewState.EffectsView)
                    },
                    backClick = { onExitClick() })
            }
        }
        ImageEditViewState.EffectsView -> {
            Scaffold(topBar = {
                TextToolbar(
                    text = "Effects"
                )
            }) {
                val softwareBitmap = bitmapAfterCrop.copy(Bitmap.Config.ARGB_8888, true)
                viewModel.loadImageFilters(softwareBitmap, LocalContext.current)
                ImageEffectsScreen(
                    bitmap = softwareBitmap,
                    viewModel = viewModel,
                    onBackClick = { viewModel.setWhichScreen(ImageEditViewState.CropView) },
                    onSendPhotoClick = { sendPhotoClick(it) })
            }
        }
    }
}