package cz.autokolk

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

class RandomEventManager(private val context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    data class Event(
        val message: String,
        val apply: (LessonProgress) -> Unit
    )

    companion object {
        private const val PREFS_NAME = "random_events"
        private const val KEY_NEXT_AT = "next_at"
        private const val KEY_DISABLED_UNTIL_TUTORIALS = "disabled_until_tutorials"
        private const val DAY_MS = 24L * 60L * 60L * 1000L

        fun scheduleNextRandom(prefs: android.content.SharedPreferences) {
            val now = System.currentTimeMillis()
            val days = Random.nextInt(1, 4) // 1..3 days
            val nextAt = now + days * DAY_MS + Random.nextLong(0, 6 * 60 * 60 * 1000L) // add up to 6h jitter
            prefs.edit().putLong(KEY_NEXT_AT, nextAt).apply()
        }
    }

    private fun buildEvents(): List<Event> {
        return listOf(
            // Lives: only positive life events
            Event("Lov - mazlíček získá 10 životů") { lp ->
                val current = lp.getCurrentHearts()
                lp.setHearts((current + 10).coerceAtMost(15))
            },
            // Coins: add/remove
            Event("Pracant - lvíček vydělal prací 5 mincí") { lp ->
                lp.addPoints(5)
            },
            Event("Dlužník - zviřátko půjčilo kamarádovi, ztrácíš 5 mincí") { lp ->
                lp.spendPoints(5)
            },
            Event("Narozeniny - Alex dostal dárky! +15 mincí") { lp ->
                lp.addPoints(15)
            },
            Event("Štěstí - našel jsi bonusový život! +1 život") { lp ->
                val current = lp.getCurrentHearts()
                lp.setHearts((current + 1).coerceAtMost(15))
            },
            Event("Ztracená peněženka - -10 mincí") { lp ->
                // Try to spend up to 10, but don't go below 0
                val current = lp.getTotalPoints()
                val toSpend = if (current >= 10) 10 else current
                if (toSpend > 0) lp.spendPoints(toSpend)
            },
            // Hunger: add/remove
            Event("Hostina - Alex je sytý, +20 hlad") { _ ->
                val hm = HungerManager(context)
                val cur = hm.getCurrentHunger()
                hm.setCurrentHunger((cur + 20).coerceAtMost(HungerManager.MAX_HUNGER))
            },
            Event("Dieta - Alex má menší hlad, -10 hlad") { _ ->
                val hm = HungerManager(context)
                val cur = hm.getCurrentHunger()
                hm.setCurrentHunger((cur - 10).coerceAtLeast(0))
            },
            Event("Piknik - společně jste se najedli, +10 hlad") { _ ->
                val hm = HungerManager(context)
                val cur = hm.getCurrentHunger()
                hm.setCurrentHunger((cur + 10).coerceAtMost(HungerManager.MAX_HUNGER))
            }
        )
    }

    fun maybeShowEvent(activity: Activity, afterHandled: (() -> Unit)? = null) {
        // Never show until tutorials are done at least once
        val tutorialsDone = TutorialManager.hasShown(activity, "tutorial_home") &&
                TutorialManager.hasShown(activity, "tutorial_welcome")
        if (!tutorialsDone) {
            if (!prefs.getBoolean(KEY_DISABLED_UNTIL_TUTORIALS, false)) {
                prefs.edit().putBoolean(KEY_DISABLED_UNTIL_TUTORIALS, true).apply()
                scheduleNextRandom(prefs)
            }
            afterHandled?.invoke()
            return
        }

        val now = System.currentTimeMillis()
        val nextAt = prefs.getLong(KEY_NEXT_AT, 0L)
        if (nextAt == 0L) {
            scheduleNextRandom(prefs)
            afterHandled?.invoke()
            return
        }
        if (now < nextAt) {
            afterHandled?.invoke()
            return
        }

        // Pick and apply an event
        val events = buildEvents()
        if (events.isEmpty()) {
            scheduleNextRandom(prefs)
            afterHandled?.invoke()
            return
        }
        val event = events[Random.nextInt(events.size)]
        applyAndShowOverlay(activity, event) {
            // Reschedule next event after user dismisses
            scheduleNextRandom(prefs)
            afterHandled?.invoke()
        }
    }

    private fun applyAndShowOverlay(activity: Activity, event: Event, onDismiss: () -> Unit) {
        val root = activity.findViewById<View>(android.R.id.content) as ViewGroup
        val overlay = LayoutInflater.from(activity).inflate(R.layout.view_event_overlay, root, false)
        overlay.isClickable = true
        overlay.isFocusable = true

        // Title and message
        val titleView = overlay.findViewById<TextView>(R.id.eventTitle)
        val messageView = overlay.findViewById<TextView>(R.id.eventMessage)
        val imageView = overlay.findViewById<ImageView>(R.id.eventImage)
        
        titleView?.text = "Událost!"
        messageView?.text = event.message
        try {
            val input = activity.assets.open("alex/AlexCool.png")
            val bmp = android.graphics.BitmapFactory.decodeStream(input)
            input.close()
            imageView?.setImageBitmap(bmp)
        } catch (_: Throwable) {
            imageView?.setImageResource(R.drawable.ic_alex)
        }

        // Apply immediately
        val progress = LessonProgress(activity)
        event.apply(progress)

        val confettiView = overlay.findViewById<ConfettiView>(R.id.confettiView)
        overlay.findViewById<MaterialButton>(R.id.eventOk)?.setOnClickListener {
            confettiView?.stop()
            root.removeView(overlay)
            onDismiss()
        }
        root.addView(overlay)
        
        // Start animations after view is laid out
        overlay.post {
            try {
                imageView?.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom))
                titleView?.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_scale))
                messageView?.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_scale))
                
                // Start confetti animation after a small delay to ensure view is measured
                // Wrap in try-catch to prevent crashes if confetti fails
                try {
                    confettiView?.postDelayed({
                        try {
                            if (confettiView?.isAttachedToWindow == true && confettiView?.visibility == View.VISIBLE) {
                                // Run confetti for ~2 seconds, then fade out and hide
                                confettiView?.visibility = View.VISIBLE
                                confettiView?.startFor(2000L, 300L)
                            }
                        } catch (e: Exception) {
                            // Silently fail confetti if there's an issue
                            e.printStackTrace()
                        }
                    }, 300)
                } catch (e: Exception) {
                    // If confetti view itself causes issues, just skip it
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** Shows a random event immediately, ignoring schedule and tutorial gating. For debugging. */
    fun showRandomNow(activity: Activity, afterHandled: (() -> Unit)? = null) {
        val events = buildEvents()
        if (events.isEmpty()) {
            afterHandled?.invoke()
            return
        }
        val event = events[Random.nextInt(events.size)]
        applyAndShowOverlay(activity, event) {
            // Also schedule the next random so normal flow continues
            scheduleNextRandom(prefs)
            afterHandled?.invoke()
        }
    }
}


