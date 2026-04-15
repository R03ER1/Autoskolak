package cz.autokolk

import android.app.Application
import com.google.android.gms.ads.MobileAds
import cz.autokolk.audio.SoundManager
import cz.autokolk.data.test.TestAttemptRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class App : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        SoundManager.init(this)
        val lp = LessonProgress(this)
        HeartRefillJobService.scheduleNext(this, lp)
        appScope.launch(Dispatchers.IO) {
            TestAttemptRepository.getInstance(this@App).migrateLegacyScoresIfNeeded()
        }
    }
}
