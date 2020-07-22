package com.indrih.core

import android.content.res.TypedArray
import androidx.annotation.StyleableRes

/** Аналог [TypedArray.getColor], возвращающий `null` вместо дефолтного значения. */
fun TypedArray.getColorOrNull(@StyleableRes index: Int): Int? =
    getColor(index, -1).takeIf { it != -1 }

/** Аналог [TypedArray.getDimensionPixelSizeOrNull], возвращающий `null` вместо дефолтного значения. */
fun TypedArray.getDimensionPixelSizeOrNull(@StyleableRes index: Int): Int? =
    getDimensionPixelSize(index, -1).takeIf { it != -1 }

/** Аналог [TypedArray.getFloat], возвращающий `null` вместо дефолтного значения. */
fun TypedArray.getFloatOrNull(@StyleableRes index: Int): Float? =
    getFloat(index, -1f).takeIf { it != -1f }

/** Аналог [TypedArray.getInt], возвращающий `null` вместо дефолтного значения. */
fun TypedArray.getIntOrNull(@StyleableRes index: Int): Int? =
    getInt(index, -1).takeIf { it != -1 }

/** Аналог [TypedArray.getResourceId], возвращающий `null` вместо дефолтного значения. */
fun TypedArray.getResourceIdOrNull(@StyleableRes index: Int): Int? =
    getResourceId(index, -1).takeIf { it != -1 }

/** Аналог [TypedArray.getString], возвращающий `null` вместо дефолтного значения. */
fun TypedArray.getNotEmptyStringOrNull(@StyleableRes index: Int): String? =
    getString(index)?.takeIf { it.isNotEmpty() }
