package com.bescom.simplephotoeditorapp.imageEdit


sealed class ImageFilterState {
    object Initial : ImageFilterState()
    object Loading : ImageFilterState()
    data class Success(val imageFilters: List<ImageFilter>) : ImageFilterState()
    data class Error(val exception: String) : ImageFilterState()
}