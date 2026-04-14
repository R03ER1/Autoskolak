package cz.autokolk

import android.app.Application
import com.google.android.gms.ads.MobileAds
import cz.autokolk.audio.SoundManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        SoundManager.init(this)
        val lp = LessonProgress(this)
        HeartRefillJobService.scheduleNext(this, lp)
    }
}
