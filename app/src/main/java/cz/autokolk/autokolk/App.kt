package cz.autokolk

import android.app.Application
import android.util.Log
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.google.android.gms.ads.MobileAds
import cz.autokolk.audio.SoundManager
import cz.autokolk.data.test.TestAttemptRepository
import cz.autokolk.work.WeeklySummaryWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okio.Path.Companion.toOkioPath

/**
 * Krok 158: centrální Coil `ImageLoader` (memory cache 25 % dostupné paměti, disk cache
 * 10 % volného místa v `cacheDir/image_cache`, crossfade). Aplikace dnes veškerá lokální
 * média (ikony lekcí, obrázky otázek) načítá z `assets/` přes [cz.autokolk.ui.components.media.AssetImageFromPath]
 * (vlastní async `BitmapFactory` loader, ne Coil — assety nejsou URL a Coil pro ně nepřináší
 * výhodu), takže tento singleton `ImageLoader` dnes není z UI aktivně využíván. Je ale
 * nastaven centrálně a hotový k použití, pokud v budoucnu přibude síťový/`content://` obsah
 * (např. importované avatary), aniž by bylo nutné znovu řešit cache konfiguraci.
 */
class App : Application(), SingletonImageLoader.Factory {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache").toOkioPath())
                    .maxSizePercent(0.1)
                    .build()
            }
            .crossfade(true)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        Log.i(
            "InterstitialAd",
            "[Ads] using ${if (BuildConfig.DEBUG) "TEST" else "PROD"} ad unit IDs",
        )
        SoundManager.init(this)
        val lp = LessonProgress(this)
        HeartRefillJobService.scheduleNext(this, lp)
        WeeklySummaryWorker.schedule(this)
        appScope.launch(Dispatchers.IO) {
            TestAttemptRepository.getInstance(this@App).migrateLegacyScoresIfNeeded()
        }
    }
}
