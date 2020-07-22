package com.indrih.core

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.snackbar.Snackbar

/** @see Snackbar */
fun Fragment.snackBar(
    view: View = requireView(),
    text: String,
    length: Int = Snackbar.LENGTH_INDEFINITE,
    buttonText: String? = view.context.getString(android.R.string.ok),
    onClick: (View) -> Unit
) {
    val snackBar = Snackbar
        .make(view, text, length)
        .setAction(buttonText, onClick)
    viewLifecycleOwner
        .lifecycle
        .addObserver(SnackBarLifecycleObserver(snackBar))
    snackBar.show()
}

private class SnackBarLifecycleObserver(private val snackBar: Snackbar) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() = snackBar.dismiss()
}