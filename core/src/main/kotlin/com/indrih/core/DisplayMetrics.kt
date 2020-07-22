package com.indrih.core

import android.content.res.Resources
import androidx.annotation.Dimension
import androidx.annotation.Px

/** Переводит значение из [Dimension.DP] в [Dimension.PX]. */
@Px
fun Float.dpToPx(): Float =
    this * Resources.getSystem().displayMetrics.density

/** Переводит значение из [Dimension.PX] в [Dimension.DP]. */
@Dimension(unit = Dimension.DP)
fun Float.pxToDp(): Float =
    this / Resources.getSystem().displayMetrics.density

/** Переводит значение из [Dimension.SP] в [Dimension.PX]. */
@Px
fun Float.spToPx(): Float =
    this * Resources.getSystem().displayMetrics.scaledDensity
