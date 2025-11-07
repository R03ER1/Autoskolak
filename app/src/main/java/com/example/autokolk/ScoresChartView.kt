package com.example.autokolk

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class ScoresChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x66FFFFFF.toInt()
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x33FFFFFF
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFC107.toInt() // Amber-ish
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.FILL
    }
    private val thresholdPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFF5252.toInt() // Red 400
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private var scores: List<Int> = emptyList()
    private var maxPoints: Int = 50

    fun setScores(scores: List<Int>, maxPoints: Int) {
        this.scores = scores
        this.maxPoints = if (maxPoints <= 0) 50 else maxPoints
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val left = paddingLeft.toFloat()
        val top = paddingTop.toFloat()
        val right = (width - paddingRight).toFloat()
        val bottom = (height - paddingBottom).toFloat()

        // Draw axes
        canvas.drawLine(left, bottom, right, bottom, axisPaint)
        canvas.drawLine(left, top, left, bottom, axisPaint)

        // Grid lines (25, 50, 75%)
        for (i in 1..3) {
            val y = bottom - (bottom - top) * (i / 4f)
            canvas.drawLine(left, y, right, y, gridPaint)
        }

        if (scores.isEmpty()) return

        val contentWidth = (right - left).coerceAtLeast(1f)
        val contentHeight = (bottom - top).coerceAtLeast(1f)
        val count = scores.size
        val dx = if (count <= 1) 0f else contentWidth / (count - 1)

        val path = Path()
        for ((index, value) in scores.withIndex()) {
            val x = left + dx * index
            val clamped = value.coerceIn(0, maxPoints)
            val ratio = clamped.toFloat() / maxPoints.toFloat()
            val y = bottom - ratio * contentHeight
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, linePaint)

        // Points
        for ((index, value) in scores.withIndex()) {
            val x = left + dx * index
            val clamped = value.coerceIn(0, maxPoints)
            val ratio = clamped.toFloat() / maxPoints.toFloat()
            val y = bottom - ratio * contentHeight
            canvas.drawCircle(x, y, 5f, pointPaint)
        }

        // Threshold line at score 43
        val thresholdScore = 43
        val clampedThreshold = thresholdScore.coerceIn(0, maxPoints)
        val thresholdRatio = clampedThreshold.toFloat() / maxPoints.toFloat()
        val thresholdY = bottom - thresholdRatio * contentHeight
        canvas.drawLine(left, thresholdY, right, thresholdY, thresholdPaint)
    }
}


