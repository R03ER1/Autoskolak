package cz.autokolk

import android.app.Application
import com.google.android.gms.ads.MobileAds

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        val lp = LessonProgress(this)
        HeartRefillJobService.scheduleNext(this, lp)
        // imageassets DFM is requested from LoadingActivity only (avoids duplicate SplitInstall).
    }
}
