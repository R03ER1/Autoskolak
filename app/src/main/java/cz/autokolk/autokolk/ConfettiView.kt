package cz.autokolk

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class ConfettiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private data class ConfettiParticle(
        var x: Float,
        var y: Float,
        var size: Float,
        var speedY: Float,
        var speedX: Float,
        var rotation: Float,
        var rotationSpeed: Float,
        val color: Int
    )

    private val particles = mutableListOf<ConfettiParticle>()
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val colors = intArrayOf(
        Color.parseColor("#FF6B6B"), // Red
        Color.parseColor("#4ECDC4"), // Teal
        Color.parseColor("#45B7D1"), // Blue
        Color.parseColor("#FFA07A"), // Light Salmon
        Color.parseColor("#98D8C8"), // Mint
        Color.parseColor("#F7DC6F"), // Yellow
        Color.parseColor("#BB8FCE"), // Purple
        Color.parseColor("#85C1E2")  // Light Blue
    )
    
    private val handler = Handler(Looper.getMainLooper())
    private var isAnimating = false
    private val particleCount = 50
    private var isFadingOut = false
    
    fun start() {
        if (isAnimating) return
        if (!isAttachedToWindow) return
        
        // Wait for view to be measured
        if (width <= 0 || height <= 0) {
            post {
                if (isAttachedToWindow && width > 0 && height > 0) {
                    start()
                }
            }
            return
        }
        
        try {
            isAnimating = true
            particles.clear()
            
            // Create initial particles
            for (i in 0 until particleCount) {
                createParticle()
            }
            
            // Start animation loop
            handler.post(animationRunnable)
        } catch (e: Exception) {
            isAnimating = false
            e.printStackTrace()
        }
    }

    fun startFor(durationMs: Long, fadeOutMs: Long = 300L) {
        start()
        // Schedule fade out and cleanup
        postDelayed({
            try {
                if (!isAttachedToWindow) return@postDelayed
                if (!isAnimating) return@postDelayed
                isFadingOut = true
                animate()
                    .alpha(0f)
                    .setDuration(fadeOutMs)
                    .withEndAction {
                        try {
                            stop()
                            particles.clear()
                            alpha = 1f
                            visibility = View.GONE
                            invalidate()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            isFadingOut = false
                        }
                    }
                    .start()
            } catch (e: Exception) {
                e.printStackTrace()
                stop()
                particles.clear()
                alpha = 1f
                visibility = View.GONE
                invalidate()
                isFadingOut = false
            }
        }, durationMs)
    }
    
    fun stop() {
        isAnimating = false
        handler.removeCallbacks(animationRunnable)
    }
    
    private fun createParticle() {
        if (width == 0 || height == 0) return
        
        val size = Random.nextFloat() * 12f + 6f
        val x = Random.nextFloat() * width
        val y = -size - Random.nextFloat() * 200f
        val speedY = Random.nextFloat() * 8f + 4f
        val speedX = (Random.nextFloat() - 0.5f) * 4f
        val rotation = Random.nextFloat() * 360f
        val rotationSpeed = (Random.nextFloat() - 0.5f) * 10f
        val color = colors[Random.nextInt(colors.size)]
        
        particles.add(ConfettiParticle(x, y, size, speedY, speedX, rotation, rotationSpeed, color))
    }
    
    private val animationRunnable = object : Runnable {
        override fun run() {
            if (!isAnimating || !isAttachedToWindow) {
                isAnimating = false
                return
            }
            
            try {
                if (width <= 0 || height <= 0) {
                    handler.postDelayed(this, 50)
                    return
                }
                
                // Update particles
                val iterator = particles.iterator()
                while (iterator.hasNext()) {
                    val particle = iterator.next()
                    particle.y += particle.speedY
                    particle.x += particle.speedX
                    particle.rotation += particle.rotationSpeed
                    
                    // Remove particles that are off screen
                    if (particle.y > height + particle.size) {
                        iterator.remove()
                        // Create new particle at top
                        if (particles.size < particleCount && width > 0 && height > 0) {
                            createParticle()
                        }
                    }
                }
                
                // Add new particles occasionally
                if (particles.size < particleCount && width > 0 && height > 0 && Random.nextFloat() < 0.3f) {
                    createParticle()
                }
                
                invalidate()
                handler.postDelayed(this, 16) // ~60 FPS
            } catch (e: Exception) {
                isAnimating = false
                e.printStackTrace()
            }
        }
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        try {
            if (width <= 0 || height <= 0) return
            if (!isAnimating && !isFadingOut && particles.isEmpty()) return
            
            for (particle in particles) {
                paint.color = particle.color
                canvas.save()
                canvas.translate(particle.x, particle.y)
                canvas.rotate(particle.rotation)
                canvas.drawRect(-particle.size / 2, -particle.size / 2, 
                              particle.size / 2, particle.size / 2, paint)
                canvas.restore()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }
}

