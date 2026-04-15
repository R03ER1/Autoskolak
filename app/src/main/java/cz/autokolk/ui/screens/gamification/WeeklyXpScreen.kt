package cz.autokolk.ui.screens.gamification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.autokolk.LessonProgress
import cz.autokolk.XpSystem
import cz.autokolk.ui.components.animation.AnimatedBackground
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.WarningAmber

@Composable
fun WeeklyXpScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lp = remember { LessonProgress(context) }
    val last7 = remember { lp.getXpLast7Days() }
    val sum7 = last7.sum()
    val prev7 = remember { lp.getXpPrevious7DaysSum() }
    val best = remember { lp.getWeeklyXpPersonalBest() }
    val delta = sum7 - prev7
    val maxBar = (last7.maxOrNull() ?: 1).coerceAtLeast(1)

    AnimatedBackground(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Zpět",
                        tint = TextPrimary,
                    )
                }
                Text(
                    text = "Týdenní XP",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                )
            }
            Spacer(Modifier.height(16.dp))
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Posledních 7 dní", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Součet: $sum7 XP",
                        style = MaterialTheme.typography.headlineSmall,
                        color = WarningAmber,
                    )
                    Text(
                        text = when {
                            delta > 0 -> "O $delta XP více než předchozí týden"
                            delta < 0 -> "O ${-delta} XP méně než předchozí týden"
                            else -> "Stejně jako předchozí týden"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Osobní rekord (7d): $best XP", style = MaterialTheme.typography.bodySmall, color = AccentCyan)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Denní přehled", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                val labels = listOf("-6", "-5", "-4", "-3", "-2", "-1", "dnes")
                last7.forEachIndexed { i, v ->
                    val frac = (v.toFloat() / maxBar.toFloat()).coerceIn(0.05f, 1f)
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(36.dp)) {
                        Box(
                            Modifier
                                .height(120.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            Box(
                                Modifier
                                    .height((120 * frac).dp)
                                    .fillMaxWidth(0.75f)
                                    .background(AccentCyan.copy(alpha = 0.65f), RoundedCornerShape(6.dp)),
                            )
                        }
                        Text(labels.getOrElse(i) { "" }, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text("$v", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            val totalXp = lp.getTotalXp()
            val lv = XpSystem.levelForTotalXp(totalXp)
            Text(
                "Celkové XP: $totalXp · ${lv.title} (lvl ${lv.level})",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }
    }
}
