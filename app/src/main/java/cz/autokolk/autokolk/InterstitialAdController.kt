package cz.autokolk

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback

/**
 * Sjednocené rozhodovací místo pro interstitial reklamy po dokončení lekce.
 *
 * Pravidla (viz REDESIGN_PLAN kroky 164):
 *  - Po každé 3. dokončené lekci ukázat reklamu.
 *  - První 3 lekce jsou bez reklam (grace window).
 *  - Rozhodnutí je jednotné pro Compose flow ([cz.autokolk.ui.screens.results.ResultsComposeScreen])
 *    i legacy Activity vrstvu ([ResultsActivity]).
 *  - Předpokládá se, že reklama je předem předpřipravena přes [LessonInterstitialAds.preload].
 *    Když není nachystaná v okamžiku „show", reklamu přeskočíme, ale počítadlo stejně
 *    resetujeme (nebudujeme frontu reklam do budoucna).
 *  - Reklama za praktikované / náhodné / testové sezení se nepočítá — volající si to řídí
 *    tím, že v takových případech [onLessonCompleted] / [maybeShowInterstitial] nezavolá.
 */
object InterstitialAdController {
    private const val TAG = "InterstitialAd"
    private const val LOG_PREFIX = "[Ads]"

    private const val PREFS = "interstitial_ad_controller"
    private const val KEY_TOTAL = "total_lessons_completed"
    private const val KEY_SINCE_LAST = "completed_lessons_since_ad"

    /** Reklama každou N-tou dokončenou lekcí. */
    const val AD_FREQUENCY = 3

    /** Prvních N celkově dokončených lekcí je bez reklam (grace window). */
    const val GRACE_LESSONS = 3

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Idempotentní proxy nad [LessonInterstitialAds.preload] — jen přidává log breadcrumb. */
    fun preload(context: Context) {
        Log.d(TAG, "$LOG_PREFIX preload requested")
        LessonInterstitialAds.preload(context.applicationContext)
    }

    /**
     * Zavolej PŘESNĚ jednou po dokončení skutečné lekce (nikoli procvičování / náhodného
     * kvízu / testu). V Compose flow to dělá [cz.autokolk.ui.screens.quiz.QuizViewModel]
     * v `completeLesson`, v legacy [MainActivity] před spuštěním [ResultsActivity].
     */
    fun onLessonCompleted(context: Context) {
        val p = prefs(context)
        val total = p.getInt(KEY_TOTAL, 0) + 1
        val since = p.getInt(KEY_SINCE_LAST, 0) + 1
        p.edit().putInt(KEY_TOTAL, total).putInt(KEY_SINCE_LAST, since).apply()
        Log.d(TAG, "$LOG_PREFIX lesson completed: total=$total, sinceAd=$since")
    }

    /** Kolik lekcí uživatel celkově dokončil od instalace (pro grace window). */
    fun totalLessonsCompleted(context: Context): Int =
        prefs(context).getInt(KEY_TOTAL, 0)

    /** Kolik lekcí uživatel dokončil od poslední reklamy (nebo od instalace). */
    fun completedLessonsSinceAd(context: Context): Int =
        prefs(context).getInt(KEY_SINCE_LAST, 0)

    /**
     * Rozhoduje, jestli teď máme uživateli ukázat interstitial na základě počítadla.
     * Neresetuje počítadlo — to udělá až [maybeShowInterstitial] po předání reklamy.
     */
    fun shouldShowInterstitial(context: Context): Boolean {
        val total = totalLessonsCompleted(context)
        val since = completedLessonsSinceAd(context)
        return total >= GRACE_LESSONS && since >= AD_FREQUENCY
    }

    /** Vynulovat počítadlo „od poslední reklamy". [totalLessonsCompleted] zůstává. */
    fun resetSinceAd(context: Context) {
        prefs(context).edit().putInt(KEY_SINCE_LAST, 0).apply()
        Log.d(TAG, "$LOG_PREFIX counter reset (sinceAd -> 0)")
    }

    /**
     * Compose-friendly: rozhodne + případně ukáže předpřipravenou reklamu. Po skončení
     * (ať už dismiss, fail-to-show nebo skip) volá [onFinished]. Nikdy neblokuje UI —
     * když reklama nebyla nachystaná, [onFinished] proběhne synchronně.
     *
     * @param debugForce vynutí zobrazení bez ohledu na počítadlo (debug tlačítko).
     */
    fun maybeShowInterstitial(
        activity: Activity?,
        debugForce: Boolean = false,
        onFinished: () -> Unit,
    ) {
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            Log.d(TAG, "$LOG_PREFIX no valid activity host; skipping")
            onFinished()
            return
        }
        val decision = debugForce || shouldShowInterstitial(activity)
        if (!decision) {
            Log.d(
                TAG,
                "$LOG_PREFIX decision=skip (total=${totalLessonsCompleted(activity)}, " +
                    "sinceAd=${completedLessonsSinceAd(activity)})",
            )
            onFinished()
            return
        }
        val ready = LessonInterstitialAds.takeReadyAd()
        if (ready == null) {
            Log.d(TAG, "$LOG_PREFIX decision=show but ad not preloaded — skipping and preloading")
            resetSinceAd(activity)
            LessonInterstitialAds.preload(activity)
            onFinished()
            return
        }
        Log.d(TAG, "$LOG_PREFIX decision=show; presenting interstitial")
        ready.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "$LOG_PREFIX interstitial dismissed")
                resetSinceAd(activity)
                LessonInterstitialAds.preload(activity)
                onFinished()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(TAG, "$LOG_PREFIX interstitial failed to show: ${adError.message}")
                resetSinceAd(activity)
                LessonInterstitialAds.preload(activity)
                onFinished()
            }
        }
        ready.show(activity)
    }
}
