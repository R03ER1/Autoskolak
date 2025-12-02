package com.example.autokolk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.text.SpannableString
import android.text.Spannable
import android.text.style.ImageSpan
import com.google.android.material.button.MaterialButton
import android.widget.ImageView
import android.graphics.BitmapFactory

object TutorialManager {
    private const val PREFS = "tutorial_overlays"
    private const val TYPING_DELAY_MS = 25L // Fast typing animation - 25ms per character

    fun hasShown(activity: android.app.Activity, key: String): Boolean {
        val prefs = activity.getSharedPreferences(PREFS, android.content.Context.MODE_PRIVATE)
        return prefs.getBoolean(key, false)
    }

    private fun markShown(activity: android.app.Activity, key: String) {
        val prefs = activity.getSharedPreferences(PREFS, android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean(key, true).apply()
    }

    private fun animateTypingText(textView: TextView, finalText: CharSequence, handler: Handler) {
        textView.text = "" // Start with empty text
        val text = finalText.toString()
        var currentIndex = 0
        
        fun typeNextChar() {
            if (currentIndex < text.length && textView.parent != null) {
                val nextChar = text[currentIndex]
                // Check if this is part of a [mince] token
                val remainingText = text.substring(currentIndex)
                if (remainingText.startsWith("[mince]")) {
                    // Add the entire token at once
                    val currentText = textView.text.toString()
                    textView.text = currentText + "[mince]"
                    currentIndex += 7 // Skip the entire token
                } else {
                    // Add single character
                    val currentText = textView.text.toString()
                    textView.text = currentText + nextChar
                    currentIndex++
                }
                
                if (currentIndex < text.length) {
                    handler.postDelayed({ typeNextChar() }, TYPING_DELAY_MS)
                } else {
                    // Animation complete, apply spannable if needed
                    if (finalText is Spannable) {
                        textView.text = finalText
                    }
                }
            }
        }
        
        typeNextChar()
    }

    fun showIfNeeded(activity: android.app.Activity, key: String, message: String, afterDismiss: (() -> Unit)? = null) {
        if (hasShown(activity, key)) {
            afterDismiss?.invoke()
            return
        }
        show(activity, key, message, afterDismiss)
    }

    fun show(activity: android.app.Activity, key: String, message: String, afterDismiss: (() -> Unit)? = null) {
        val root = activity.findViewById<View>(android.R.id.content) as ViewGroup
        val overlay = LayoutInflater.from(activity).inflate(R.layout.view_tutorial_overlay, root, false)
        overlay.isClickable = true
        overlay.isFocusable = true
        // Replace token [mince] with inline coin icon and prepare for typing animation
        val handler = Handler(Looper.getMainLooper())
        val okButton = overlay.findViewById<MaterialButton>(R.id.tutorialOk)
        val tutorialText = overlay.findViewById<TextView>(R.id.tutorialText)
        val tutorialImage = overlay.findViewById<ImageView>(R.id.tutorialImage)
        if (tutorialText != null) {
            val token = "[mince]"
            if (message.contains(token)) {
                val spannable = SpannableString(message)
                var start = message.indexOf(token)
                while (start >= 0) {
                    val end = start + token.length
                    try {
                        val drawable = ContextCompat.getDrawable(activity, R.drawable.ic_coin)
                        if (drawable != null) {
                            // Scale icon to match text line height
                            val lineHeight = tutorialText.textSize.toInt()
                            val size = (lineHeight * 1.2f).toInt()
                            drawable.setBounds(0, 0, size, size)
                            val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM)
                            spannable.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    } catch (_: Throwable) { }
                    start = message.indexOf(token, end)
                }
                // Animate typing with spannable
                tutorialText.post {
                    animateTypingText(tutorialText, spannable, handler)
                }
            } else {
                // Animate typing with plain text
                tutorialText.post {
                    animateTypingText(tutorialText, message, handler)
                }
            }
        }
        // Load Alex image next to the text
        try {
            val hungerManager = HungerManager(activity)
            val hunger = hungerManager.getCurrentHunger()
            val imageName = when {
                hunger <= 20 -> "AlexHungry.png"
                hunger <= 40 -> "AlexSad.png"
                hunger <= 60 -> "Alex.png"
                hunger <= 80 -> "AlexHappy.png"
                else -> "AlexCool.png"
            }
            val progress = LessonProgress(activity)
            val finalName = if (progress.isSunglassesEnabled()) "C" + imageName else imageName
            val input = activity.assets.open("images/alex/" + finalName)
            val bmp = BitmapFactory.decodeStream(input)
            input.close()
            tutorialImage?.setImageBitmap(bmp)
        } catch (_: Throwable) { }
        
        okButton?.setOnClickListener {
            // Disable button to prevent multiple clicks
            okButton.isEnabled = false
            okButton.isClickable = false
            
            // Animate lion sliding down
            tutorialImage?.let { img ->
                val slideDownAnim = AnimationUtils.loadAnimation(activity, R.anim.slide_lion_to_bottom)
                img.startAnimation(slideDownAnim)
            }
            
            // Animate text and button fading out
            val fadeOutAnim = AnimationUtils.loadAnimation(activity, R.anim.fade_out_slow)
            tutorialText?.startAnimation(fadeOutAnim)
            okButton.startAnimation(fadeOutAnim)
            
            // Remove view after animations complete (use the longer duration)
            handler.postDelayed({
                root.removeView(overlay)
                markShown(activity, key)
                afterDismiss?.invoke()
            }, 400) // Match the fade out duration
        }
        root.addView(overlay)
        // Animate the lion sliding from the bottom after the view is added
        tutorialImage?.let { img ->
            img.post {
                val animation = AnimationUtils.loadAnimation(activity, R.anim.slide_lion_from_bottom)
                img.startAnimation(animation)
            }
        }
    }

    fun showSequenceIfNeeded(activity: android.app.Activity, keysAndMessages: List<Pair<String, String>>) {
        if (keysAndMessages.isEmpty()) return
        fun step(index: Int) {
            if (index >= keysAndMessages.size) return
            val (key, msg) = keysAndMessages[index]
            if (hasShown(activity, key)) {
                step(index + 1)
            } else {
                show(activity, key, msg) { step(index + 1) }
            }
        }
        step(0)
    }
}


