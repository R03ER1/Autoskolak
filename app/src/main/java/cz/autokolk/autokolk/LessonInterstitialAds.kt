package cz.autokolk

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Holds one preloaded lesson interstitial so Results can show it without a long wait.
 * Preload from čtecí lekce (Compose overlay) / [MainActivity] while the user is in a session
 * that ends with this ad.
 */
object LessonInterstitialAds {
    private const val TAG = "InterstitialAd"
    private const val LOG_PREFIX = "[Ads]"
    // Debug buildy dostávají Google test ID, release produkční ID — viz app/build.gradle.kts.
    internal val AD_UNIT_ID: String = BuildConfig.ADMOB_INTERSTITIAL_ID

    private val lock = Any()
    @Volatile
    private var cached: InterstitialAd? = null

    @Volatile
    private var loading = false

    fun preload(context: Context) {
        synchronized(lock) {
            if (cached != null || loading) return
            loading = true
        }
        Log.d(TAG, "$LOG_PREFIX preload started")
        val app = context.applicationContext
        InterstitialAd.load(
            app,
            AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    synchronized(lock) {
                        cached = ad
                        loading = false
                    }
                    Log.d(TAG, "$LOG_PREFIX preload succeeded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(TAG, "$LOG_PREFIX preload failed: ${error.message}")
                    synchronized(lock) {
                        loading = false
                    }
                }
            }
        )
    }

    fun takeReadyAd(): InterstitialAd? {
        synchronized(lock) {
            val ad = cached
            cached = null
            return ad
        }
    }
}
