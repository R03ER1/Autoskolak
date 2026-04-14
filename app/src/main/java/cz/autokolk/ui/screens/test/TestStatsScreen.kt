package cz.autokolk.ui.screens.test

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.LessonProgress
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

@Composable
fun TestStatsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scores = remember { LessonProgress(context).getAllTestScores(50) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Statistiky testů", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
        Text(
            "Počet dokončených pokusů: ${scores.size}",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(8.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
        ) {
            if (scores.size < 2) return@Canvas
            val max = 50f
            val min = 0f
            val w = size.width
            val h = size.height
            val pad = 12.dp.toPx()
            val stepX = (w - pad * 2) / (scores.size - 1).coerceAtLeast(1)
            var last: Offset? = null
            scores.forEachIndexed { i, s ->
                val x = pad + i * stepX
                val y = h - pad - (s - min) / (max - min) * (h - pad * 2)
                val pt = Offset(x, y)
                last?.let { prev ->
                    drawLine(AccentCyan, prev, pt, strokeWidth = 3f)
                }
                drawCircle(Color.White, 5f, pt)
                last = pt
            }
        }
        scores.takeLast(12).reversed().forEachIndexed { idx, s ->
            Text(
                "#${scores.size - idx}: $s / 50",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
