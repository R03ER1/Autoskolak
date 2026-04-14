package cz.autokolk

import android.content.res.AssetManager
import android.system.ErrnoException
import android.system.OsConstants
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.LinkedHashMap

/**
 * LRU-ish cache of extracted lesson videos under [baseDir].
 * Avoids rewriting the same large file to disk when the user navigates between questions (audit S1).
 */
class VideoAssetFileCache(private val baseDir: File) {

    /** Thrown when the device runs out of storage while copying a video asset (audit S2). */
    class NoSpaceOnDeviceException(cause: Throwable?) : IOException(cause)

    companion object {
        private const val MAX_CACHE_BYTES = 100L * 1024 * 1024
    }

    private val accessOrder = object : LinkedHashMap<String, File>(32, 0.75f, true) {}

    @Synchronized
    fun getOrCopyFromAssets(assets: AssetManager, videoPath: String): File? {
        accessOrder[videoPath]?.let { existing ->
            if (existing.exists() && existing.length() > 0L) {
                return existing
            }
            accessOrder.remove(videoPath)
        }
        baseDir.mkdirs()
        val safeName = videoPath.replace('/', '_').ifEmpty { "video.bin" }
        val outFile = File(baseDir, safeName)
        try {
            assets.open(videoPath).use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: FileNotFoundException) {
            outFile.delete()
            throw e
        } catch (e: IOException) {
            outFile.delete()
            if (isNoSpaceOnDevice(e)) throw NoSpaceOnDeviceException(e)
            return null
        } catch (e: Exception) {
            outFile.delete()
            if (isNoSpaceOnDevice(e)) throw NoSpaceOnDeviceException(e)
            return null
        }
        if (!outFile.exists() || outFile.length() <= 0L) {
            outFile.delete()
            return null
        }
        accessOrder[videoPath] = outFile
        trimToBudget()
        return outFile
    }

    private fun trimToBudget() {
        var total = accessOrder.values.sumOf { if (it.exists()) it.length() else 0L }
        val iterator = accessOrder.entries.iterator()
        while (total > MAX_CACHE_BYTES && iterator.hasNext()) {
            val (_, file) = iterator.next()
            val len = if (file.exists()) file.length() else 0L
            file.delete()
            iterator.remove()
            total -= len
        }
    }

    private fun isNoSpaceOnDevice(t: Throwable): Boolean {
        var x: Throwable? = t
        while (x != null) {
            if (x is ErrnoException && x.errno == OsConstants.ENOSPC) return true
            if (x is IOException && x.message?.contains("no space", ignoreCase = true) == true) {
                return true
            }
            x = x.cause
        }
        return false
    }
}
