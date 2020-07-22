package com.indrih.core

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Сделан для того чтобы лишний раз не импортить указывать context.
 * и не передавать руками [View.getContext].
 */
@Suppress("NOTHING_TO_INLINE")
inline fun View.getColor(@ColorRes id: Int): Int =
    ContextCompat.getColor(context, id)

/**
 * Сделан для того чтобы лишний раз не прописывать руками [View.getResources].
 */
@Suppress("NOTHING_TO_INLINE")
inline fun View.getDimension(@DimenRes id: Int): Float =
    resources.getDimension(id)

/**
 * Сделан для того чтобы лишний раз не прописывать руками [Fragment.getResources].
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Fragment.getDimension(@DimenRes id: Int): Float =
    resources.getDimension(id)
