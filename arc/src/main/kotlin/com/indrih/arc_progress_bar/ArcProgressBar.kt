package com.indrih.arc_progress_bar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.RectF
import android.os.Bundle
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat
import com.indrih.core.dpToPx
import com.indrih.core.spToPx
import com.indrih.core.getDimensionPixelSizeOrNull
import com.indrih.core.getFloatOrNull
import com.indrih.core.getResourceIdOrNull
import com.indrih.core.getNotEmptyStringOrNull
import com.indrih.core.getColorOrNull
import com.indrih.core.getColor
import com.indrih.core.hotInvalidatable
import com.indrih.core.invalidatable
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Прогресс бар в виде арки.
 * Все его параметры вынесены в `declare-styleable` и являются настраиваемыми, кроме параметров [LinearGradient].
 * Все параметры автоматически сохраняются в [Bundle].
 */
class ArcProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr) {
    /** Угол, на который будет рисоваться арка. */
    var arcAngle: Float by invalidatable(200f)

    /** Толщина прогресс бара. */
    var strokeWidth: Float by hotInvalidatable(20f.dpToPx()) {
        it.also(paint::setStrokeWidth)
    }

    /** Текст, находящийся в центре арки. */
    var centralText: String? by invalidatable(null)

    /** Размер текста, находящегося в центре арки. */
    var centralTextSize: Float by invalidatable(12f.spToPx())

    /** Цвет текста, находящегося в центре арки. */
    var centralTextColor: Int by invalidatable(getColor(R.color.progress_color_text_color))

    /** Appearance текста, находящегося в центре арки. */
    var centralTextAppearance: Int by invalidatable(android.R.style.TextAppearance_Material_Caption)

    /** Текст, находящийся внизу арки. */
    var bottomText: String? by invalidatable(null)

    /** Размер текста, находящегося внизу арки. */
    var bottomTextSize: Float by invalidatable(17f.spToPx())

    /** Цвет текста, находящегося внизу арки. */
    var bottomTextColor: Int by invalidatable(Color.BLACK)

    /** Отступ текста, находящегося внизу арки, от рутового вью. */
    var bottomTextMargin: Float by invalidatable(20f.dpToPx())

    /** Appearance текста, находящегося внизу арки. */
    var bottomTextAppearance: Int by invalidatable(android.R.style.TextAppearance_Material_Body2)

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
    var currentProgress: Float by invalidatable(0f) { min(it, maxProgress) }

    /** Координаты прямоугольника, внутри которого мы будем рисовать арку. */
    private val rectF = RectF()

    /** Текст вью, через которую мы будем проставлять text appearance. */
    private val textView = TextView(context)

    private val textPaint = TextPaint().also {
        it.isAntiAlias = true
    }
    private val paint = Paint().also {
        it.isAntiAlias = true
        it.style = Paint.Style.STROKE
        it.strokeCap = Paint.Cap.ROUND
    }

    init {
        context.theme
            .obtainStyledAttributes(attrs, R.styleable.ArcProgress, defStyleAttr, defStyleRes)
            .use { attributes ->
                selectArcAttributes(attributes)
                selectCentralTextAttributes(attributes)
                selectBottomTextAttributes(attributes)
                selectProgressAttributes(attributes)
            }
    }

    private fun selectArcAttributes(attributes: TypedArray) {
        attributes
            .getFloatOrNull(R.styleable.ArcProgress_arc_angle)
            ?.let { arcAngle = it }
        attributes
            .getDimensionPixelSizeOrNull(R.styleable.ArcProgress_arc_stroke_width)
            ?.let { strokeWidth = it.toFloat() }
    }

    private fun selectCentralTextAttributes(attributes: TypedArray) {
        attributes
            .getString(R.styleable.ArcProgress_arc_central_text)
            ?.let { centralText = it }
        attributes
            .getDimensionPixelSizeOrNull(R.styleable.ArcProgress_arc_central_text_size)
            ?.let { centralTextSize = it.toFloat() }
        attributes
            .getColorOrNull(R.styleable.ArcProgress_arc_central_text_color)
            ?.let { centralTextColor = it }
        attributes
            .getResourceIdOrNull(R.styleable.ArcProgress_arc_central_text_appearance)
            ?.let { centralTextAppearance = it }
    }

    private fun selectBottomTextAttributes(attributes: TypedArray) {
        attributes
            .getNotEmptyStringOrNull(R.styleable.ArcProgress_arc_bottom_text)
            ?.let { bottomText = it }
        attributes
            .getDimensionPixelSizeOrNull(R.styleable.ArcProgress_arc_bottom_text_size)
            ?.let { bottomTextSize = it.toFloat() }
        attributes
            .getColorOrNull(R.styleable.ArcProgress_arc_bottom_text_color)
            ?.let { bottomTextColor = it }
        attributes
            .getDimensionPixelSizeOrNull(R.styleable.ArcProgress_arc_bottom_text_margin)
            ?.let { bottomTextMargin = it.toFloat() }
        attributes
            .getResourceIdOrNull(R.styleable.ArcProgress_arc_bottom_text_appearance)
            ?.let { bottomTextAppearance = it }
    }

    private fun selectProgressAttributes(attributes: TypedArray) {
        attributes
            .getFloatOrNull(R.styleable.ArcProgress_arc_max)
            ?.let { maxProgress = it }
        attributes
            .getFloatOrNull(R.styleable.ArcProgress_arc_progress)
            ?.let { currentProgress = it }
    }

    /** Дефолтная ширина вью (в dp). Нужен для wrap_content. */
    private val defaultWidth = 150f

    override fun getSuggestedMinimumWidth(): Int = defaultWidth.dpToPx().roundToInt()

    /** Дефолтная высота вью (в dp). Нужен для wrap_content. */
    private val defaultHeight = 96f

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

        // Если мы задаём ширину арки 150 и высоту арки 96, то при попытке отрисовать арку
        // canvas будет рисовать круг 150 на 96. Соотвественно, высота арки будет меньше ожидаемого.
        // Поэтому необходимо высчитать полную высоту арки, как будто арка превратилась в круг.
        val fullHeight = (1 + arcAngle / degreesInCirce) * height
        val strokePadding = strokeWidth / 2
        rectF.set(
            strokePadding,
            strokePadding,
            width - strokePadding,
            fullHeight - strokePadding
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawArc(canvas)
        centralText
            ?.takeIf { it.isNotEmpty() }
            ?.let { drawCentralText(canvas, it) }
        bottomText
            ?.takeIf { it.isNotEmpty() }
            ?.let { drawBottomText(canvas, it) }
    }

    private fun drawArc(canvas: Canvas) {
        val startAngle = highestPoint - arcAngle / 2f

        // Вся арка сначала заполняется пустым
        paint.shader = createGradient(colorsOfUnfinishedPart)
        canvas.drawArc(rectF, startAngle, arcAngle, false, paint)

        // Сверху накладывается прогресс
        val finishedSweepAngle = currentProgress / maxProgress * arcAngle

        paint.shader = createGradient(colorsOfFinishedPart)
        canvas.drawArc(rectF, startAngle, finishedSweepAngle, false, paint)
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

    private fun drawCentralText(canvas: Canvas, text: String) {
        TextViewCompat.setTextAppearance(textView, centralTextAppearance)
        with(textPaint) {
            color = centralTextColor
            textSize = centralTextSize
            typeface = textView.typeface
        }
        val textHeight = average(
            textPaint.descent(),
            textPaint.ascent()
        )
        canvas.drawText(
            text,
            (width - textPaint.measureText(text)) / 2.0f,
            height / 2.0f - textHeight,
            textPaint
        )
    }

    private fun drawBottomText(canvas: Canvas, text: String) {
        TextViewCompat.setTextAppearance(textView, bottomTextAppearance)
        with(textPaint) {
            color = bottomTextColor
            textSize = bottomTextSize
            typeface = textView.typeface
        }
        val textHeight = average(
            textPaint.descent(),
            textPaint.ascent()
        )
        canvas.drawText(
            text,
            (width - textPaint.measureText(text)) / 2.0f,
            height - textHeight - bottomTextMargin,
            textPaint
        )
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun average(a: Float, b: Float) = (a + b) / 2

    private companion object {
        private const val degreesInCirce = 360
        private const val leftColorPosition = 0f
        private const val centralColorPosition = 0.52f
        private const val rightColorPosition = 1f

        /**
         * По дефолту анимация пойдёт от правой части (0 градусов) вниз (90),
         * потом на левую часть (180), затем вверх (270), и закончит в правой части (360 или 0).
         */
        private const val highestPoint = 270
    }
}
