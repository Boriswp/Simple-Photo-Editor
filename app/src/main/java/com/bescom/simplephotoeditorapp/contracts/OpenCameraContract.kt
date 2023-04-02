package com.bescom.simplephotoeditorapp.contracts
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract


class OpenCameraContract : ActivityResultContract<Uri, Boolean>() {

    override fun createIntent(context: Context, input: Uri): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, input)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean  = resultCode == Activity.RESULT_OK
}
