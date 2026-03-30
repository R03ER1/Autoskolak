package cz.autokolk

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest

class App : Application() {

    private lateinit var imageSplitInstallManager: SplitInstallManager
    private val imageModuleName = "imageassets"

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        val lp = LessonProgress(this)
        HeartRefillJobService.scheduleNext(this, lp)

        // Request install of imageassets dynamic feature so image assets are available
        imageSplitInstallManager = SplitInstallManagerFactory.create(this)
        requestImageModuleIfNeeded()
    }

    /**
     * Ensure that the dynamic feature module containing image assets is installed.
     * Images live in `imageassets/src/main/assets/images` and `imageassets/src/main/assets/alex`,
     * but once the module is installed they are accessible via this activity's AssetManager
     * as `images/...` and `alex/...`.
     */
    private fun requestImageModuleIfNeeded() {
        // If the module is already installed, nothing to do
        if (::imageSplitInstallManager.isInitialized &&
            imageSplitInstallManager.installedModules.contains(imageModuleName)
        ) {
            Log.d("App", "Image module already installed")
            return
        }

        try {
            val manager = if (::imageSplitInstallManager.isInitialized) {
                imageSplitInstallManager
            } else {
                SplitInstallManagerFactory.create(this).also { imageSplitInstallManager = it }
            }

            if (manager.installedModules.contains(imageModuleName)) {
                Log.d("App", "Image module already installed (post-init)")
                return
            }

            val request = SplitInstallRequest.newBuilder()
                .addModule(imageModuleName)
                .build()

            Log.d("App", "Requesting install of dynamic feature module: $imageModuleName")
            manager.startInstall(request)
                .addOnSuccessListener {
                    Log.d("App", "Dynamic feature module '$imageModuleName' install started (sessionId=$it)")
                }
                .addOnFailureListener { exception ->
                    Log.e("App", "Failed to request install for module '$imageModuleName'", exception)
                }
        } catch (e: Exception) {
            Log.e("App", "Error while requesting image module installation", e)
        }
    }
}


