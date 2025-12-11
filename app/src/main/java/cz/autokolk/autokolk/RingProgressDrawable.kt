package cz.autokolk

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable

class RingProgressDrawable(
    private var progressFraction: Float,
    private var trackColor: Int,
    private var progressColor: Int,
    private var strokeWidthPx: Float,
    private var ringMarginPx: Float = 0f
) : Drawable() {

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val arcBounds = RectF()

    init {
        trackPaint.color = trackColor
        progressPaint.color = progressColor
        trackPaint.strokeWidth = strokeWidthPx
        progressPaint.strokeWidth = strokeWidthPx
    }

    override fun draw(canvas: Canvas) {
        // Calculate the center and radius to ensure the ring fits perfectly
        val centerX = bounds.centerX().toFloat()
        val centerY = bounds.centerY().toFloat()
        // Draw near the outer edge: subtract half stroke (since stroke is centered) and a configurable margin
        val radius = (minOf(bounds.width(), bounds.height()) / 2f) - (strokeWidthPx / 2f) - ringMarginPx
        
        arcBounds.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        // Draw full ring track
        canvas.drawArc(arcBounds, 0f, 360f, false, trackPaint)

        // Draw progress arc
        val sweep = (progressFraction.coerceIn(0f, 1f)) * 360f
        canvas.drawArc(arcBounds, -90f, sweep, false, progressPaint)
    }

    override fun setAlpha(alpha: Int) {
        trackPaint.alpha = alpha
        progressPaint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        trackPaint.colorFilter = colorFilter
        progressPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    fun setProgress(progress: Float) {
        this.progressFraction = progress
        invalidateSelf()
    }

    fun setColors(track: Int, progress: Int) {
        this.trackColor = track
        this.progressColor = progress
        trackPaint.color = track
        progressPaint.color = progress
        invalidateSelf()
    }

    fun setStrokeWidth(widthPx: Float) {
        this.strokeWidthPx = widthPx
        trackPaint.strokeWidth = widthPx
        progressPaint.strokeWidth = widthPx
        invalidateSelf()
    }

    fun setRingMargin(marginPx: Float) {
        this.ringMarginPx = marginPx
        invalidateSelf()
    }
}


