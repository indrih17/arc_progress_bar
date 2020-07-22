package com.indrih.line_progress_bar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.RectF
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.use
import com.indrih.core.dpToPx
import com.indrih.core.getDimensionPixelSizeOrNull
import com.indrih.core.getFloatOrNull
import com.indrih.core.getColor
import com.indrih.core.hotInvalidatable
import com.indrih.core.invalidatable
import kotlin.math.roundToInt

/**
 * Прогресс бар в виде линии.
 * Все его параметры вынесены в `declare-styleable` и являются настраиваемыми, кроме параметров [LinearGradient].
 * Все параметры автоматически сохраняются в [Bundle].
 */
class LineProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    /** Толщина прогресс бара. */
    var strokeWidth: Float by hotInvalidatable(20f.dpToPx()) {
        it.also(paint::setStrokeWidth)
    }

    /**
     * Максимальный прогресс.
     * По умолчанию 100, но к примеру если у Вас 489 файлов, Вы можете поставить
     * значение 489 и при загрузке каждого файла инкрементировать [currentProgress],
     * а progress bar автоматически выведет информацию в процентах.
     */
    var maxProgress: Float by invalidatable(100f)

    /**
     * Текущий прогресс.
     * @see maxProgress
     */
    var currentProgress: Float by invalidatable(0f) { newValue ->
        if (newValue > maxProgress) maxProgress else newValue
    }

    /** Координаты основного прямоугольника. */
    private val mainRectF = RectF()

    /** Координаты прогресса. */
    private val progressRectF = RectF()

    private val paint = Paint().also {
        it.isAntiAlias = true
        it.style = Paint.Style.FILL
        it.strokeCap = Paint.Cap.ROUND
    }

    init {
        context.theme
            .obtainStyledAttributes(attrs, R.styleable.LineProgress, defStyleAttr, 0)
            .use { attributes ->
                selectLineAttributes(attributes)
                selectProgressAttributes(attributes)
            }
    }

    private fun selectLineAttributes(attributes: TypedArray) {
        attributes
            .getDimensionPixelSizeOrNull(R.styleable.LineProgress_line_stroke_width)
            ?.let { strokeWidth = it.toFloat() }
    }

    private fun selectProgressAttributes(attributes: TypedArray) {
        attributes
            .getFloatOrNull(R.styleable.LineProgress_line_max)
            ?.let { maxProgress = it }
        attributes
            .getFloatOrNull(R.styleable.LineProgress_line_progress)
            ?.let { currentProgress = it }
    }

    /** Дефолтная ширина вью (в dp). Нужен для wrap_content. */
    private val defaultWidth = 320f

    override fun getSuggestedMinimumWidth(): Int = defaultWidth.dpToPx().roundToInt()

    /** Дефолтная высота вью (в dp). Нужен для wrap_content. */
    private val defaultHeight = 20f

    override fun getSuggestedMinimumHeight(): Int = defaultHeight.dpToPx().roundToInt()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = resolveSize(
            suggestedMinimumWidth + paddingStart + paddingEnd,
            widthMeasureSpec
        )
        val height = resolveSize(
            suggestedMinimumHeight + paddingTop + paddingBottom,
            heightMeasureSpec
        )
        setMeasuredDimension(width, height)
        mainRectF.set(
            0f,
            0f,
            width.toFloat(),
            height.toFloat()
        )
        progressRectF.set(mainRectF)
    }

    private var insetY: Float = Float.MAX_VALUE
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Вся линия сначала заполняется пустым
        paint.shader = createGradient(colorsOfUnfinishedPart)
        canvas.drawRoundRect(mainRectF, cornerRadius, cornerRadius, paint)

        // Сверху накладывается прогресс
        paint.shader = createGradient(colorsOfFinishedPart)
        with(mainRectF) {
            val width = (right - left) * (currentProgress / maxProgress)
            val height = bottom - top
            progressRectF.setWidth(left, width)
            if (width < height) {
                insetY = height / 2 - width
                progressRectF.insetYFrom(this, if (insetY >= 0) insetY else 0f)
                canvas.drawOval(progressRectF, paint)
            } else {
                progressRectF.setYFrom(this)
                canvas.drawRoundRect(progressRectF, cornerRadius, cornerRadius, paint)
            }
        }
    }

    /** Установить ширину от левого края. */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun RectF.setWidth(left: Float, width: Float) {
        this.left = left
        this.right = left + width
    }

    /**  */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun RectF.insetYFrom(rectF: RectF, inset: Float) {
        this.top = rectF.top + inset
        this.bottom = rectF.bottom - inset
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun RectF.setYFrom(rectF: RectF) {
        this.top = rectF.top
        this.bottom = rectF.bottom
    }

    /** Цвета незавершенной части прогресса. */
    private val colorsOfUnfinishedPart = intArrayOf(
        getColor(R.color.progress_dim_red),
        getColor(R.color.progress_dim_yellow),
        getColor(R.color.progress_dim_green)
    )

    /** Цвета завершённой части прогресса. */
    private val colorsOfFinishedPart = intArrayOf(
        getColor(R.color.progress_bright_red),
        getColor(R.color.progress_bright_yellow),
        getColor(R.color.progress_bright_green)
    )

    /**
     * Градиент цветов прогресса.
     * ВАЖНО: каждый раз нужно генерировать новый инстанс, т.к. он не переиспользуемый.
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun createGradient(colors: IntArray): LinearGradient =
        LinearGradient(
            0f,
            0f,
            width.toFloat(),
            0f, // цвета будут рисоваться горизонтально (1-ый цвет слева, N-ый цвет справа).
            colors, // Массив цветов, из которых будет составляться гамма завершённой части прогресса.
            floatArrayOf(
                leftColorPosition,
                centralColorPosition,
                rightColorPosition
            ),
            Shader.TileMode.CLAMP // не имеет значения, поставил просто так.
        )

    private companion object {
        private const val cornerRadius = 35f
        private const val leftColorPosition = 0f
        private const val centralColorPosition = 0.52f
        private const val rightColorPosition = 1f
    }
}
