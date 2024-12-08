package com.example.nutricheck.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.nutricheck.R

class SemiCircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f
    private var totalCalories = 1900f
    private var max = 1900f

    private val strokeWidth = 20f
    private val padding = 40f

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = this@SemiCircularProgressBar.strokeWidth
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFDE4D")
        style = Paint.Style.STROKE
        strokeWidth = this@SemiCircularProgressBar.strokeWidth
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = ResourcesCompat.getFont(context, R.font.plus_jakarta_sans_bold)
    }

    private val rectF = RectF()

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = (width - 2 * padding) / 2

        textPaint.textSize = width / 7f
        rectF.set(
            padding,
            height / 2 - radius,
            width - padding,
            height / 2 + radius
        )

        canvas.drawArc(rectF, 180f, 180f, false, backgroundPaint)

        val sweepAngle = (progress / max) * 180f
        canvas.drawArc(rectF, 180f, sweepAngle, false, progressPaint)

        val progressText = "${(progress / max * totalCalories).toInt()}/${totalCalories.toInt()}"
        val fontMetrics = textPaint.fontMetrics
        val textY = height / 2f + radius / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2
        canvas.drawText(progressText, width / 2f, textY, textPaint)

        textPaint.textSize = width / 11f
        canvas.drawText("calories fulfilled", width / 2f, textY + width / 7f, textPaint)
    }


    fun setProgress(value: Float) {
        progress = value.coerceIn(0f, max)
        invalidate()
    }

    fun setTotalCalories(calories: Float) {
        totalCalories = calories
        max = calories
        invalidate()
    }

    fun setMax(value: Float) {
        max = value
        invalidate()
    }
}
