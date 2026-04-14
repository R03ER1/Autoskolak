package cz.autokolk

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/** Single dead Alex asset; primary path matches `imageassets` feature module. */
object AlexDeadBitmapLoader {
    private val PATHS = listOf(
        "images/alex/AlexDead.png",
        "alex/AlexDead.png",
    )

    fun load(assets: AssetManager): Bitmap? {
        for (path in PATHS) {
            try {
                assets.open(path).use { return BitmapFactory.decodeStream(it) }
            } catch (_: Throwable) { }
        }
        return null
    }
}
