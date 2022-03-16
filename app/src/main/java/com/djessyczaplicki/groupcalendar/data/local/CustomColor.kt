package com.djessyczaplicki.groupcalendar.data.local

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

class CustomColor(
    val red: Int,
    val green: Int,
    val blue: Int
) {
    companion object {
        fun from(color: ULong): CustomColor {
            val argb = Color(color).toArgb()
            return CustomColor(argb.red, argb.green, argb.blue)
        }
    }

    fun toComposeColor(): Color = Color(red, green, blue)
}