package com.indrih.core

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.IBinder
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager

private data class KeyboardData(
    val windowToken: IBinder?,
    val inputMethodManager: InputMethodManager
)

object Keyboard {
    private var isKeyboardShowing = false
    private var data: KeyboardData? = null

    fun observeKeyboardVisibleStatus(parentView: View) {
        parentView.viewTreeObserver.addOnGlobalLayoutListener {
            data = KeyboardData(
                windowToken = parentView.windowToken,
                inputMethodManager = parentView.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            )

            val outRect = Rect()
            parentView.getWindowVisibleDisplayFrame(outRect)
            val screenHeight = parentView.rootView.height

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            val keypadHeight = screenHeight - outRect.bottom

            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                if (!isKeyboardShowing) {
                    isKeyboardShowing = true
                }
            } else {
                // keyboard is closed
                if (isKeyboardShowing) {
                    isKeyboardShowing = false
                }
            }
        }
    }

    internal fun show() {
        if (!isKeyboardShowing) {
            data?.let { (_, inputMethodManager) ->
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
        }
    }

    internal fun hide() {
        if (isKeyboardShowing) {
            data?.let { (windowToken, inputMethodManager) ->
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
            }
        }
    }
}

fun View.showKeyboard() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        windowInsetsController?.show(WindowInsets.Type.ime())
    } else {
        Keyboard.show()
    }
}

fun View.hideKeyboard() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        windowInsetsController?.hide(WindowInsets.Type.ime())
    } else {
        Keyboard.hide()
    }
}

fun View.showOrHideKeyboard(status: Boolean) {
    if (status) showKeyboard() else hideKeyboard()
}
