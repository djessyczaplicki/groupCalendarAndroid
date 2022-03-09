package com.djessyczaplicki.groupcalendar.util

import androidx.compose.ui.graphics.Color

fun Double.format(digits: Int) = "%.${digits}f".format(this)
