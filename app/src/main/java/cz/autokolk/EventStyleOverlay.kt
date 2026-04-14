package cz.autokolk

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton

/**
 * Full-screen overlay matching random events: dimmed scrim, Alex image, confetti, pop animations, OK.
 */
object EventStyleOverlay {

    fun show(
        activity: Activity,
        title: String,
        message: String,
        valueLine: String,
        onDismiss: (() -> Unit)? = null
    ) {
        if (activity.isFinishing) return
        if (android.os.Build.VERSION.SDK_INT >= 17 && activity.isDestroyed) return

        val root = activity.findViewById<View>(android.R.id.content) as ViewGroup
        val overlay = LayoutInflater.from(activity).inflate(R.layout.view_event_overlay, root, false)
        overlay.isClickable = true
        overlay.isFocusable = true

        val titleView = overlay.findViewById<TextView>(R.id.eventTitle)
        val messageView = overlay.findViewById<TextView>(R.id.eventMessage)
        val valueView = overlay.findViewById<TextView>(R.id.eventValue)
        val imageView = overlay.findViewById<ImageView>(R.id.eventImage)

        titleView?.text = title
        messageView?.text = message
        valueView?.text = valueLine

        val cool = loadAlexCoolBitmap(activity.assets)
        if (cool != null) {
            imageView?.setImageBitmap(cool)
        } else {
            imageView?.setImageResource(R.drawable.ic_alex)
        }

        val confettiView = overlay.findViewById<ConfettiView>(R.id.confettiView)
        overlay.findViewById<MaterialButton>(R.id.eventOk)?.setOnClickListener {
            confettiView?.stop()
            root.removeView(overlay)
            onDismiss?.invoke()
        }
        root.addView(overlay)

        overlay.post {
            try {
                imageView?.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in_bottom))
                titleView?.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.pop_scale))
                messageView?.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.pop_scale))
                valueView?.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.pop_scale))

                try {
                    confettiView?.postDelayed({
                        try {
                            if (confettiView?.isAttachedToWindow == true && confettiView?.visibility == View.VISIBLE) {
                                confettiView?.visibility = View.VISIBLE
                                confettiView?.startFor(2000L, 300L)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, 300)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadAlexCoolBitmap(assets: android.content.res.AssetManager): Bitmap? {
        val paths = listOf("images/AlexCool.png", "images/alex/AlexCool.png", "alex/AlexCool.png")
        for (path in paths) {
            try {
                assets.open(path).use { input ->
                    return BitmapFactory.decodeStream(input)
                }
            } catch (_: Throwable) {
            }
        }
        return null
    }
}
