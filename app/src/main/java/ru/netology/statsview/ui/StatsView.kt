package ru.netology.statsview.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attributesSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attributesSet, defStyleAttr, defStyleRes) {

    private var arcsProgress = 0f
    private var rotationProgress = 0f
    private var alphaProgress = 0f
    private var isAnimating = false

    private val arcsAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 3000
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animation ->
            arcsProgress = animation.animatedValue as Float
            invalidate()
        }
    }

    private val rotationAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 3000
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animation ->
            rotationProgress = animation.animatedValue as Float
            invalidate()
        }
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isAnimating = false
            }
        })
    }

    private val alphaAnimator = ValueAnimator.ofFloat(0.5f, 1f).apply {
        duration = 200
        startDelay = 2500
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animation ->
            alphaProgress = animation.animatedValue as Float
            invalidate()
        }
    }

    private fun startCircleAnimation() {
        arcsProgress = 0f
        rotationProgress = 0f
        alphaProgress = 0f
        isAnimating = true

        arcsAnimator.start()
        rotationAnimator.start()
        alphaAnimator.start()
    }

    fun startCombinedAnimation() {
        post {
            startCircleAnimation()
        }
    }

    private var textSize = AndroidUtils.dp(context, 20).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 5)

    private var colors = emptyList<Int>()

    init {
        context.withStyledAttributes(attributesSet, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth.toFloat()).toInt()

            colors = listOf(
                getColor(R.styleable.StatsView_color1, generateRandomColor()),
                getColor(R.styleable.StatsView_color2, generateRandomColor()),
                getColor(R.styleable.StatsView_color3, generateRandomColor()),
                getColor(R.styleable.StatsView_color4, generateRandomColor())
            )
        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()

    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = this@StatsView.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    private val smallCirclePaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply { color = colors.first() }



    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        canvas.save()
        canvas.rotate(360f * rotationProgress, center.x, center.y)

        var startAngle = -90F
        val allData = data.sum()

        if (allData > 0) {
            data.forEachIndexed { index, item ->
                val fullAngle = item.getCoefficientOfAll(allData) * 360F
                paint.color = colors.getOrElse(index) { generateRandomColor() }

                val currentAngle = if (isAnimating) fullAngle * arcsProgress else fullAngle

                canvas.drawArc(
                    oval,  // ВСЕГДА используем полный радиус!
                    startAngle,
                    currentAngle,
                    false,
                    paint
                )

                startAngle += fullAngle
            }
        }

        canvas.restore()
        val currentAlpha = (alphaProgress * 255).toInt()
        smallCirclePaint.alpha = currentAlpha

        canvas.drawCircle(
            center.x,
            (lineWidth / 2).toFloat(),
            (lineWidth / 2).toFloat(),
            smallCirclePaint
        )

        val percentageText = if (allData > 0) {
            "%.2f%%".format(allData.getCoefficientOfAll(allData) * 100)
        } else {
            "0%"
        }

        canvas.drawText(
            percentageText,
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        arcsAnimator.cancel()
        rotationAnimator.cancel()
        alphaAnimator.cancel()
    }

    private fun generateRandomColor(): Int = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

    private fun Float.getCoefficientOfAll(all: Float) = if (all > 0) this / all else 0f
}