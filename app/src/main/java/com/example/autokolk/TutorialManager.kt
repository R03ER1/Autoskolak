package com.example.autokolk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    fun hasShown(activity: android.app.Activity, key: String): Boolean {
        val prefs = activity.getSharedPreferences(PREFS, android.content.Context.MODE_PRIVATE)
        return prefs.getBoolean(key, false)
    }

    private fun markShown(activity: android.app.Activity, key: String) {
        val prefs = activity.getSharedPreferences(PREFS, android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean(key, true).apply()
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
        // Replace token [mince] with inline coin icon
        val textView = overlay.findViewById<TextView>(R.id.tutorialText)
        if (textView != null) {
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
                            val lineHeight = textView.textSize.toInt()
                            val size = (lineHeight * 1.2f).toInt()
                            drawable.setBounds(0, 0, size, size)
                            val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM)
                            spannable.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    } catch (_: Throwable) { }
                    start = message.indexOf(token, end)
                }
                textView.text = spannable
            } else {
                textView.text = message
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
            overlay.findViewById<ImageView>(R.id.tutorialImage)?.setImageBitmap(bmp)
        } catch (_: Throwable) { }
        overlay.findViewById<MaterialButton>(R.id.tutorialOk)?.setOnClickListener {
            root.removeView(overlay)
            markShown(activity, key)
            afterDismiss?.invoke()
        }
        root.addView(overlay)
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


