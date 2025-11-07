package com.example.autokolk

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CurvyPathView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.button_normal)
        strokeWidth = 8f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val path = Path()
    private var starPositions = mutableListOf<PointF>()
    private var viewWidth = 0
    private var viewHeight = 0

    fun setStarPositions(positions: List<PointF>) {
        starPositions.clear()
        starPositions.addAll(positions)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
        createCurvyPath()
    }

    private fun createCurvyPath() {
        if (starPositions.size < 2) return

        path.reset()
        val density = resources.displayMetrics.density
        val starRadius = 28f * density // 28dp radius

        // Start from first star
        val firstPos = starPositions[0]
        path.moveTo(firstPos.x, firstPos.y + starRadius)

        for (i in 1 until starPositions.size) {
            val currentPos = starPositions[i]
            val prevPos = starPositions[i - 1]
            
            // Create simple curved connection between stars
            val midX = (prevPos.x + currentPos.x) / 2f
            val midY = (prevPos.y + currentPos.y) / 2f
            
            // Add some curve by offsetting the midpoint
            val curveOffset = if (i % 2 == 0) 30f * density else -30f * density
            val controlPoint = PointF(midX + curveOffset, midY)
            
            path.quadTo(
                controlPoint.x, controlPoint.y,
                currentPos.x, currentPos.y - starRadius
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (starPositions.size >= 2) {
            canvas.drawPath(path, pathPaint)
        }
    }
}
