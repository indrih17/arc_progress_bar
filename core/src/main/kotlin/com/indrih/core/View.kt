package com.indrih.core

import android.text.*
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

/** Поставить фокус и поднять клавиатуру или убрать фокус и опустить клавиатуру. */
fun EditText.setFocusStatus(isEnabled: Boolean) {
    if (isEnabled) requestFocus() else clearFocus()
}

/**
 * Подобная защита от рекурсии необходима, когда идёт автоматическое обновление состояния в модели.
 */
inline fun TextView.recursionSafeSetText(newText: String, ifNew: (String) -> Unit = {}) {
    if (text.toString() != newText) {
        text = newText
        ifNew(newText)
    }
}

/** Программная установка максимальной длины. */
fun TextView.maxLength(max: Int) {
    filters += InputFilter.LengthFilter(max)
}

/** Нормальное сложение двух [SpannableString] с сохранением всех установок (например, spannable + link). */
operator fun SpannableString.plus(other: SpannableString): CharSequence =
    TextUtils.concat(this, other)

/** Удобная версия [ClickableSpan]. */
inline fun clickableSpan(
    text: String,
    @ColorInt color: Int,
    crossinline onClick: (View) -> Unit
): SpannableString {
    val spannable = SpannableString(text)
    val click = object : ClickableSpan() {
        override fun onClick(widget: View) =
            onClick(widget)

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = color
        }
    }
    spannable.setSpan(click, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return spannable
}

/** Вешает слушатель, который сработает, когда пользователь доскроллит до конца. */
fun ScrollView.onReachedToEnd(lifecycle: Lifecycle, block: (View) -> Unit) {
    // Поставил референсы потому что LeakCanary ругался.
    val scrollViewRef = WeakReference(this)
    val blockSoftRef = SoftReference(block)

    // Листенер нужно добавлять и удалять в зависимости от жц, потому что
    // обнаружился баг, что если пользователь доскроллил до конца и закрыл экран,
    // то функция `block` будет вызвана 100500 раз.
    // Решение - удалять листенер в какой-то момент времени, осталось только понять - в какой именно момент.
    // Идеальное решение - onStop. Попытка поставить onDestroy не увенчалась успехом, баг сохранялся.
    // Но после onStop может снова идти onStart, поэтому листенер снова надо добавить.
    val scrollChangeListener = ViewTreeObserver.OnScrollChangedListener {
        val view = scrollViewRef.get() ?: return@OnScrollChangedListener
        val diff = view.getChildAt(view.childCount - 1).bottom - (view.height + view.scrollY)
        if (diff == 0) blockSoftRef.get()?.invoke(view)
    }
    lifecycle.addObserver(
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewTreeObserver.addOnScrollChangedListener(scrollChangeListener)
                Lifecycle.Event.ON_STOP -> viewTreeObserver.removeOnScrollChangedListener(scrollChangeListener)
                else -> Unit
            }
        }
    )
}

/** Более удобная вариация [TextView.setCompoundDrawablesWithIntrinsicBounds]. */
fun TextView.setDrawableRes(
    @DrawableRes left: Int? = null,
    @DrawableRes top: Int? = null,
    @DrawableRes right: Int? = null,
    @DrawableRes bottom: Int? = null
) {
    setCompoundDrawablesWithIntrinsicBounds(
        left ?: 0,
        top ?: 0,
        right ?: 0,
        bottom ?: 0
    )
}

var BottomSheetBehavior<*>.isVisible: Boolean
    get() = state == BottomSheetBehavior.STATE_EXPANDED
    set(value) {
        state = if (value) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_HIDDEN
    }

fun ImageButton.setVisibleStatus(status: Boolean) {
    visibility = if (status) View.VISIBLE else View.INVISIBLE
}
