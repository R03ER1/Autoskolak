package cz.autokolk

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.button.MaterialButton

object HeartsRewardAds {
    private const val AD_UNIT_ID = "ca-app-pub-7904041740523292/3817416182"
    private var rewardedAd: RewardedAd? = null
    private var isLoading: Boolean = false

    fun wireForHeartsSheet(
        activity: AppCompatActivity,
        sheetView: View,
        lessonProgress: LessonProgress,
        heartsTextView: TextView,
        plusButton: MaterialButton?,
        okButton: MaterialButton
    ) {
        val rewardContainer = sheetView.findViewById<View>(R.id.bottomSheetRewardContainer) ?: return
        val rewardButton = sheetView.findViewById<MaterialButton>(R.id.bottomSheetReward) ?: return
        val rewardLoading = sheetView.findViewById<View>(R.id.bottomSheetRewardLoading)

        rewardContainer.visibility = View.VISIBLE
        rewardLoading?.visibility = View.GONE
        rewardButton.visibility = View.VISIBLE

        rewardButton.setOnClickListener {
            rewardButton.visibility = View.GONE
            rewardLoading?.visibility = View.VISIBLE
            plusButton?.isEnabled = false
            okButton.isEnabled = false

            showRewardedAd(
                activity = activity,
                lessonProgress = lessonProgress,
                heartsTextView = heartsTextView,
                onFinished = {
                    rewardLoading?.visibility = View.GONE
                    rewardButton.visibility = View.VISIBLE
                    plusButton?.isEnabled = true
                    okButton.isEnabled = true
                }
            )
        }
    }

    private fun showRewardedAd(
        activity: AppCompatActivity,
        lessonProgress: LessonProgress,
        heartsTextView: TextView,
        onFinished: () -> Unit
    ) {
        fun updateHeartsText() {
            heartsTextView.text = lessonProgress.getCurrentHearts().toString()
        }

        val existingAd = rewardedAd
        if (existingAd != null) {
            existingAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    updateHeartsText()
                    onFinished()
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    rewardedAd = null
                    onFinished()
                }
            }
            existingAd.show(activity) { _: RewardItem ->
                val current = lessonProgress.getCurrentHearts()
                lessonProgress.setHearts(current + 1)
                updateHeartsText()
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
                    showRewardedAd(activity, lessonProgress, heartsTextView, onFinished)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoading = false
                    rewardedAd = null
                    onFinished()
                }
            }
        )
    }
}
