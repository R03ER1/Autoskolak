package cz.autokolk.ui.screens.gamification

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.HapticFeedbackConstantsCompat
import cz.autokolk.BONUS_WHEEL_SEGMENTS
import cz.autokolk.BonusWheelRewardType
import cz.autokolk.BonusWheelSegmentSpec
import cz.autokolk.LessonProgress
import cz.autokolk.audio.SoundManager
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.components.glass.GlassCard
import cz.autokolk.ui.theme.AccentBlue
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentGradientEnd
import cz.autokolk.ui.theme.AccentGradientStart
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.DarkSurface
import cz.autokolk.ui.theme.ErrorRed
import cz.autokolk.ui.theme.GlassTone
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.WarningAmber
import cz.autokolk.ui.util.rememberHaptic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Dialog bonusového kola (2.2.10 redesign) — glassmorphism styl přes [GlassCard] (stejný
 * pattern jako [cz.autokolk.ui.screens.quiz.QuizNoLivesOverlay]), harmonická paleta z theme
 * a segmenty s viditelnými odměnami (mince, extra kola, dočasné neomezené životy, truhla)
 * ještě před roztočením. Otevírá se jak z obchodu ([CoinShopScreen]/nastavení), tak z nového
 * rychlého tlačítka na Home obrazovce ([cz.autokolk.ui.components.buttons.BonusWheelFab]) —
 * v obou případech jde o STEJNOU instanci komponenty, žádná duplicitní logika.
 */
@Composable
fun BonusWheelDialog(
    lessonProgress: LessonProgress,
    onDismiss: () -> Unit,
) {
    val view = LocalView.current
    val haptic = rememberHaptic()
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var remaining by remember { mutableIntStateOf(lessonProgress.getBonusWheelRollsRemainingToday()) }

    LaunchedEffect(Unit) {
        remaining = lessonProgress.getBonusWheelRollsRemainingToday()
    }

    Dialog(onDismissRequest = { if (!isSpinning) onDismiss() }) {
        Box(
            // Krok 2.2.11: samotný "fake glass" fill (GlassTone.Dark) je jen ~5% bílá —
            // nad libovolným obsahem obrazovky (bez vlastního rozostření za sebou) to bylo
            // moc průsvitné a prosvítal skrz obsah pod dialogem (např. karty "Denní výzvy").
            // Řešeno lokálně JEN pro tento dialog přidáním téměř neprůhledné tmavé podkladové
            // vrstvy pod stávající GlassCard — sdílený GlassTokens/glassPalette pro GlassTone.Dark
            // zůstává beze změny, takže ostatní místa (QuizNoLivesOverlay, EventOverlay,
            // TutorialOverlay) vypadají a chovají se úplně stejně jako dřív.
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(DarkSurface.copy(alpha = 0.9f)),
        ) {
            GlassCard(
                // Dialog nemá barevné/rozostřené pozadí za sebou (jen systémový scrim) — pevný
                // tmavý tón zajišťuje čitelnost v obou režimech, stejně jako u QuizNoLivesOverlay.
                tone = GlassTone.Dark,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AccentGradientStart, AccentGradientEnd))),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("🎡", style = MaterialTheme.typography.headlineSmall)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Bonusové kolo",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Dnes zbývá $remaining točení",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "▼",
                        color = WarningAmber,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(Modifier.height(4.dp))
                    WheelGraphic(sizeDp = 220.dp, rotationDegrees = rotation.value)
                    Spacer(Modifier.height(16.dp))
                    errorMsg?.let {
                        Text(
                            it,
                            color = AccentCyan,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    resultText?.let { text ->
                        Text(
                            text,
                            style = MaterialTheme.typography.headlineSmall,
                            color = WarningAmber,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    when {
                        resultText != null || (errorMsg != null && !isSpinning) -> {
                            PrimaryGradientButton(
                                text = "Zavřít",
                                onClick = onDismiss,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        else -> {
                            PrimaryGradientButton(
                                text = if (isSpinning) "Točí se…" else "Zatočit!",
                                onClick = {
                                    if (isSpinning) return@PrimaryGradientButton
                                    val result = lessonProgress.rollBonusWheel()
                                    if (result == null) {
                                        errorMsg = "Dnes už nemáš žádné točení."
                                        return@PrimaryGradientButton
                                    }
                                    errorMsg = null
                                    isSpinning = true
                                    scope.launch {
                                        val n = BONUS_WHEEL_SEGMENTS.size
                                        val sweep = 360f / n
                                        val segmentCenter = result.segmentIndex * sweep - 90f + sweep / 2f
                                        // Pointer je nahoře (úhel -90°) — dopočítá se rotace tak, aby na
                                        // něm po zastavení skutečně ležel vylosovaný segment.
                                        val desiredMod = (((-90f - segmentCenter) % 360f) + 360f) % 360f
                                        val currentMod = ((rotation.value % 360f) + 360f) % 360f
                                        val deltaToTarget = ((desiredMod - currentMod) + 360f) % 360f
                                        val target = rotation.value + 360f * 5 + deltaToTarget
                                        val animJob = launch {
                                            rotation.animateTo(
                                                target,
                                                animationSpec = tween(2800, easing = FastOutSlowInEasing),
                                            )
                                        }
                                        // Tikot podle segmentů — zpomalování s časem imituje realistické kolo.
                                        launch {
                                            var interval = 60L
                                            while (animJob.isActive) {
                                                SoundManager.play(SoundManager.Sound.WHEEL_TICK, volume = 0.7f)
                                                delay(interval)
                                                interval = (interval + 15L).coerceAtMost(260L)
                                            }
                                        }
                                        animJob.join()
                                        isSpinning = false
                                        remaining = lessonProgress.getBonusWheelRollsRemainingToday()
                                        resultText = when (result.type) {
                                            BonusWheelRewardType.COINS -> "+${result.coins} mincí"
                                            BonusWheelRewardType.CHEST -> "🎁 Truhla: +${result.coins} mincí!"
                                            BonusWheelRewardType.EXTRA_SPINS -> "🔄 +${result.extraSpins} kola navíc!"
                                            BonusWheelRewardType.UNLIMITED_LIVES ->
                                                "❤️ Neomezené životy na ${result.unlimitedLivesMinutes} min!"
                                        }
                                        haptic.onAchievement()
                                        try {
                                            view.performHapticFeedback(HapticFeedbackConstantsCompat.CONTEXT_CLICK)
                                        } catch (_: Throwable) {
                                        }
                                        SoundManager.play(SoundManager.Sound.WHEEL_WIN)
                                        SoundManager.play(SoundManager.Sound.COIN, volume = 0.7f)
                                    }
                                },
                                // Krok 2.2.11: appka nemá pro PrimaryGradientButton žádný jiný "disabled"
                                // vizuál než tento — enabled=false teď navíc přepne na šedý/ztlumený
                                // vzhled (viz PrimaryGradientButton.kt), takže "Zatočit!" už je jasně
                                // vidět jako nedostupné při 0 zbývajících točeních, ne jen neklikatelné.
                                enabled = !isSpinning && remaining > 0,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Barva segmentu z harmonické theme palety — [BonusWheelRewardType.COINS] odstupňováno podle výše výhry. */
private fun segmentColor(segment: BonusWheelSegmentSpec): Color = when (segment.type) {
    BonusWheelRewardType.EXTRA_SPINS -> AccentBlue
    BonusWheelRewardType.UNLIMITED_LIVES -> ErrorRed.copy(alpha = 0.88f)
    BonusWheelRewardType.CHEST -> WarningAmber
    BonusWheelRewardType.COINS -> when {
        segment.coins >= 80 -> AccentTeal
        segment.coins >= 50 -> AccentTeal.copy(alpha = 0.85f)
        segment.coins >= 30 -> AccentCyan
        else -> AccentCyan.copy(alpha = 0.75f)
    }
}

/** Krátký popisek + ikona vidět přímo na segmentu — čitelné ještě PŘED roztočením. */
private fun segmentLabel(segment: BonusWheelSegmentSpec): String = when (segment.type) {
    BonusWheelRewardType.COINS -> "🪙\n+${segment.coins}"
    BonusWheelRewardType.EXTRA_SPINS -> "🔄\n+${segment.extraSpins}"
    BonusWheelRewardType.UNLIMITED_LIVES -> "❤️∞\n${segment.unlimitedLivesMinutes}m"
    BonusWheelRewardType.CHEST -> "🎁\n+${segment.coins}"
}

/**
 * Vykreslení kola — sdílené pro velkou verzi v dialogu i (v menším měřítku a bez popisků)
 * pro Home FAB ikonu ([cz.autokolk.ui.components.buttons.BonusWheelFab]).
 */
@Composable
fun WheelGraphic(
    sizeDp: Dp,
    rotationDegrees: Float,
    showLabels: Boolean = true,
) {
    val segments = BONUS_WHEEL_SEGMENTS
    val n = segments.size
    val textMeasurer = if (showLabels) rememberTextMeasurer() else null
    Box(
        Modifier
            .size(sizeDp)
            .graphicsLayer { rotationZ = rotationDegrees }
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color(0xFF12162A), Color(0xFF1A1F36)))),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val sweep = 360f / n
            val radius = size.minDimension / 2f
            for (i in 0 until n) {
                val segment = segments[i]
                val startAngle = i * sweep - 90f
                val color = segmentColor(segment)
                drawArc(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.95f), color.copy(alpha = 0.55f)),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = radius,
                    ),
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                )
                // Jemný oddělovač mezi segmenty — na tmavém pozadí čitelnější "krájení" kola
                // než ostré barevné hrany bez přechodu (dřívější "křiklavý" dojem).
                drawArc(
                    color = Color.Black.copy(alpha = 0.25f),
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                    style = Stroke(width = 2f),
                )
                if (showLabels && textMeasurer != null && sizeDp.value >= 120f) {
                    val label = segmentLabel(segment)
                    val measured = textMeasurer.measure(
                        text = label,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                        ),
                    )
                    val midAngle = startAngle + sweep / 2f
                    val labelRadius = radius * 0.62f
                    rotate(degrees = midAngle, pivot = Offset(size.width / 2f, size.height / 2f)) {
                        drawText(
                            textLayoutResult = measured,
                            topLeft = Offset(
                                size.width / 2f + labelRadius - measured.size.width / 2f,
                                size.height / 2f - measured.size.height / 2f,
                            ),
                        )
                    }
                }
            }
        }
        Box(
            Modifier
                .align(Alignment.Center)
                .size(sizeDp * (48f / 220f))
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF1A1F36), AccentCyan.copy(alpha = 0.35f)))),
        )
    }
}