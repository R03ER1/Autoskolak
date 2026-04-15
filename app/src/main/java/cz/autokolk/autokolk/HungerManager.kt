package cz.autokolk

import android.content.Context
import kotlin.math.roundToInt

class HungerManager(context: Context) {
    private val prefs = context.getSharedPreferences("hunger_prefs", Context.MODE_PRIVATE)
    private val context = context

    companion object {
        private const val KEY_HUNGER = "current_hunger"
        private const val KEY_LAST_UPDATE = "last_update"
        private const val KEY_FREEZE_UNTIL = "freeze_until"
        private const val KEY_INITIALIZED = "initialized"
        private const val KEY_REVIVE_TIMESTAMP = "revive_timestamp"
        const val MAX_HUNGER = 100
        private const val MILLIS_PER_HOUR = 60L * 60L * 1000L
        private const val REVIVE_GRACE_PERIOD_MS = 2000L // 2 seconds grace period after revive
    }

    init {
        if (!prefs.getBoolean(KEY_INITIALIZED, false)) {
            prefs.edit()
                .putInt(KEY_HUNGER, MAX_HUNGER)
                .putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
                .putBoolean(KEY_INITIALIZED, true)
                .apply()
        }
    }

    fun getCurrentHunger(): Int {
        applyDecayIfNeeded()
        return prefs.getInt(KEY_HUNGER, MAX_HUNGER)
    }

    fun setCurrentHunger(hunger: Int) {
        val oldHunger = prefs.getInt(KEY_HUNGER, MAX_HUNGER)
        val clamped = hunger.coerceIn(0, MAX_HUNGER)
        val now = System.currentTimeMillis()
        // When hunger is explicitly changed (e.g., feeding or revival), also reset the
        // decay baseline to "now" so we don't instantly re‑apply a long backlog of decay.
        // This prevents situations where reviving Alex immediately makes him die again
        // because the last_update timestamp was far in the past.
        // Use commit() instead of apply() to ensure synchronous write, especially important
        // when reviving from death screen to prevent race conditions.
        val editor = prefs.edit()
            .putInt(KEY_HUNGER, clamped)
            .putLong(KEY_LAST_UPDATE, now)
        
        // If reviving from 0 (or very low) to a higher value, set revive timestamp
        if (oldHunger <= 0 && clamped > 0) {
            editor.putLong(KEY_REVIVE_TIMESTAMP, now)
        }
        
        editor.commit()
            
        // Schedule hunger notification if hunger changed
        if (clamped != oldHunger) {
            // If hunger increased, reset last-notified boundary so future drops notify correctly
            if (clamped > oldHunger) {
                HungerNotificationService.resetTierFlags(context)
            }
            scheduleHungerNotification()
        }
    }

    private fun applyDecayIfNeeded() {
        val last = prefs.getLong(KEY_LAST_UPDATE, System.currentTimeMillis())
        val now = System.currentTimeMillis()
        if (now <= last) return

        // If hunger decay is frozen until a future time, shift baseline forward and skip decay
        val freezeUntil = prefs.getLong(KEY_FREEZE_UNTIL, 0L)
        if (freezeUntil > now) {
            // Do not change hunger; just move last_update forward to now to avoid accumulating decay
            prefs.edit()
                .putLong(KEY_LAST_UPDATE, now)
                .apply()
            return
        } else if (freezeUntil != 0L && freezeUntil <= now) {
            // Freeze expired; clear the flag and continue with normal decay using recorded last
            prefs.edit().remove(KEY_FREEZE_UNTIL).apply()
        }

        var hunger = prefs.getInt(KEY_HUNGER, MAX_HUNGER)
        if (hunger <= 0) {
            // Nothing to decay; just advance baseline to now
            prefs.edit().putLong(KEY_LAST_UPDATE, now).apply()
            return
        }

        var cursor = last
        var changed = false

        // Remove 1 point after each variable interval based on current hourly rate.
        // Interval (ms) = max(15min, 60min / rate) to ensure at least 15-minute granularity.
        while (cursor < now && hunger > 0) {
            val ratePerHour = getHourlyDecayRateFor(hunger.toDouble())
            val intervalMillis = if (ratePerHour <= 0.0) Long.MAX_VALUE else {
                val minutesPerPoint = 60.0 / ratePerHour
                val millis = (minutesPerPoint * 60_000.0).toLong()
                // enforce minimum 15 minutes per tick
                kotlin.math.max(15L * 60L * 1000L, millis)
            }

            val nextTick = if (intervalMillis == Long.MAX_VALUE) Long.MAX_VALUE else cursor + intervalMillis
            if (nextTick > now) break

            // Apply one-point decay at this tick
            hunger -= 1
            if (hunger < 0) hunger = 0
            cursor = nextTick
            changed = true
        }

        // Persist updates
        if (changed) {
            val oldHunger = prefs.getInt(KEY_HUNGER, MAX_HUNGER)
            prefs.edit()
                .putInt(KEY_HUNGER, hunger)
                .putLong(KEY_LAST_UPDATE, cursor)
                .apply()

            if (hunger != oldHunger) {
                if (hunger > oldHunger) {
                    HungerNotificationService.resetTierFlags(context)
                }
                scheduleHungerNotification()
            }
        } else {
            // No tick crossed; just move baseline forward to now to avoid accumulating tiny drift
            prefs.edit().putLong(KEY_LAST_UPDATE, now).apply()
        }
    }

    /** Returns how many hunger points are lost per hour for the current hunger. */
    fun getHourlyDecayRate(): Double {
        val current = prefs.getInt(KEY_HUNGER, MAX_HUNGER).toDouble()
        return getHourlyDecayRateFor(current)
    }

    /** Returns millis until the next 1-point hunger decay tick. Returns 0 if none scheduled. */
    fun millisUntilNextPoint(): Long {
        // Ensure state is up to date
        applyDecayIfNeeded()
        val now = System.currentTimeMillis()

        // If frozen, next change is when freeze ends
        val freezeUntil = prefs.getLong(KEY_FREEZE_UNTIL, 0L)
        if (freezeUntil > now) return freezeUntil - now

        val hunger = prefs.getInt(KEY_HUNGER, MAX_HUNGER)
        if (hunger <= 0) return 0L

        val ratePerHour = getHourlyDecayRateFor(hunger.toDouble())
        if (ratePerHour <= 0.0) return 0L

        val last = prefs.getLong(KEY_LAST_UPDATE, now)
        val minutesPerPoint = 60.0 / ratePerHour
        val intervalMillis = kotlin.math.max(15L * 60L * 1000L, (minutesPerPoint * 60_000.0).toLong())

        val nextTick = last + intervalMillis
        return if (nextTick > now) nextTick - now else 0L
    }

    /**
     * Returns millis until hunger crosses the next lower 10% boundary (e.g., from 93 -> 90).
     * If already on an exact boundary, returns time until crossing to the next one below (e.g., 90 -> 80).
     * Returns 0 if no further decay is scheduled.
     */
    fun millisUntilNextTenPercentBoundary(): Long {
        applyDecayIfNeeded()
        val now = System.currentTimeMillis()

        // If frozen, boundary occurs when freeze ends (state does not change until then)
        val freezeUntil = prefs.getLong(KEY_FREEZE_UNTIL, 0L)
        if (freezeUntil > now) return freezeUntil - now

        var hunger = prefs.getInt(KEY_HUNGER, MAX_HUNGER)
        if (hunger <= 0) return 0L

        // Determine target boundary strictly below current displayed bucket
        val currentBucket = (hunger / 10) * 10
        var target = currentBucket - 10
        if (hunger % 10 == 0) {
            // If exactly on boundary, the next boundary is one bucket lower
            target = hunger - 10
        }
        if (target < 0) target = 0

        // Simulate forward from last_update using current decay model until we reach the target bucket
        var cursor = prefs.getLong(KEY_LAST_UPDATE, now)
        if (cursor < now) cursor = now // start from now for forward scheduling

        while (hunger > target) {
            val ratePerHour = getHourlyDecayRateFor(hunger.toDouble())
            if (ratePerHour <= 0.0) return 0L
            val minutesPerPoint = 60.0 / ratePerHour
            val intervalMillis = kotlin.math.max(15L * 60L * 1000L, (minutesPerPoint * 60_000.0).toLong())
            cursor += intervalMillis
            hunger -= 1
            if (hunger <= 0) break
        }

        val delay = cursor - now
        return if (delay > 0L) delay else 0L
    }

    /**
     * Millis until [getCurrentHunger] first becomes at or below the next notification band edge
     * (50 → 20 → 5 → 0). Used to schedule [HungerNotificationService].
     */
    fun millisUntilNextNotificationBandEdge(): Long {
        applyDecayIfNeeded()
        val now = System.currentTimeMillis()

        val freezeUntil = prefs.getLong(KEY_FREEZE_UNTIL, 0L)
        if (freezeUntil > now) return freezeUntil - now

        var hunger = prefs.getInt(KEY_HUNGER, MAX_HUNGER)
        if (hunger <= 0) return 0L

        val nextStop = when {
            hunger > 50 -> 50
            hunger > 20 -> 20
            hunger > 5 -> 5
            else -> 0
        }

        var cursor = prefs.getLong(KEY_LAST_UPDATE, now)
        if (cursor < now) cursor = now

        while (hunger > nextStop) {
            val ratePerHour = getHourlyDecayRateFor(hunger.toDouble())
            if (ratePerHour <= 0.0) return 0L
            val minutesPerPoint = 60.0 / ratePerHour
            val intervalMillis = kotlin.math.max(15L * 60L * 1000L, (minutesPerPoint * 60_000.0).toLong())
            cursor += intervalMillis
            hunger -= 1
            if (hunger < 0) hunger = 0
        }

        val delay = cursor - now
        return if (delay > 0L) delay else 0L
    }

    /** Rate function: 0.5 pts/hr when hunger < 20; ramps up smoothly to 4 pts/hr at > 90. */
    private fun getHourlyDecayRateFor(currentHunger: Double): Double {
        val h = currentHunger.coerceIn(0.0, MAX_HUNGER.toDouble())
        return when {
            h < 20.0 -> 0.5 // 1 point per 2 hours
            h <= 90.0 -> {
                // Linear ramp from 0.5 at 20 to 3.5 at 90
                val t = (h - 20.0) / (90.0 - 20.0)
                0.5 + t * 3.0
            }
            else -> {
                // Emphasize increase above 90 with quadratic ease to reach 4.0 at 100
                val t = ((h - 90.0) / 10.0).coerceIn(0.0, 1.0)
                3.5 + (t * t) * 0.5
            }
        }
    }

    /** Returns epoch millis until which hunger is frozen (0 if not set). */
    fun getFreezeUntilEpochMillis(): Long {
        return prefs.getLong(KEY_FREEZE_UNTIL, 0L)
    }

    /** Returns true if hunger decay is currently frozen ("kamení" active). */
    fun isFrozenNow(): Boolean {
        val now = System.currentTimeMillis()
        val until = getFreezeUntilEpochMillis()
        return until > now
    }

    /**
     * Prevents hunger decay until the specified epoch millis.
     * While frozen, calls to getCurrentHunger() will keep hunger unchanged and advance last_update to now.
     */
    fun freezeDecayUntil(epochMillis: Long) {
        prefs.edit()
            .putLong(KEY_FREEZE_UNTIL, epochMillis)
            .apply()
    }

    /** Freezes hunger decay for the given number of hours from now. */
    fun freezeDecayForHours(hours: Int) {
        if (hours <= 0) return
        val until = System.currentTimeMillis() + hours.toLong() * 60L * 60L * 1000L
        freezeDecayUntil(until)
    }
    
    /** Schedules a hunger notification check. */
    private fun scheduleHungerNotification() {
        HungerNotificationService.scheduleNext(context, this)
    }
}


