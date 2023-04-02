package com.bescom.simplephotoeditorapp.imageEdit

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ImageFiltersEditor(
    viewModel: ImageEditorViewModel,
    selectedFilter: ImageFilter,
    onClick: (selectedFilter: ImageFilter) -> Unit,
) {
    when (val result = viewModel.imageFilterUIState.collectAsState().value) {
        is ImageFilterState.Success -> {
            val listState = rememberLazyListState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                LazyRow(state = listState) {
                    items(result.imageFilters) { imageFilter ->
                        imageFilter.filterPreview?.let {
                            ImageFilterElement(
                                image = it,
                                filterName = imageFilter.name,
                                isSelected = selectedFilter.name == imageFilter.name,
                                onClick = {
                                    onClick(imageFilter)
                                })
                        }
                    }
                }
            }
        }

        else -> {}
    }
}

@Composable
private fun ImageFilterElement(
    image: Bitmap,
    filterName: String,
    onClick: () -> Unit,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .width(90.dp)
            .padding(horizontal = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = true) {
                onClick.invoke()
            },
    ) {
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = "filter image",
            modifier = Modifier
                .height(96.dp)
                .fillMaxWidth(),
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop
        )
        Text(
            text = filterName,
            color = if (isSelected) Color.White else Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isSelected) Color.Red else Color(1F, 1F, 1F, 0.5F))
                .align(Alignment.BottomCenter),
            textAlign = TextAlign.Center
        )
    }
}