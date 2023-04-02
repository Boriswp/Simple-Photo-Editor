package com.bescom.simplephotoeditorapp.contracts

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.bescom.simplephotoeditorapp.imageEdit.ImageEditorActivity


class ImageEditorContract : ActivityResultContract<Uri, Uri?>() {

    override fun createIntent(context: Context, input: Uri): Intent {
        return Intent(context, ImageEditorActivity::class.java).putExtra("data", input.toString())
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data
    }
}