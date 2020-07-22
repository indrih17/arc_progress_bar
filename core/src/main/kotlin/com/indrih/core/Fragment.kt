package com.indrih.core

import android.app.Dialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/** @return `true` если [T] есть в иерархии, иначе `false`. */
inline fun <reified T> Fragment.isThereHierarchy(): Boolean {
    var current: Fragment? = this
    while (current != null) {
        if (current is T) return true
        current = current.parentFragment
    }
    return false
}

/** Скрывает [dialog] при [Fragment.onPause]. */
fun Fragment.dismissOnPause(dialog: Dialog) {
    viewLifecycleOwner.lifecycle.addObserver(
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                dialog.dismiss()
            }
        }
    )
}

/** Создать фрагмент по дженерику. */
inline fun <reified F : Fragment> FragmentFactory.create(): F {
    val fragment = instantiate(
        F::class.java.classLoader
            ?: throw error("Невозможно создать инстанс фрагмента ${fragmentName<F>()}, classLoader == null"),
        fragmentName<F>()
    )
    return fragment as? F ?: throw error("Создался $fragment, а ожидалось ${fragmentName<F>()}")
}

/** @return имя класса [T]. */
inline fun <reified T : Fragment> fragmentName(): String =
    T::class.java.name
