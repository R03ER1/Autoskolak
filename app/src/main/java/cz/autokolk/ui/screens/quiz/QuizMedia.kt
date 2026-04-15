package cz.autokolk.ui.screens.quiz

import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cz.autokolk.VideoAssetFileCache
import cz.autokolk.ui.components.media.AssetImageFromPath
import cz.autokolk.ui.theme.AutokolkShapes
import cz.autokolk.ui.theme.AutokolkTokens
import cz.autokolk.ui.theme.GlassFill
import java.io.File

@Composable
fun QuizMedia(
    imagePath: String?,
    videoPath: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val shape = AutokolkShapes.medium
    val cache = remember {
        VideoAssetFileCache(File(context.cacheDir, "quiz_video_cache"))
    }
    if (!imagePath.isNullOrBlank()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .shadow(AutokolkTokens.ElevationLow, shape)
                .clip(shape)
                .background(GlassFill.copy(alpha = 0.2f)),
        ) {
            AssetImageFromPath(
                assetPath = imagePath,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Fit,
            )
        }
        return
    }
    if (!videoPath.isNullOrBlank()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .shadow(AutokolkTokens.ElevationLow, shape)
                .clip(shape)
                .background(GlassFill.copy(alpha = 0.25f)),
        ) {
            key(videoPath) {
                val path = videoPath!!
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            setOnPreparedListener { mp -> mp.isLooping = true }
                            try {
                                val file = cache.getOrCopyFromAssets(ctx.assets, path)
                                if (file != null && file.exists()) {
                                    setVideoPath(file.absolutePath)
                                    start()
                                }
                            } catch (_: Throwable) {
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                )
            }
        }
        return
    }
}
