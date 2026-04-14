package cz.autokolk.ui.screens.quiz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.components.media.AssetImageFromPath

@Composable
fun QuizMedia(
    imagePath: String?,
    videoPath: String?,
    modifier: Modifier = Modifier,
) {
    if (!imagePath.isNullOrBlank()) {
        AssetImageFromPath(
            assetPath = imagePath,
            contentDescription = null,
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Fit,
        )
        return
    }
    if (!videoPath.isNullOrBlank()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
        ) {
            // Placeholder — přehrávač z dynamického modulu lze doplnit později.
        }
    }
}
