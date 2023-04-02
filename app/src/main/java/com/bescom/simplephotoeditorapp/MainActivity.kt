package com.bescom.simplephotoeditorapp

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.bescom.simplephotoeditorapp.contracts.ImageEditorContract
import com.bescom.simplephotoeditorapp.contracts.OpenCameraContract
import com.bescom.simplephotoeditorapp.contracts.SelectPictureContract
import com.bescom.simplephotoeditorapp.elements.CircleFloatingButton
import com.bescom.simplephotoeditorapp.helpers.FileHelper
import com.bescom.simplephotoeditorapp.ui.theme.SimplePhotoEditorAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimplePhotoEditorAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {

    val context = LocalContext.current

    var savedUri by remember {
        mutableStateOf(Uri.EMPTY)
    }

    val photoFile = File.createTempFile(
        "IMG_",
        ".jpg",
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    )

    val localImageUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        photoFile
    )
    val writeAccessLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val fName = Random.nextInt()
            FileHelper().saveImage(savedUri, "Image$fName", context)
        }
    }

    val openImageEditorScreen =
        rememberLauncherForActivityResult(contract = ImageEditorContract()) { uri ->
            if (uri != null) {
                savedUri = uri
                writeAccessLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }


    val selectPictureLauncher = rememberLauncherForActivityResult(
        contract = SelectPictureContract()
    ) { imageUri ->
        if (imageUri != null) {
            openImageEditorScreen.launch(imageUri)
        }
    }

    val openCameraLauncher = rememberLauncherForActivityResult(
        contract = OpenCameraContract()
    ) { resultCode ->
        if (resultCode && localImageUri != null) {
            openImageEditorScreen.launch(localImageUri)
        }
    }

    val cameraPermission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCameraLauncher.launch(localImageUri)
            }
        }

    ImagePickerDialogUI(
        selectPictureLauncher = selectPictureLauncher,
        openCameraPermission = cameraPermission
    )
}

@Composable
fun ImagePickerDialogUI(
    modifier: Modifier = Modifier,
    selectPictureLauncher: ManagedActivityResultLauncher<Unit, Uri?>,
    openCameraPermission: ManagedActivityResultLauncher<String, Boolean>,
) {

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier
                .background(Color.White)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircleFloatingButton(
                        modifier = Modifier
                            .size(75.dp),
                        onClick = {
                            openCameraPermission.launch(Manifest.permission.CAMERA)
                        },
                        contentColor = Color.Transparent
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            tint = Color.Unspecified,
                            contentDescription = "dislike"
                        )
                    }

                    Spacer(modifier = Modifier.padding(8.dp))

                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircleFloatingButton(
                        modifier = Modifier
                            .size(75.dp),
                        onClick = {
                            selectPictureLauncher.launch(Unit)
                        },
                        contentColor = Color.Transparent
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_gallery),
                            tint = Color.Unspecified,
                            contentDescription = "dislike"
                        )
                    }

                    Spacer(modifier = Modifier.padding(8.dp))

                }

            }
        }
    }
}

