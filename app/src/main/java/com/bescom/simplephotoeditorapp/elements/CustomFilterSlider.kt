package com.bescom.simplephotoeditorapp.elements

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CustomFilterSlider(
    @DrawableRes drawableResource: Int,
    sliderPosition: Float,
    valueFrom: Float,
    valueTo: Float,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(drawableResource),
            modifier = Modifier
                .height(20.dp)
                .width(20.dp),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Slider(
            value = sliderPosition,
            valueRange = valueFrom..valueTo,
            steps = 21,
            onValueChange = { onValueChange(it) },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.Red,
                inactiveTrackColor = Color.Gray,
                inactiveTickColor = Color.Gray,
                activeTickColor = Color.Red
            )
        )
    }

}