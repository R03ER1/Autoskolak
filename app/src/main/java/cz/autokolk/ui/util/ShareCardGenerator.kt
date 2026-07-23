package cz.autokolk.ui.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.compose.ui.graphics.toArgb
import cz.autokolk.R
import cz.autokolk.LessonProgress
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.DarkBackground
import cz.autokolk.ui.theme.DarkSurface
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary
import cz.autokolk.ui.theme.WarningAmber
import java.io.File
import java.io.FileOutputStream

/**
 * Generuje vizuální PNG "kartu" se streakem a týdenním souhrnem a nasdílí ji jako obrázek
 * (krok 143 z REDESIGN_PLAN.md). Kreslí se klasickým `android.graphics.Canvas` + `Paint`
 * (ne Compose `graphicsLayer`), aby to bylo spolehlivé i na nižších API úrovních (minSdk 24).
 */
object ShareCardGenerator {

    private const val TAG = "ShareCardGenerator"
    private const val CACHE_SUBDIR = "share_cards"
    private const val FILE_NAME = "share_card.png"
    private const val CARD_WIDTH = 1080
    private const val CARD_HEIGHT = 1350

    /** Data zobrazená na kartě — statistiky za posledních 7 dní + celkový streak. */
    data class CardStats(
        val streak: Int,
        val xpThisWeek: Int,
        val lessonsThisWeek: Int,
        val activeDaysThisWeek: Int,
        val totalXp: Int,
    )

    fun fromLessonProgress(lessonProgress: LessonProgress): CardStats {
        return CardStats(
            streak = lessonProgress.getCurrentStreak(),
            xpThisWeek = lessonProgress.getXpLast7Days().sum(),
            lessonsThisWeek = lessonProgress.getLessonsCompletedLast7Days(),
            activeDaysThisWeek = lessonProgress.getActiveDaysLast7(),
            totalXp = lessonProgress.getTotalXp(),
        )
    }

    /**
     * Vyrenderuje kartu, uloží ji do cache a spustí `Intent.ACTION_SEND` s `image/png`.
     * Při jakémkoliv selhání generování bitmapy tiše spadne zpět na čistě textové sdílení,
     * aby uživatel o možnost sdílet nepřišel.
     */
    fun shareStreakCard(
        context: Context,
        stats: CardStats,
        shareText: String,
        chooserTitle: String,
    ) {
        val uri = try {
            generate(context, stats)
        } catch (t: Throwable) {
            Log.e(TAG, "Generování PNG karty selhalo, padám zpět na textové sdílení", t)
            null
        }

        val send = Intent(Intent.ACTION_SEND).apply {
            if (uri != null) {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                type = "text/plain"
            }
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(send, chooserTitle))
    }

    /** Vyrenderuje bitmapu, uloží ji do `cacheDir/share_cards/share_card.png` a vrátí `content://` URI přes FileProvider. */
    private fun generate(context: Context, stats: CardStats): Uri {
        val bitmap = renderBitmap(context, stats)
        val file = saveToCache(context, bitmap)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun saveToCache(context: Context, bitmap: Bitmap): File {
        val dir = File(context.cacheDir, CACHE_SUBDIR).apply { mkdirs() }
        // Přepisujeme stejný název souboru při každém sdílení — staré karty se nehromadí.
        val file = File(dir, FILE_NAME)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    private fun renderBitmap(context: Context, stats: CardStats): Bitmap {
        val bitmap = Bitmap.createBitmap(CARD_WIDTH, CARD_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Pozadí — diagonální gradient v barvách appky.
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, CARD_WIDTH.toFloat(), CARD_HEIGHT.toFloat(),
                DarkBackground.toArgb(), DarkSurface.toArgb(),
                Shader.TileMode.CLAMP,
            )
        }
        canvas.drawRect(0f, 0f, CARD_WIDTH.toFloat(), CARD_HEIGHT.toFloat(), bgPaint)

        val centerX = CARD_WIDTH / 2f

        // Hlavička — název appky.
        val titlePaint = textPaint(56f, TextSecondary.toArgb(), Typeface.DEFAULT_BOLD)
        canvas.drawText(
            context.getString(R.string.app_name).uppercase(),
            centerX,
            150f,
            titlePaint,
        )

        // Plamínek streaku — vykreslení existující vektorové ikonky ic_streak (stejná identita jako top bar).
        val flameSize = 260
        try {
            val flameDrawable = androidx.core.content.ContextCompat.getDrawable(context, R.drawable.ic_streak)
            flameDrawable?.setBounds(
                (centerX - flameSize / 2f).toInt(),
                240,
                (centerX + flameSize / 2f).toInt(),
                240 + flameSize,
            )
            flameDrawable?.draw(canvas)
        } catch (t: Throwable) {
            Log.w(TAG, "Nepodařilo se vykreslit ic_streak do share karty", t)
        }

        // Velké číslo streaku.
        val streakPaint = textPaint(200f, WarningAmber.toArgb(), Typeface.DEFAULT_BOLD)
        canvas.drawText(stats.streak.toString(), centerX, 720f, streakPaint)

        val streakLabelPaint = textPaint(52f, TextPrimary.toArgb(), Typeface.DEFAULT)
        canvas.drawText("dní v řadě 🔥", centerX, 790f, streakLabelPaint)

        // Oddělovací linka.
        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AccentCyan.copy(alpha = 0.35f).toArgb()
            strokeWidth = 3f
        }
        canvas.drawLine(120f, 880f, CARD_WIDTH - 120f, 880f, dividerPaint)

        // Souhrn týdne.
        val sectionTitlePaint = textPaint(46f, AccentTeal.toArgb(), Typeface.DEFAULT_BOLD)
        canvas.drawText("SOUHRN POSLEDNÍCH 7 DNÍ", centerX, 960f, sectionTitlePaint)

        drawStatRow(canvas, centerX, 1060f, "Lekcí dokončeno", stats.lessonsThisWeek.toString())
        drawStatRow(canvas, centerX, 1140f, "Aktivních dní", "${stats.activeDaysThisWeek} / 7")
        drawStatRow(canvas, centerX, 1220f, "XP za týden", "${stats.xpThisWeek} XP")

        // Patička.
        val footerPaint = textPaint(36f, TextSecondary.copy(alpha = 0.8f).toArgb(), Typeface.DEFAULT)
        canvas.drawText("Autoškolák · celkem ${stats.totalXp} XP", centerX, CARD_HEIGHT - 60f, footerPaint)

        return bitmap
    }

    private fun drawStatRow(canvas: Canvas, centerX: Float, y: Float, label: String, value: String) {
        val labelPaint = textPaint(42f, TextSecondary.toArgb(), Typeface.DEFAULT, align = Paint.Align.LEFT)
        val valuePaint = textPaint(42f, TextPrimary.toArgb(), Typeface.DEFAULT_BOLD, align = Paint.Align.RIGHT)
        val left = 140f
        val right = CARD_WIDTH - 140f
        canvas.drawText(label, left, y, labelPaint)
        canvas.drawText(value, right, y, valuePaint)
    }

    private fun textPaint(
        textSizePx: Float,
        colorInt: Int,
        typeface: Typeface,
        align: Paint.Align = Paint.Align.CENTER,
    ): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = textSizePx
        color = colorInt
        this.typeface = typeface
        textAlign = align
    }
}
