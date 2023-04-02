package com.bescom.simplephotoeditorapp.imageEdit

sealed class ImageEditViewState {
    object CropView : ImageEditViewState()
    object EffectsView : ImageEditViewState()
}