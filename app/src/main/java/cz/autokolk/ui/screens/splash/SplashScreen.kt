package cz.autokolk.ui.screens.splash

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import cz.autokolk.R
import cz.autokolk.VideoModuleRegistry
import cz.autokolk.ui.components.buttons.PrimaryGradientButton
import cz.autokolk.ui.navigation.Route
import cz.autokolk.ui.screens.onboarding.OnboardingPreferences
import cz.autokolk.ui.theme.AccentCyan
import cz.autokolk.ui.theme.AccentTeal
import cz.autokolk.ui.theme.DarkBackground
import cz.autokolk.ui.theme.TextPrimary
import cz.autokolk.ui.theme.TextSecondary

private const val TAG = "SplashScreen"
private enum class SplashState {
    CONSENT, DFM_CHECK, DFM_DOWNLOADING, TERMS, ERROR, NAVIGATING,
}

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    var state by remember { mutableStateOf(SplashState.CONSENT) }
    var downloadProgress by remember { mutableFloatStateOf(-1f) }
    var statusText by remember { mutableStateOf("Příprava aplikace…") }

    val splitInstallManager = remember { SplitInstallManagerFactory.create(context) }

    fun navigateAfterSplash() {
        if (state == SplashState.NAVIGATING) return
        state = SplashState.NAVIGATING
        val onboardingPrefs = OnboardingPreferences(context)
        val destination =
            if (onboardingPrefs.isCompleted) Route.Home.route else Route.Onboarding.route
        navController.navigate(destination) {
            popUpTo(Route.Splash.route) { inclusive = true }
        }
    }

    fun checkTermsThenNavigate() {
        val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        if (prefs.getBoolean("terms_accepted_v1", false)) {
            navigateAfterSplash()
        } else {
            state = SplashState.TERMS
        }
    }

    fun startOrCheckDfm() {
        state = SplashState.DFM_CHECK
        statusText = "Kontrola obsahu…"

        if (splitInstallManager.installedModules.contains(VideoModuleRegistry.MEDIA_FEATURE_MODULE_NAME)) {
            Log.d(TAG, "Media module already installed")
            checkTermsThenNavigate()
            return
        }

        state = SplashState.DFM_DOWNLOADING
        statusText = "Stahuji materiály…"
        downloadProgress = -1f

        try {
            val request = SplitInstallRequest.newBuilder()
                .addModule(VideoModuleRegistry.MEDIA_FEATURE_MODULE_NAME)
                .build()
            splitInstallManager.startInstall(request)
                .addOnFailureListener { e ->
                    Log.e(TAG, "DFM install failed", e)
                    state = SplashState.ERROR
                    statusText = "Chyba při stahování"
                }
        } catch (e: Exception) {
            Log.e(TAG, "DFM install error", e)
            state = SplashState.ERROR
            statusText = "Chyba při stahování"
        }
    }

    DisposableEffect(splitInstallManager) {
        val listener = SplitInstallStateUpdatedListener { installState ->
            if (!installState.moduleNames().contains(VideoModuleRegistry.MEDIA_FEATURE_MODULE_NAME)) {
                return@SplitInstallStateUpdatedListener
            }
            when (installState.status()) {
                SplitInstallSessionStatus.DOWNLOADING -> {
                    val total = installState.totalBytesToDownload()
                    val pct = if (total > 0) {
                        installState.bytesDownloaded().toFloat() / total
                    } else {
                        -1f
                    }
                    downloadProgress = pct
                    if (pct >= 0f) {
                        statusText = "Stahuji materiály… ${(pct * 100).toInt()} %"
                    }
                }
                SplitInstallSessionStatus.INSTALLED -> {
                    Log.d(TAG, "Image module installed")
                    checkTermsThenNavigate()
                }
                SplitInstallSessionStatus.FAILED -> {
                    Log.e(TAG, "DFM install failed: ${installState.errorCode()}")
                    state = SplashState.ERROR
                    statusText = "Chyba při stahování"
                }
                else -> { /* PENDING, INSTALLING, etc. */ }
            }
        }
        splitInstallManager.registerListener(listener)
        onDispose {
            try { splitInstallManager.unregisterListener(listener) } catch (_: Throwable) {}
        }
    }

    LaunchedEffect(Unit) {
        if (activity == null) {
            startOrCheckDfm()
            return@LaunchedEffect
        }
        val params = ConsentRequestParameters.Builder().build()
        val consentInfo = UserMessagingPlatform.getConsentInformation(activity)
        consentInfo.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    startOrCheckDfm()
                }
            },
            { startOrCheckDfm() },
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp),
        ) {
            AppLogo()
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Autoškolák",
                style = MaterialTheme.typography.headlineLarge,
                color = AccentCyan,
            )
            Spacer(Modifier.height(24.dp))

            when (state) {
                SplashState.CONSENT, SplashState.DFM_CHECK, SplashState.NAVIGATING -> {
                    CircularProgressIndicator(color = AccentCyan)
                    Spacer(Modifier.height(16.dp))
                    Text(statusText, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
                SplashState.DFM_DOWNLOADING -> {
                    if (downloadProgress >= 0f) {
                        val animatedProgress by animateFloatAsState(
                            targetValue = downloadProgress,
                            animationSpec = tween(300),
                            label = "dfmProgress",
                        )
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = AccentCyan,
                            trackColor = AccentCyan.copy(alpha = 0.2f),
                        )
                    } else {
                        CircularProgressIndicator(color = AccentCyan, strokeWidth = 2.dp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(statusText, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
                SplashState.ERROR -> {
                    Text(statusText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                    PrimaryGradientButton(
                        text = "Zkusit znovu",
                        onClick = { startOrCheckDfm() },
                    )
                }
                SplashState.TERMS -> { /* handled by AlertDialog below */ }
            }
        }
    }

    if (state == SplashState.TERMS) {
        TermsDialog(
            onAccept = {
                context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                    .edit().putBoolean("terms_accepted_v1", true).apply()
                navigateAfterSplash()
            },
            onLinkClick = {
                try {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/dos-pachos-studio/zásady-ochrany-soukromí"))
                    )
                } catch (_: Throwable) {}
            },
        )
    }
}

/**
 * Logo aplikace na načítací obrazovce — úmyslně **lokální** vektorový zdroj
 * ([R.drawable.ic_launcher_foreground], stejná grafika jako skutečná ikona appky),
 * ne obrázek z `mediaassets`. Tahle obrazovka totiž sama teprve spouští stažení
 * on-demand modulu `mediaassets` (viz `startOrCheckDfm`), takže logo z něj by
 * při úplně prvním spuštění appky mohlo krátce zobrazit shimmer/error placeholder,
 * než se modul stáhne. Na onboarding obrazovce (spouští se až po dokončení
 * stahování) naopak `IconSign.png` z `mediaassets` zůstává — viz `OnboardingScreen.kt`.
 */
@Composable
private fun AppLogo() {
    Box(
        modifier = Modifier
            .size(160.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AccentTeal.copy(alpha = 0.85f),
                        AccentCyan.copy(alpha = 0.55f),
                    ),
                ),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "Autoškolák",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(110.dp),
        )
    }
}

@Composable
private fun TermsDialog(
    onAccept: () -> Unit,
    onLinkClick: () -> Unit,
) {
    val annotated = buildAnnotatedString {
        val prefix = "Používáním aplikace souhlasíte s "
        val link = "podmínkami používání a zásadami ochrany soukromí"
        val suffix = "."
        append(prefix)
        pushStringAnnotation("URL", "link")
        pushStyle(SpanStyle(color = AccentCyan, textDecoration = TextDecoration.Underline))
        append(link)
        pop()
        pop()
        append(suffix)
    }

    AlertDialog(
        onDismissRequest = { /* non-cancellable */ },
        title = { Text("Podmínky používání") },
        text = {
            ClickableText(
                text = annotated,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                onClick = { offset ->
                    annotated.getStringAnnotations("URL", offset, offset)
                        .firstOrNull()?.let { onLinkClick() }
                },
            )
        },
        confirmButton = {
            TextButton(onClick = onAccept) {
                Text("OK")
            }
        },
    )
}
