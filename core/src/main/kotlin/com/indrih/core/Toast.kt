package com.indrih.core

import android.content.Context
import android.widget.Toast

/** @see Toast */
fun Context.toast(s: String): Unit =
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()

/** @see Toast */
fun Context.longToast(s: String): Unit =
    Toast.makeText(this, s, Toast.LENGTH_LONG).show()

