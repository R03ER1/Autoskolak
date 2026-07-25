package cz.autokolk.ui.components.media

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cz.autokolk.ui.components.animation.shimmerLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Krok 158: interní stav asynchronního načtení asset obrázku. */
private sealed interface AssetImageState {
    data object Loading : AssetImageState
    data class Success(val bitmap: Bitmap) : AssetImageState
    data object Error : AssetImageState
}

/**
 * Krok 158: načtení obrázku z assets je nyní vždy asynchronní (mimo hlavní/composition
 * vlákno přes [Dispatchers.IO]), takže dekódování většího PNG/JPEG nikdy nezablokuje
 * recompose/kreslení. Během načítání se zobrazí shimmer placeholder (stejná komponenta jako
 * u ostatních skeleton loading efektů, [shimmerLoading]); při chybě dekódování (poškozený/
 * chybějící asset) se zobrazí error stav s tlačítkem na nový pokus.
 *
 * Aplikace nepoužívá síťové obrázky (Coil `ImageLoader` je nastaven centrálně v `App.kt`
 * pro budoucí použití, ale veškerá média — ikony lekcí, obrázky otázek — jsou balíčkovaná
 * v `assets/`), takže disk/memory cache navíc zde není potřeba: [BitmapFactory] dekóduje
 * přímo z APK assets, které jsou samy o sobě rychlé (mmap přes `AssetManager`).
 */
@Composable
fun AssetImageFromPath(
    assetPath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val context = LocalContext.current
    var retryToken by remember(assetPath) { mutableIntStateOf(0) }

    val state by produceState<AssetImageState>(AssetImageState.Loading, assetPath, retryToken) {
        value = AssetImageState.Loading
        value = withContext(Dispatchers.IO) {
            try {
                val bmp = context.assets.open(assetPath).use { BitmapFactory.decodeStream(it) }
                if (bmp != null) AssetImageState.Success(bmp) else AssetImageState.Error
            } catch (_: Exception) {
                AssetImageState.Error
            }
        }
    }

    when (val s = state) {
        is AssetImageState.Success -> {
            Image(
                bitmap = s.bitmap.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
            )
        }
        AssetImageState.Loading -> {
            Box(
                modifier = modifier
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        RoundedCornerShape(8.dp),
                    )
                    .shimmerLoading(),
            )
        }
        AssetImageState.Error -> {
            Box(
                modifier = modifier
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        RoundedCornerShape(8.dp),
                    )
                    .clickable(onClick = { retryToken++ }),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Zkusit znovu načíst obrázek",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
