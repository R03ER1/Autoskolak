package com.example.autokolk

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class AlexDeathActivity : AppCompatActivity() {
    private lateinit var deadLionImage: ImageView
    private lateinit var holdButton: ImageView
    private lateinit var holdProgressText: TextView
    private lateinit var deathTitle: TextView
    
    private var holdStartTime: Long = 0
    private var isHolding = false
    private var holdHandler: Handler? = null
    private var holdRunnable: Runnable? = null
    private var rotationAnimator: ObjectAnimator? = null
    
    private val HOLD_DURATION_MS = 5000L // 5 seconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alex_death)
        
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        
        deadLionImage = findViewById(R.id.deadLionImage)
        holdButton = findViewById(R.id.holdButton)
        holdProgressText = findViewById(R.id.holdProgressText)
        deathTitle = findViewById(R.id.deathTitle)
        
        // Set initial button background drawable level to 0 (empty)
        (holdButton.background as? android.graphics.drawable.LayerDrawable)?.let { layerDrawable ->
            val progressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress)
            progressDrawable?.level = 0
        }
        
        // Get lion name
        val prefs = getSharedPreferences("lesson_progress", MODE_PRIVATE)
        val lionName = prefs.getString("lion_name", "Alex") ?: "Alex"
        deathTitle.text = "⚠️ ${lionName.uppercase()} VYHLADOVĚL... ⚠️"
        
        // Load dead lion image rotated 90 degrees
        try {
            assets.open("images/alex/AlexDead.png").use { input ->
                val bmp = android.graphics.BitmapFactory.decodeStream(input)
                deadLionImage.setImageBitmap(bmp)
            }
            deadLionImage.rotation = 90f
        } catch (_: Throwable) { }
        
        // Setup hold button
        holdButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startHolding()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    stopHolding()
                    true
                }
                else -> false
            }
        }
    }
    
    private fun startHolding() {
        if (isHolding) return
        isHolding = true
        holdStartTime = System.currentTimeMillis()
        
        // Reset button fill level
        (holdButton.background as? android.graphics.drawable.LayerDrawable)?.let { layerDrawable ->
            val progressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress)
            progressDrawable?.level = 0
        }
        
        // Start rotation animation
        rotationAnimator = ObjectAnimator.ofFloat(deadLionImage, "rotation", 90f, 0f)
        rotationAnimator?.duration = HOLD_DURATION_MS
        rotationAnimator?.start()
        
        // Start fill animation - animate drawable level from 0 to 10000
        (holdButton.background as? android.graphics.drawable.LayerDrawable)?.let { layerDrawable ->
            val progressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress)
            progressDrawable?.let {
                val fillAnimator = ObjectAnimator.ofInt(it, "level", 0, 10000)
                fillAnimator.duration = HOLD_DURATION_MS
                fillAnimator.start()
            }
        }
        
        // Start progress update
        holdHandler = Handler(Looper.getMainLooper())
        holdRunnable = object : Runnable {
            override fun run() {
                if (!isHolding) return
                
                val elapsed = System.currentTimeMillis() - holdStartTime
                val secondsRemaining = ((HOLD_DURATION_MS - elapsed) / 1000f).coerceAtLeast(0f)
                
                if (elapsed >= HOLD_DURATION_MS) {
                    // Completed!
                    onHoldComplete()
                } else {
                    holdProgressText.text = String.format("%.1f", secondsRemaining)
                    holdHandler?.postDelayed(this, 50L)
                }
            }
        }
        holdRunnable?.let { holdHandler?.post(it) }
    }
    
    private fun stopHolding() {
        if (!isHolding) return
        isHolding = false
        
        rotationAnimator?.cancel()
        rotationAnimator = null
        holdHandler?.removeCallbacksAndMessages(null)
        holdRunnable = null
        holdProgressText.text = ""
        
        // Reset button fill level
        (holdButton.background as? android.graphics.drawable.LayerDrawable)?.let { layerDrawable ->
            val progressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress)
            progressDrawable?.level = 0
        }
        
        // Reset rotation
        deadLionImage.rotation = 90f
    }
    
    private fun onHoldComplete() {
        isHolding = false
        holdHandler?.removeCallbacksAndMessages(null)
        rotationAnimator?.cancel()
        
        // Change to happy lion
        try {
            val progress = LessonProgress(this)
            val imageName = if (progress.isSunglassesEnabled()) {
                "CAlexHappy.png"
            } else {
                "AlexHappy.png"
            }
            assets.open("images/alex/$imageName").use { input ->
                val bmp = android.graphics.BitmapFactory.decodeStream(input)
                deadLionImage.setImageBitmap(bmp)
            }
            deadLionImage.rotation = 0f
        } catch (_: Throwable) { }
        
        // Revive the lion - set hunger to a reasonable value
        val hungerManager = HungerManager(this)
        hungerManager.setCurrentHunger(50) // Set to 50% hunger
        
        // Close after a brief delay
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 1000L)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopHolding()
        rotationAnimator?.cancel()
        holdHandler?.removeCallbacksAndMessages(null)
    }
}

