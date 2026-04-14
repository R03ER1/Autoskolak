package cz.autokolk

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

class AlexDeathActivity : AutokolkActivity() {
    private lateinit var deadLionImage: ImageView
    private lateinit var holdButton: ImageView
    private lateinit var holdProgressText: TextView
    private lateinit var deathTitle: TextView
    
    private var holdStartTime: Long = 0
    private var isHolding = false
    private var holdHandler: Handler? = null
    private var holdRunnable: Runnable? = null
    private var rotationAnimator: ObjectAnimator? = null
    private var fillAnimator: ObjectAnimator? = null

    private val holdDurationMs = 3000L
    
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
        
        AlexDeadBitmapLoader.load(assets)?.let { bmp ->
            deadLionImage.setImageBitmap(bmp)
            deadLionImage.rotation = 90f
        }
        
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
        
        rotationAnimator = ObjectAnimator.ofFloat(deadLionImage, "rotation", 90f, 0f).apply {
            duration = holdDurationMs
            start()
        }

        (holdButton.background as? android.graphics.drawable.LayerDrawable)?.let { layerDrawable ->
            val progressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress)
            progressDrawable?.let {
                fillAnimator = ObjectAnimator.ofInt(it, "level", 0, 10000).apply {
                    duration = holdDurationMs
                    start()
                }
            }
        }
        
        // Start progress update
        holdHandler = Handler(Looper.getMainLooper())
        holdRunnable = object : Runnable {
            override fun run() {
                if (!isHolding) return
                
                val elapsed = System.currentTimeMillis() - holdStartTime
                val secondsRemaining = ((holdDurationMs - elapsed) / 1000f).coerceAtLeast(0f)
                
                if (elapsed >= holdDurationMs) {
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
        fillAnimator?.cancel()
        fillAnimator = null
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
        fillAnimator?.cancel()
        fillAnimator = null
        
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
        hungerManager.setCurrentHunger(50) // Set to 50% hunger (uses commit() to ensure synchronous write)
        
        // Verify hunger was set correctly
        val verifyHunger = hungerManager.getCurrentHunger()
        if (verifyHunger <= 0) {
            // Something went wrong - don't finish
            return
        }
        
        // Set result to indicate successful revive
        setResult(RESULT_OK)
        
        // Close immediately - no delay needed since hunger is already set
        finish()
    }
    
    
    override fun onDestroy() {
        super.onDestroy()
        stopHolding()
        rotationAnimator?.cancel()
        holdHandler?.removeCallbacksAndMessages(null)
    }
}

