package com.bescom.simplephotoeditorapp.imageEdit

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.bescom.simplephotoeditorapp.Consts
import com.bescom.simplephotoeditorapp.R
import com.bescom.simplephotoeditorapp.ui.theme.Shapes
import com.canhub.cropper.CropImageView


@Composable
fun ImageCropScreen(
    origBitmap: Bitmap,
    nextClick: (bitmap: Bitmap) -> Unit,
    backClick: () -> Unit
) {

    var cropImageView: CropImageView? = null
    val focusRequester = FocusRequester()

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (crop, buttons) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(crop) {
                    top.linkTo(parent.top)
                    bottom.linkTo(buttons.top, margin = 20.dp)
                }
        ) {
            AndroidView(
                factory = { context: Context ->
                    val view = LayoutInflater.from(context)
                        .inflate(R.layout.crop_layout, null, false)
                    cropImageView = view.findViewById(R.id.cropImageView)
                    cropImageView?.setImageBitmap(origBitmap)
                    view
                },
                update = {
                    if (cropImageView == null) {
                        cropImageView = it.findViewById(R.id.cropImageView)
                        cropImageView?.setImageBitmap(origBitmap)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 50.dp)
                    .focusRequester(focusRequester = focusRequester)
            )

        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 30.dp)
                .constrainAs(buttons) {
                    bottom.linkTo(parent.bottom)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            Button(
                onClick = {
                    backClick()
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

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        Color(0F, 0F, 0F, 0.6F, ColorSpaces.Srgb),
                        shape = RoundedCornerShape(36.dp)
                    )
                    .padding(all = 13.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_rotate),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .height(20.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                cropImageView?.rotateImage(Consts.ROTATION)
                            })
                        }
                )
            }

            Button(
                onClick = {
                    cropImageView?.getCroppedImage(origBitmap.width, origBitmap.height)
                        ?.let { nextClick(it) }
                },
                modifier = Modifier
                    .width(140.dp)
                    .height(50.dp)
                    .padding(horizontal = 10.dp),
                shape = Shapes.medium,
                colors =
                ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                    backgroundColor = Color.Red
                )
            ) {
                Text(text = "Next")
            }
        }
    }
}