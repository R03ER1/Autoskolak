package cz.autokolk.ui.components.sheets

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.LoadAdError
import cz.autokolk.LessonProgress

object RewardedAdHelper {
    private const val AD_UNIT_ID = "ca-app-pub-7904041740523292/3817416182"
    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    /**
     * Shows a rewarded ad. On reward, adds one heart via [lessonProgress].
     * [onResult] is called with `true` on success, `false` on failure/cancel.
     */
    fun showForHeart(
        activity: Activity,
        lessonProgress: LessonProgress,
        onResult: (Boolean) -> Unit,
    ) {
        showAd(activity, onReward = {
            val current = lessonProgress.getCurrentHearts()
            lessonProgress.setHearts(current + 1)
        }, onResult = onResult)
    }

    /**
     * Shows a rewarded ad. On reward, protects the streak by recording
     * today as completed (even if user didn't finish a lesson).
     * [onResult] is called with `true` on success, `false` on failure/cancel.
     */
    fun showForStreakProtect(
        activity: Activity,
        lessonProgress: LessonProgress,
        onResult: (Boolean) -> Unit,
    ) {
        showAd(activity, onReward = {
            lessonProgress.setStreakForToday(
                lessonProgress.getCurrentStreak().coerceAtLeast(1),
            )
        }, onResult = onResult)
    }

    /**
     * Po shlédnutí reklamy aktivuje 30 minut 2× XP ([LessonProgress.activateDoubleXpForMinutes]).
     */
    fun showForDoubleXp(
        activity: Activity,
        lessonProgress: LessonProgress,
        onResult: (Boolean) -> Unit,
    ) {
        showAd(activity, onReward = {
            lessonProgress.activateDoubleXpForMinutes(30)
        }, onResult = onResult)
    }

    private fun showAd(
        activity: Activity,
        onReward: () -> Unit,
        onResult: (Boolean) -> Unit,
    ) {
        val existing = rewardedAd
        if (existing != null) {
            existing.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                }
                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    rewardedAd = null
                    onResult(false)
                }
            }
            existing.show(activity) {
                onReward()
                onResult(true)
            }
            return
        }

        if (isLoading) return

        isLoading = true
        RewardedAd.load(
            activity,
            AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    isLoading = false
                    rewardedAd = ad
                    showAd(activity, onReward, onResult)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoading = false
                    rewardedAd = null
                    onResult(false)
                }
            },
        )
    }
}
