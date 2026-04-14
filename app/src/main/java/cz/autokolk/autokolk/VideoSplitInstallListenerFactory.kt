package cz.autokolk

import android.util.Log
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

/**
 * Play Core listener for on-demand video feature modules.
 * Keeps [MainActivity] smaller and documents the DFM state machine in one place.
 */
object VideoSplitInstallListenerFactory {

    fun create(
        logTag: String,
        videoToModuleMap: Map<String, String>,
        installedVideoModules: MutableSet<String>,
        getPendingVideoPath: () -> String?,
        setPendingVideoPath: (String?) -> Unit,
        onReloadVideo: (String) -> Unit,
        onInstallFailed: (moduleName: String?, errorCode: Int) -> Unit,
        onDownloadProgress: (moduleName: String?, percent: Int) -> Unit,
    ): SplitInstallStateUpdatedListener = SplitInstallStateUpdatedListener { state ->
        when (state.status()) {
            SplitInstallSessionStatus.INSTALLED -> {
                val moduleName = state.moduleNames().firstOrNull()
                if (moduleName != null) {
                    Log.d(logTag, "Video module '$moduleName' installed")
                    installedVideoModules.add(moduleName)
                    val pending = getPendingVideoPath()
                    if (pending != null) {
                        val videoFileName = pending.substringAfterLast("/")
                        val requiredModule = videoToModuleMap[videoFileName]
                        if (requiredModule == moduleName &&
                            installedVideoModules.contains(requiredModule)
                        ) {
                            setPendingVideoPath(null)
                            onReloadVideo(pending)
                        }
                    }
                }
            }
            SplitInstallSessionStatus.FAILED -> {
                val moduleName = state.moduleNames().firstOrNull()
                val code = state.errorCode()
                Log.e(logTag, "Video module '$moduleName' installation failed: $code")
                onInstallFailed(moduleName, code)
            }
            SplitInstallSessionStatus.DOWNLOADING -> {
                val moduleName = state.moduleNames().firstOrNull()
                val bytesDownloaded = state.bytesDownloaded()
                val totalBytes = state.totalBytesToDownload()
                val percent = if (totalBytes > 0) {
                    (bytesDownloaded * 100 / totalBytes).toInt()
                } else {
                    0
                }
                if (totalBytes > 0) {
                    Log.d(
                        logTag,
                        "Video module '$moduleName' downloading: $percent% ($bytesDownloaded/$totalBytes bytes)",
                    )
                }
                onDownloadProgress(moduleName, percent)
            }
            SplitInstallSessionStatus.PENDING -> {
                val moduleName = state.moduleNames().firstOrNull()
                Log.d(logTag, "Video module '$moduleName' installation pending")
            }
            else -> {
                val moduleName = state.moduleNames().firstOrNull()
                Log.d(logTag, "Video module '$moduleName' status: ${state.status()}")
            }
        }
    }
}
