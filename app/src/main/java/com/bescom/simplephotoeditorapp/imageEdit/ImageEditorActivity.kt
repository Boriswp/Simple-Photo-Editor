package com.bescom.simplephotoeditorapp.imageEdit

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import com.bescom.simplephotoeditorapp.core.BaseActivity
import com.bescom.simplephotoeditorapp.helpers.FileHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class ImageEditorActivity : BaseActivity<ImageEditorViewModel>() {

    override val viewModel: ImageEditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ComposeView(this).apply {
                setContent {
                    val bitmap: Bitmap?
                    val uri = Uri.parse(intent?.extras?.getString("data") ?: " ")
                    runBlocking(Dispatchers.IO) {
                        bitmap = FileHelper().convertUriToBitmap(uri, context)
                    }
                    bitmap?.let {
                        ImageEditorScreen(it, viewModel, onExitClick = {
                            setResult(RESULT_CANCELED, null)
                            finish()
                        }) { bitmap ->
                            setResult(
                                RESULT_OK,
                                intent.setData(
                                    FileHelper().convertBitmapToUri(
                                        context,
                                        bitmap
                                    )
                                )
                            )
                            finish()
                        }
                    } ?: run {
                        setResult(RESULT_CANCELED, null)
                        finish()
                    }
                }
            }
        )
    }
}