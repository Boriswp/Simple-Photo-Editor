package com.bescom.simplephotoeditorapp.imageEdit

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bescom.simplephotoeditorapp.Consts
import com.bescom.simplephotoeditorapp.R
import com.bescom.simplephotoeditorapp.elements.CustomFilterSlider
import com.bescom.simplephotoeditorapp.helpers.Utils
import com.bescom.simplephotoeditorapp.ui.theme.Shapes
import jp.co.cyberagent.android.gpuimage.GPUImageView


@Composable
fun ImageEffectsScreen(
    bitmap: Bitmap,
    viewModel: ImageEditorViewModel,
    onBackClick: () -> Unit,
    onSendPhotoClick: (newBitmap: Bitmap) -> Unit
) {
    val configuration = LocalConfiguration.current
    val focusManager = LocalFocusManager.current
    var expose by remember { mutableStateOf(Consts.STANDARD_EXPOSE) }
    var contrast by remember { mutableStateOf(Consts.STANDARD_CONTRAST) }
    var vignette by remember { mutableStateOf(Consts.STANDARD_VIGNETTE) }
    var mGPUImageView: GPUImageView? by remember {
        mutableStateOf(null)
    }

    val newScaledDimensions by remember {
        mutableStateOf(
            Utils.resize(
                bitmap,
                configuration.screenWidthDp,
                (configuration.screenHeightDp * 0.45).toInt()
            )
        )
    }

    var selectedFilter by remember {
        mutableStateOf(ImageFilter())
    }
    val openFilters = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {

        AndroidView(
            factory = { context: Context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.gpuimage_layout, null, false)
                mGPUImageView = view.findViewById(R.id.gpuImageView)
                mGPUImageView?.gpuImage?.setImage(bitmap)
                view
            },
            modifier = Modifier
                .width(newScaledDimensions.first.dp)
                .height(newScaledDimensions.second.dp)
                .align(Alignment.TopCenter)
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { openFilters.value = false }) {
                    Text(
                        text = "Effects",
                        color = if (!openFilters.value) Color.Red else Color.Gray
                    )
                }

                Spacer(modifier = Modifier.padding(45.dp))

                TextButton(onClick = { openFilters.value = true }) {
                    Text(
                        text = "Filters",
                        color = if (openFilters.value) Color.Red else Color.Gray
                    )
                }
            }
            if (!openFilters.value) {
                Column(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                            })
                        }
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    CustomFilterSlider(
                        drawableResource = R.drawable.ic_exposition,
                        sliderPosition = expose,
                        valueFrom = -1f,
                        valueTo = 1f,
                        onValueChange = {
                            expose = it
                            mGPUImageView?.gpuImage?.let { it1 ->
                                viewModel.applyImageChanges(
                                    it1,
                                    expose,
                                    contrast,
                                    vignette
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.padding(5.dp))

                    CustomFilterSlider(
                        drawableResource = R.drawable.ic_contrast,
                        sliderPosition = contrast,
                        valueFrom = 0.5f,
                        valueTo = 1.5f,
                        onValueChange = {
                            contrast = it
                            mGPUImageView?.gpuImage?.let { it1 ->
                                viewModel.applyImageChanges(
                                    it1,
                                    expose,
                                    contrast,
                                    vignette
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.padding(5.dp))

                    CustomFilterSlider(
                        drawableResource = R.drawable.ic_screwer,
                        sliderPosition = vignette,
                        valueFrom = 3.5f,
                        valueTo = 5.5f,
                        onValueChange = {
                            vignette = it
                            mGPUImageView?.gpuImage?.let { it1 ->
                                viewModel.applyImageChanges(
                                    it1,
                                    expose,
                                    contrast,
                                    vignette
                                )
                            }
                        }
                    )
                }
            } else {
                ImageFiltersEditor(
                    selectedFilter = selectedFilter,
                    viewModel = viewModel,
                    onClick = {
                        selectedFilter = it
                        mGPUImageView?.gpuImage?.let { it1 ->
                            viewModel.applyFilterChanges(
                                it1,
                                selectedFilter
                            )
                        }
                    })
            }
            Row(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
                    .padding(horizontal = 20.dp, vertical = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        onBackClick()
                    },
                    modifier = Modifier
                        .width(140.dp)
                        .height(50.dp)
                        .padding(horizontal = 10.dp),
                    shape = Shapes.medium,
                    colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                        backgroundColor = Color.Gray
                    )
                ) {
                    Text(text = "Back")
                }

                Spacer(modifier = Modifier.padding(20.dp))
                Button(
                    onClick = {
                        mGPUImageView?.gpuImage?.bitmapWithFilterApplied?.let {
                            onSendPhotoClick(
                                it
                            )
                        }
                    },
                    modifier = Modifier
                        .width(140.dp)
                        .height(50.dp),
                    shape = Shapes.medium,
                    colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                        backgroundColor = Color.Red
                    )
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}




