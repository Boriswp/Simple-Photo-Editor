package com.bescom.simplephotoeditorapp.imageEdit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bescom.simplephotoeditorapp.Consts
import com.bescom.simplephotoeditorapp.repository.ImageEditorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@HiltViewModel
class ImageEditorViewModel @Inject constructor(
    private val repository: ImageEditorRepository,
) : ViewModel() {

    private val _imageFilterDataState = MutableStateFlow<ImageFilterState>(ImageFilterState.Initial)
    val imageFilterUIState = _imageFilterDataState.asStateFlow()

    private val _screenState = MutableStateFlow<ImageEditViewState>(ImageEditViewState.CropView)
    val screenState = _screenState.asStateFlow()

    fun setWhichScreen(state: ImageEditViewState) {
        viewModelScope.launch(ioDispatchers) {
            if (state == ImageEditViewState.CropView) {
                filterList.clear()
            }
            _screenState.value = state
        }
    }

    private val filterList = ArrayList<GPUImageFilter>()

    fun loadImageFilters(origImage: Bitmap, context: Context) {
        viewModelScope.launch(ioDispatchers) {
            kotlin.runCatching {
                _imageFilterDataState.value = ImageFilterState.Loading
                val image = getPreviewImage(origImage = origImage)
                repository.loadImageFilters(image = image, context)
            }.onSuccess {
                filterList.add(
                    GPUImageMyPhotoFilter(
                        Consts.STANDARD_EXPOSE,
                        Consts.STANDARD_CONTRAST
                    )
                )
                filterList.add(
                    GPUImageVignetteFilter(
                        PointF(0.5f, 0.5f),
                        floatArrayOf(1f, 1f, 1f),
                        0f,
                        kotlin.math.abs(Consts.STANDARD_VIGNETTE - 6.3f)
                    )
                )
                it[0].filter?.let { it1 -> filterList.add(it1) }
                _imageFilterDataState.value = ImageFilterState.Success(it)
            }.onFailure {
                _imageFilterDataState.value = ImageFilterState.Error(it.message.toString())
            }
        }
    }

    fun applyFilterChanges(
        gpuImage: GPUImage,
        selectedFilter: ImageFilter
    ) {
        selectedFilter.filter?.let { filterList[2] = it }
        gpuImage.setFilter(GPUImageFilterGroup(filterList))
    }

    fun applyImageChanges(
        gpuImage: GPUImage,
        expose: Float,
        contrast: Float,
        vignette: Float
    ) {
        filterList[0] = GPUImageMyPhotoFilter(expose, contrast)
        filterList[1] =
            GPUImageVignetteFilter(
                PointF(0.5f, 0.5f),
                floatArrayOf(1f, 1f, 1f), 0f, kotlin.math.abs(vignette - 6.3f)
            )
        gpuImage.setFilter(GPUImageFilterGroup(filterList))
    }

    private fun getPreviewImage(origImage: Bitmap): Bitmap {
        return kotlin.runCatching {
            val previewWidth = 150
            val previewHeight = origImage.height * previewWidth / origImage.width
            Bitmap.createScaledBitmap(origImage, previewWidth, previewHeight, false)
        }.getOrDefault(origImage)
    }

    private fun createErrorHandler() = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    private val ioDispatchers: CoroutineContext
        get() = Dispatchers.IO + createErrorHandler()
}