package com.indrih.core

import android.view.View
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Делегат, который при каждом обновлении [value] вызывает [View.postInvalidate].
 * Если вью только была создана и мы отдаём первичные данные для отрисовки,
 * то [View.invalidate] не будет вызван (внутри есть защита "от дурака").
 */
class InvalidateDelegate<T : Any?>(
    internal var value: T,
    private val update: (T) -> T
) : ReadWriteProperty<View, T> {
    /** Вызывается при каждом вызове геттера у проперти. */
    override fun getValue(thisRef: View, property: KProperty<*>): T =
        value

    /** Вызывается при каждом вызове сеттера у проперти. */
    override fun setValue(thisRef: View, property: KProperty<*>, value: T) {
        this.value = update(value)
        thisRef.postInvalidate()
    }
}

/**
 * Удобный экстеншен с дефолтной лямбдой. В классе не стоит ставить дефолтное значение лямбды,
 * чтобы любой человек, создающий функцию-обёртку над [InvalidateDelegate], не забывал
 * передать лямбду.
 * @see InvalidateDelegate
 */
fun <T : Any?> invalidatable(value: T, update: (T) -> T = { it }) =
    InvalidateDelegate(
        value = value,
        update = update
    )

/**
 * То же самое, что и [invalidatable], но при первом старте применяет [update].
 * Нужен для сократить количество дубляции логики.
 */
fun <T> View.hotInvalidatable(value: T, update: (T) -> T) =
    invalidatable(value, update).also { delegate ->
        post { delegate.value = update(value) }
    }
