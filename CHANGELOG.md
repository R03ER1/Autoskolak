# Changelog

All notable changes to this project will be documented in this file.

This file follows a simple format inspired by Keep a Changelog.

## [Unreleased]
-

## [2.0.24] - 2026-04-14
- Added: New Compose package structure (`ui/theme`, `ui/components/*`, `ui/screens/*`, `ui/navigation`, `data`, `util`) — ready for upcoming Compose UI migration.

## [2.0.23] - 2026-04-14
- Added: Sound engine (`SoundManager` singleton + Compose `rememberSoundManager()` helper) using `SoundPool` for low-latency playback of short effects (correct, wrong, streak, coin, tap, levelup, countdown). Gracefully skips missing raw resources — actual sound files will be added in Phase 12.

## [2.0.22] - 2026-04-14
- Added: Centralized haptic feedback utility (`HapticFeedback` object + Compose `rememberHaptic()` helper) with six feedback levels (light, medium, heavy, success, error, streak). Uses `HapticFeedbackConstants` on API 30+ with `Vibrator` fallback for older devices.

## [2.0.21] - 2026-04-14
- Added: Haze 1.7.2 (glassmorphism blur efekty pro Compose — základní haze + materials).
- Changed: compileSdk 35 → 36 (vyžadováno tranzitivními závislostmi Haze).

## [2.0.20] - 2026-04-14
- Added: Accompanist Permissions 0.37.3 a Drawable Painter 0.37.3, Coil Compose 3.4.0, Compose Foundation a Animation (příprava pro Pager, async obrázky, animace).

## [2.0.19] - 2026-04-14
- Added: Quicksand font family (5 weights: Light, Regular, Medium, SemiBold, Bold) pro Compose Typography.

## [2.0.18] - 2026-04-14
- Added: Lottie Compose 6.7.1 závislost (příprava pro animace: splash, správná/špatná odpověď, streak, konfety atd.).

## [2.0.17] - 2026-04-14
- Added: Navigation Compose 2.9.7 závislost (příprava pro NavGraph a single-activity navigaci).

## [2.0.16] - 2026-04-14
- Added: Jetpack Compose závislosti (BOM 2026.04.00, Material 3, UI Tooling); Compose a XML koexistují.

## [2.0.15] - 2026-04-14
- Security / compliance: `usesCleartextTraffic` vypnuto; HTTP zůstává jen pro localhost/emulátor přes `network_security_config`.
- Privacy: Google UMP (souhlas s reklamami) před startem stahování DFM na `LoadingActivity`; odkaz na soukromí v Nastavení.
- Performance: cache extrahovaných videí v `cacheDir/video_asset_cache` (LRU) místo kopírování do nového temp souboru při každé otázce.
- UX: stav stahování video modulu v lekci; hláška při nedostatku místa; `LoadingActivity` — text k potvrzení ve Play; nápověda k síti v popisku načítání.
- Changed: `imageassets` se žádá jen z `LoadingActivity` (ne duplicitně z `App`).
- Changed: vývojářská karta v Nastavení jen v `debug`; release R8 (`isMinifyEnabled`); odstraněné nepoužívané Compose závislosti z `:app`.
- i18n / a11y: české texty hladové notifikace, titulek procvičování, popisky spodní navigace; `pageTitle` v layoutu nastavení (oprava Lint `MissingInflatedId`).
- Refactor: `VideoModuleRegistry`, `VideoSplitInstallListenerFactory`, `VideoAssetFileCache`; doplněny ProGuard pravidla a unit test `VideoModuleRegistryTest`.

## [2.0.14] - 2026-04-14
- Fixed: `LoadingActivity` — blokace systémového zpět přes `OnBackPressedDispatcher` místo prázdného `onBackPressed()`, aby Lint nehlásil `MissingSuperCall` a chování zůstalo stejné.

## [2.0.13] - 2026-04-14
- Fixed: Notifikace z `HeartRefillJobService` a `HungerNotificationService` — před voláním `notify` se na Android 13+ kontroluje `POST_NOTIFICATIONS`; bez oprávnění se preference „už odesláno“ neaktualizují, aby šlo upozornění zkusit znovu po udělení práva. Opravuje Lint `MissingPermission` a sestavení s `lintDebug`.

## [2.0.12] - 2026-04-14
- Added: Dokument [`docs/REVIDECNI_AUDIT.md`](docs/REVIDECNI_AUDIT.md) — revizní audit aplikace (moduly DFM, úložiště a cache videí, stabilita, architektura, UX a použitelnost, design, výkon, notifikace, compliance, testy, release, backlog).
- Changed: `.gitignore` — ignorovat `**/build/` ve všech modulech (např. `videoassets*`), aby se do gitu nedostávaly Gradle meziprodukty.

## [2.0.11] - 2026-03-31
- Changed: Lesson results interstitial — preload starts during reading intro and during the quiz (`LessonInterstitialAds`), so the ad is often ready when results open; loading overlay redesigned (Material card + circular indicator).
- Note: If the user finishes before preload completes, the new loading UI still appears until the ad loads.

## [2.0.10] - 2026-03-31
- Fixed: Topic intro / reading lesson (`ReadingLessonActivity`) — image paths now use the `images/` asset prefix like question images, so intro slides show pictures again.

## [2.0.9] - 2026-03-30
- Changed: Vývojářské možnosti v Nastavení jsou dostupné i v release buildu; heslo je konstanta `DEVELOPER_OPTIONS_PASSWORD` v `SettingsActivity` (ne `local.properties` / BuildConfig).

## [2.0.8] - 2026-03-30
- Fixed: Alex death flow — death screen no longer opens twice (onCreate + onResume); after revive, Alex page loads main content if it was skipped while dead.
- Changed: Death revival hold shortened to 3 seconds; dead Alex uses `images/alex/AlexDead.png` from the imageassets module (with legacy path fallback).
- Added: `AlexDead.png` asset under `imageassets/src/main/assets/images/alex/`.

## [2.0.7] - 2026-03-30
- Changed: Practice — „Tvoje chyby“ section title uses red accent for visibility.

## [2.0.6] - 2026-03-30
- Added: Practice screen — "Tvoje chyby" bucket: tracks wrong answers from lessons, practice categories, random quiz, and tests; sorts by consecutive wrong streak; ✅/❌ chips like other categories; fixing here marks correct until you miss the same question again anywhere.

## [2.0.5] - 2026-03-30
- Changed: Vývojářské heslo už není v kódu — načítá se z `local.properties` (`developerOptionsPassword`) jen při debug buildu. V release buildu je sekce Debugging skrytá úplně.

## [2.0.4] - 2026-03-26
- Changed: Obrazovka streaku po lekci — stejné tlačítko (teal gradient) a typografie jako na obrazovce výsledků; černé pozadí, velký plamen a číslo zůstávají.

## [2.0.3] - 2026-03-26
- Added: When an achievement star is earned, a full-screen overlay matches random events (Alex with sunglasses, confetti, pop animations, OK) and shows the achievement name plus how many stars you have in that category.

## [2.0.2] - 2026-03-26
- Fixed: Random event overlay now loads Alex with sunglasses from `images/AlexCool.png` (imageassets module); the wrong paw fallback (`ic_alex`) appeared when the old path `alex/AlexCool.png` did not exist.
- Changed: Event overlay typography — slightly smaller “Událost!” headline, larger description text, and coin/life/hunger change on its own line below.

## [2.0.1] - 2026-03-26
- Fixed: On Android 15+ (target SDK 35), window content no longer draws under the status and navigation bars; portrait-only setting is unchanged. Implemented via `AutokolkActivity` (`WindowCompat.setDecorFitsSystemWindows`) and theme `windowOptOutEdgeToEdgeEnforcement` on API 35.

## [2.0.0] - 2026-03-19
- Release: Přidány AdMob reklamy (interstitial po lekci) a rewarded reklama za srdce.

## [1.0.4] - 2026-03-19
- Added: Integrated Google Mobile Ads SDK (AdMob), including `APPLICATION_ID` in manifest and SDK initialization in `App`.
- Added: Interstitial AdMob ad is now requested and shown after each completed lesson/review on the results screen.

## [1.0.3] - 2026-03-19
- Added: First-run consent dialog asking users to accept terms of use and privacy policy with a link to the hosted policy page.

## [1.0.2] - 2026-03-12
- Changed: Forced all activities to portrait orientation so the app always stays in vertical mode and does not rotate to landscape.

## [1.0.1] - 2026-03-12
- Added: New startup loading screen that blocks the app until the `imageassets` dynamic feature module (with all lesson and Alex images) is installed.
- Changed: Home screen is now opened only after required image packages are fully available to prevent missing images when users start using the app immediately after install.

## [1.0.0] - 2025-12-11
- Changed: Version bump to 1.0.0 for official release.

## [0.1.58] - 2025-12-11
- Changed: Version bump for release submission.

## [0.1.57] - 2025-12-03
- Changed: Split `videoassets` module into 5 smaller Dynamic Feature Modules (videoassets1-5) to comply with 200MB size limit per module. Each module contains approximately 11 videos, balanced by file size (~160MB per module).
- Changed: Converted video modules back from Play Asset Delivery to Dynamic Feature Modules using SplitInstall API.
- Changed: Updated video loading code in MainActivity to use SplitInstall API instead of Asset Delivery API.
- Changed: Replaced Play Asset Delivery dependency with Play Feature Delivery for dynamic feature modules.

## [0.1.56] - 2025-12-03
- Changed: Converted `videoassets` module from Dynamic Feature Module to Play Asset Delivery (Asset Pack) to resolve 200MB size limit issue. Asset Packs support up to 1.5GB per pack, allowing all video assets to be delivered on-demand without size restrictions.
- Changed: Updated video loading code in MainActivity to use Asset Delivery API instead of SplitInstall API.
- Added: Play Asset Delivery dependency for handling large video asset downloads.

## [0.1.55] - 2025-12-03
- Added: Dynamic feature module `imageassets` for all lesson and Alex images, moved from base assets to reduce base module size.
- Added: Automatic Play Core request for `imageassets` in `App.onCreate` so image assets are available early in app lifecycle.

## [0.1.54] - 2025-12-03
- Added: Dynamic feature module `videoassets` is now requested via Play Core SplitInstall so videos are delivered on-demand.
- Added: Play Feature Delivery dependency to handle installation of the `videoassets` module at runtime.

## [0.1.53] - 2025-12-03
- Fixed: Changed package name from reserved "com.example.autokolk" to "cz.autokolk" to comply with Google Play requirements.
- Fixed: Added bundle configuration to reduce AAB size (language, density, and ABI splits enabled).
- Note: For apps exceeding 200MB, consider using Play Asset Delivery for large video assets or compressing assets further.

## [0.1.52] - 2025-12-03
- Fixed: ConstraintLayout lint error in activity_results.xml - resultsText now correctly constrains to buttonsContainer instead of homeButton (which is nested inside).

## [0.1.51] - 2025-12-03
- Added: Password protection for developer options - the debugging dropdown and random event trigger now require password "Vasik31zaz" to access. Once unlocked, access is saved and remains available.
- Synced version string in Settings and Gradle to 0.1.51.

## [0.1.50] - 2025-12-03
- Fixed: Alex death screen no longer reappears after completing revive - added a revive timestamp that provides a 2-second grace period after revival, preventing death screen from showing immediately. Also changed `setCurrentHunger()` to use `.commit()` for synchronous writes and removed the 1-second delay before finishing the death activity.

## [0.1.49] - 2025-12-03
- Fixed: Reviving Alex no longer immediately reopens the death screen — hunger decay baseline now resets when hunger is manually changed, preventing instant re-decay to 0.
- Synced version string in Settings and Gradle to 0.1.49.

## [0.1.48] - 2025-01-XX
- Added: Death screen for Alex when hunger reaches 0, showing a message about starvation and a dead lion image rotated 90 degrees.
- Added: Hold-to-revive mechanic - user must hold a circular button for 5 seconds to revive the lion.
- Added: During the hold, the lion image slowly rotates back to upright position.
- Added: After successful revival, the lion changes to happy state and hunger is restored to 50%.
- Synced version string in Settings and Gradle to 0.1.48.

## [0.1.47] - 2025-11-10
- Added: Lion image in introduction tutorials now animates sliding up from the bottom.
- Synced version string in Settings and Gradle to 0.1.47.

## [0.1.46] - 2025-11-10
- Changed: Confetti now auto-disappears — runs for ~2s, then fades out and clears.
- Synced version string in Settings and Gradle to 0.1.46.

## [0.1.45] - 2025-11-10
- Added: Confetti animation to random event overlay - colorful particles fall from the top when events are shown.
- Synced version string in Settings and Gradle to 0.1.45.

## [0.1.44] - 2025-11-10
- Added: Random event overlay animations - lion image slides up from bottom, texts pop up with scale animation.
- Synced version string in Settings and Gradle to 0.1.44.

## [0.1.43] - 2025-11-10
- Changed: Random event headline ("Událost!") is now much larger (80sp) and spans the full screen width while staying on a single line.
- Synced version string in Settings and Gradle to 0.1.43.

## [0.1.42] - 2025-11-10
- Added: Floating hunger bar on Alex food screen fixed to the top, showing current hunger percent.
- Changed: Hunger bar updates live as you purchase food.
- Synced version string in Settings and Gradle to 0.1.42.

## [0.1.41] - 2025-11-10
- Fixed: Top bar bottom sheets now show the + button only for Streak. Points and Hearts show the close button only.
- Synced version string in Settings and Gradle to 0.1.41.

## [0.1.40] - 2025-11-06
- Changed: Clicking "Vymazat vše" now also resets all achievements (Úspěchy).
- Synced version string in Settings and Gradle to 0.1.40.

## [0.1.39] - 2025-11-06
- Changed: Achievements progress now shows percentage toward the next star (e.g., "74%") instead of remaining X/Y.
- Synced version string in Settings and Gradle to 0.1.39.

## [0.1.38] - 2025-11-06
- Added: Achievements (Úspěchy) screen in Settings.
- Added: Tracks and unlocks:
  - Streak (5/25/100)
  - Chyby – opravy (20/50/200)
  - Otázky – správně zodpovězeno (100/500/1140)
  - Penízky – získané (50/250/1000) a utracené (150/400/1500)
  - Alex – streak krmení (10/20/40) a jednotlivá jídla (mrkev 60, zmrzlina 90, kuře 30, klobás 250, pivo 5, kamení 5)
- Added: Each unlocked achievement awards 150 points automatically.
- Synced version string in Settings and Gradle to 0.1.38.

## [0.1.35] - 2025-11-06
- Added: Random events every 1–3 days that show on app open with a dark overlay (like tutorials) titled "Událost!" with Alex image and a message.
- Added: Events can add/remove lives (hearts) or coins (points). Examples: Lov (+10 životů), Nemoc (-10 životů), Pracant (+5 mincí), Dlužník (-5 mincí), and a few more variations.
- Changed: Events do not appear on the very first app open to avoid covering tutorials.
- Synced version string in Settings and Gradle to 0.1.35.

## [0.1.36] - 2025-11-06
- Added: Settings → Debugging now has a "Spustit náhodnou událost" card to trigger an event immediately for testing.
- Synced version string in Settings and Gradle to 0.1.36.

## [0.1.37] - 2025-11-06
- Changed: Event overlay now shows the Alex image from assets (`AlexCool.png`).
- Changed: Random events list updated — removed life-reduction events; added hunger events that increase/decrease hunger value; kept positive life and coin events.
- Synced version string in Settings and Gradle to 0.1.37.

## [0.1.34] - 2025-01-XX
- Added: "Podrobnosti" clickable text on test results screen that opens a scrollable view showing all questions, correct answers, and user answers.
- Synced version string in Settings and Gradle to 0.1.34.

## [0.1.33] - 2025-11-06
- Fixed: Closing topic info opened from a lesson now returns to that lesson instead of reopening it.
- Synced version string in Settings and Gradle to 0.1.33.

## [0.1.32] - 2025-11-06
- Added: Info button next to the lesson progress bar opens the topic intro again during a lesson (when available for that lesson).
- Synced version string in Settings and Gradle to 0.1.32.

## [0.1.31] - 2025-11-06
- Changed: Practice chips are now centered and each takes one-third of the row width.
- Synced version string in Settings and Gradle to 0.1.31.

## [0.1.30] - 2025-11-06
- Fixed: Practice chips no longer propagate clicks to the parent card, preventing double-opening of questions.
- Synced version string in Settings and Gradle to 0.1.30.

## [0.1.29] - 2025-11-06
- Added: Practice counters (✅ ❌ ❔) are now buttons to start filtered practice.
- Added: New practice modes for Correct/Wrong/Unanswered in question loading.
- Synced version string in Settings and Gradle to 0.1.29.

## [0.1.28] - 2025-11-06
- Changed: Bottom sheet button text updated from "OK" to "Zavřít" for Lives/Points/Streak.
- Synced version string in Settings and Gradle to 0.1.28.

## [0.1.27] - 2025-11-06
- Added: "+" button in Lives bottom sheet to start 10 random questions from CSV.
- Added: Grants +1 heart at the end if score is at least 50%.
- Synced version string in Settings and Gradle to 0.1.27.

## [0.1.26] - 2025-11-06
- Fixed: Prevent stale full lives notifications from firing on app open.
- Added: Track when hearts first become full and only notify within 30 minutes.
- Synced version string in Settings and Gradle to 0.1.26.

## [0.1.24] - 2025-11-06
- Added: When "kamení" is active (48h freeze), the Alex health bar sweep animation is disabled.
- Added: While "kamení" is active, the hunger label shows "Hladovění začne za X hod a X min" next to the hunger value, counting down to freeze end.

## [0.1.25] - 2025-11-06
- Changed: Kamení countdown moved to its own line below hunger and limited in width to avoid spanning the full screen.

## [0.1.23] - 2025-01-27
- Changed: Last question button in test attempts now shows "UKONČIT" with red/orange gradient background and white text instead of showing "25/25".

## [0.1.22] - 2025-01-27
- Fixed: Answer shuffling now preserves order for questions with a, b, c answers.

## [0.1.21] - 2025-10-20
- Fixed: Full lives notifications now work correctly and don't spam.
- Fixed: Added duplicate prevention for lives notifications.
- Fixed: Service now continues scheduling after showing full lives notification.
- Fixed: Reset notification tracking when hearts drop below 15.
- Bumped version string in settings screen.

## [0.1.20] - 2025-10-20
- Fixed: Hunger notifications now align exactly with each 10% drop; timing corrected.
- Changed: Scheduler aligns checks to the next 10% boundary instead of arbitrary ticks.
- Fixed: Reset last-notified level when hunger increases to avoid missed future alerts.
- Bumped version string in settings screen.

## [0.1.19] - 2025-10-20
- Changed: Tutorial text now renders the token "[mince]" as an inline coin icon.
- Bumped version string in settings screen.

## [0.1.18] - 2025-10-20
- Fixed: Lesson completion text now shows the correct display lesson number instead of the internal lesson number.
- Bumped version string in settings screen.

## [0.1.17] - 2025-10-20
- Changed: Next/previous buttons in lessons and practice result panel now use lowercase text ("předchozí", "další").
- Changed: Next/previous buttons now have lighter font weight and smaller size (80dp width, 36dp height).
- Changed: Added spacing between next/previous buttons in result panel.
- Bumped version string in settings screen.

## [0.1.16] - 2025-10-16
- Changed: Lesson and practice result panel now uses gradient backgrounds (green to green-black for correct, red to red-black for wrong) instead of solid colors.
- Bumped version string in settings screen.

## [0.1.15] - 2025-10-16
- Changed: Lesson circle gradients now use 45-degree diagonal orientation (top-left to bottom-right).
- Bumped version string in settings screen.

## [0.1.14] - 2025-10-16
- Changed: Lesson circle gradients now shift hue (e.g., greenish yellow to blueish yellow) instead of brightness.
- Bumped version string in settings screen.

## [0.1.13] - 2025-10-16
- Changed: Lesson circle gradients now preserve hue (vary S/V only) for cleaner look.
- Bumped version string in settings screen.

## [0.1.12] - 2025-10-16
- Changed: Home screen lesson icons now use a subtle vertical gradient per section color (replacing rainbow solids).
- Bumped version string in settings screen.

## [0.1.11] - 2025-10-16
- Changed: Shop items now center text and remove empty image space.
- Added: In-app popup for shop purchases/errors (reused practice/food popup).
- Bumped version string in settings screen.

## [0.1.10] - 2025-10-16
- Fixed: Food popup icon is now white for better contrast.
- Bumped version string in settings screen.

## [0.1.9] - 2025-10-16
- Changed: Food purchases now show an in-app popup (same style as practice).
- Replaced Android Toasts with animated popup for food and kamení actions.
- Bumped version string in settings screen.

## [0.1.8] - 2025-10-16
- Changed: Food item boxes now have no white outline and a slightly lighter dark background.
- Bumped version string in settings screen.

## [0.1.7] - 2025-10-16
- Fixed: Beer item coin icon now uses yellow (not white).
- Changed: Kamení layout now shows subtitle centered, coin cost centered below.
- Bumped version string in settings screen.

## [0.1.6] - 2025-10-16
- Changed: Food menu coin icons now use the same yellow as the top menu.
- Bumped version string in settings screen.

## [0.1.5] - 2025-10-16
- Changed: Food menu: titles centered; meat and points shown inline on one row.
- Changed: Replaced food/points emojis with white icons in the menu.
- Bumped version string in settings screen.

## [0.1.4] - 2025-10-16
- Changed: Increased padding inside Alex Food/Shop close button so the X fits better.
- Bumped version string in settings screen.

## [0.1.3] - 2025-10-16
- Changed: Alex Food/Shop close button fixed position, moved slightly lower-right.
- Changed: Close button now uses gradient background with white X, square shape.
- Bumped version string in settings screen.

## [0.1.2] - 2025-10-16
- Fixed: Resource compile error in `activity_changelog.xml` (missing `xmlns:app`).
- Bumped version string in settings screen.

## [0.1.1] - 2025-10-16
- Added: Tapping version in Settings opens in-app Changelog screen.
- Added: New `ChangelogActivity` that reads `CHANGELOG.md` from assets and displays it.
- Bumped version string in settings screen.

## [0.1.0] - 2025-10-16
- Started the first wave of external testing.

## [0.0.72] - 2025-10-16
- Changed: Test statistics page now shows a top title "Statistiky" and is centered.
- Changed: Average text merged into one line: "Průměrné skóre: xx/50".
- Changed: Added margins and padding around the scores chart for spacing.
- Added: Red threshold line at 43 points on the statistics chart (pass mark).
- Bumped version string in settings screen.

## [0.0.71] - 2025-10-16
- Changed: Results screen titles are now white and centered.
- Changed: Results return/close buttons now use the dark green–blue gradient with white text.
- Bumped version string in settings screen.

## [0.0.70] - 2025-10-16
- Fixed: Crash when opening lesson with 0 lives (ClassCastException in no-hearts bottom sheet).
- Bumped version string in settings screen.

## [0.0.69] - 2025-10-16
- Changed: Lesson progress bars (top) now use the dark green–blue gradient.
- Bumped version string in settings screen.

## [0.0.68] - 2025-10-16
- Changed: Practice navigation buttons (Prev/Next) now use the gradient and white text.
- Confirmed: Lesson navigation already uses primary gradient style.
- Bumped version string in settings screen.

## [0.0.67] - 2025-10-16
- Changed: Tutorial "OK" button now uses the gradient and white text.
- Changed: Streak/Coins/Lives bottom-sheet "OK" button uses the gradient and white text.
- Changed: Alex shop "Koupit" buttons use the gradient and white text.
- Bumped version string in settings screen.

## [0.0.66] - 2025-10-16
- Changed: Test Attempt "Spustit test" button now uses the dark green–blue gradient and white text.
- Bumped version string in settings screen.

## [0.0.65] - 2025-10-16
- Changed: Alex page Food and Shop button icons are now tinted white.
- Bumped version string in settings screen.

## [0.0.64] - 2025-10-16
- Changed: Alex page Food and Shop buttons now use the same dark green–blue gradient.
- Bumped version string in settings screen.

## [0.0.63] - 2025-10-16
- Changed: Popup lesson "Start" button now uses a dark green–blue gradient background.
- Changed: Updated primary button style to use gradient drawable.
- Bumped version string in settings screen.

## [0.0.62] - 2025-10-16
- Added: When Alex's hunger reaches 0, he is displayed as `AlexDead.png` rotated 90° to the right.
- Bumped version string in settings screen.

## [0.0.61] - 2025-10-16
- Changed: Hunger notifications now align with variable tick timing and trigger exactly when hunger crosses each 10-point threshold down to 0.
- Bumped version string in settings screen.

## [0.0.60] - 2025-10-16
- Added: Settings → Hunger debug now shows countdown until the next -1 point.
- Confirmed: Hunger speed display remains accurate (still shows points per hour).
- Bumped version string in settings screen.

## [0.0.59] - 2025-10-16
- Changed: Alex hunger decay now removes a fixed 1 point per tick; the interval between ticks varies by current hunger speed (min 15 minutes). This replaces the previous model that removed a variable amount each fixed hour.
- Bumped version string in settings screen.

## [0.0.58] - 2025-10-16
- Fixed: All settings dialogs now fully use a dark theme, including title, message panel, input, and button bar, preventing any white-on-white on older devices.
- Bumped version string in settings screen.

## [0.0.57] - 2025-10-16
- Fixed: Settings dialogs' text inputs now enforce a dark background with light text to prevent white-on-white on older devices.
- Bumped version string in settings screen.

## [0.0.56] - 2025-10-16
- Changed: Settings page layout restructured for better organization.
- Removed: Test 2 and Test 3 toggle switches from settings.
- Moved: Delete all button to the top of settings for better visibility.
- Added: Debugging dropdown section containing all testing and debugging tools.
- Added: Collapsible debugging section with arrow indicator for better UX.
- Bumped version to 0.0.56.
- Added: Coin popup notification in practice section that shows "+1[coin icon]" when points are earned.
- Added: Popup appears at the top of the screen and fades out automatically after 1 second.
- Added: Multiple popups are staggered by 200ms when multiple points are earned at once.
- Changed: Practice scoring now provides visual feedback when users earn points every 5 questions.

## [0.0.55] - 2025-10-16
- Fixed: Bug in test attempts where users couldn't change their answers after selecting one.
- Changed: In test mode, answer options remain enabled after selection, allowing users to change their answers.
- Changed: Answer selection logic now differentiates between test mode and other modes for better user experience.
- Bumped version string in settings screen.

## [0.0.54] - 2025-10-16
- Added: Click actions for notifications - lives notification opens home screen, hunger notification opens Alex page.
- Changed: Both notifications now have proper PendingIntent actions for better user experience.
- Bumped version string in settings screen.

## [0.0.53] - 2025-10-16
- Added: Hunger notification system that sends notifications for every 10% of Alex's hunger (90%, 80%, 70%, etc.).
- Added: Notifications show "[Lion Name] is hungry! He's on XX%!" with Alex lion icon.
- Added: New HungerNotificationService to handle hunger-based notifications.
- Changed: HungerManager now automatically schedules hunger notifications when hunger changes.
- Bumped version string in settings screen.

## [0.0.52] - 2025-10-16
- Changed: Notification system now only sends notification when lives reach exactly 15 (full), instead of every new live.
- Changed: Notification text updated to "Životy jsou plné!" (Lives are full!) instead of showing heart count.
- Changed: Notification icon updated from heart to Alex (lion) icon for better thematic consistency.
- Bumped version string in settings screen.

## [0.0.51] - 2025-10-15
- Changed: Tutorial overlay now centers text and places Alex at the bottom, half off-screen, spanning the full width.
- Bumped version string in settings screen.

## [0.0.50] - 2025-10-15
- Added: Alex image shown next to tutorial texts.
- Bumped version string in settings screen.

## [0.0.49] - 2025-10-15
- Added: In-app tutorial overlays shown on first open and first visit of each page.
- Bumped version string in settings screen.

## [0.0.48] - 2025-10-15
- Added: Scores chart on statistics page showing each test attempt out of 50.
- Bumped version string in settings screen.

## [0.0.47] - 2025-10-15
- Added: Persist test scores after each attempt and show average on statistics page.
- Bumped version string in settings screen.

## [0.0.46] - 2025-10-15
- Changed: Opening "Statistky" page now uses no transition animation.
- Bumped version string in settings screen.

## [0.0.45] - 2025-10-15
- Added: Clickable "Statistky" link under "Spustit test" to open a blank stats page.
- Added: New `TestAttemptStatsActivity` registered in manifest.
- Bumped version string in settings screen.

## [0.0.44] - 2025-10-15
- Fixed: Invalid/empty drawable `button_gradient.xml` causing resource parsing failure.
- Bumped version string in settings screen.

## [0.0.43] - 2025-10-09
- Changed: Sweep overlay now only covers the filled portion of Alex health bar.
- Bumped version string in settings screen.

## [0.0.42] - 2025-10-09
- Changed: Reversed Alex health bar sweep to move right→left.
- Bumped version string in settings screen.

## [0.0.41] - 2025-10-09
- Added: Animated leftward sweep overlay on Alex health bar to suggest draining.
- Bumped version string in settings screen.

## [0.0.40] - 2025-10-09
- Added: Thin rounded outline to Alex health progress bar for better visibility.
- Bumped version string in settings screen.

## [0.0.39] - 2025-10-09
- Changed: Alex page health progress bar is 3× thicker (48dp), with rounded corners and a red→yellow→light green gradient.
- Bumped version string in settings screen.

## [0.0.38] - 2025-10-09
- Changed: Alex health label food icon tinted white for better contrast.
- Bumped version string in settings screen.

## [0.0.37] - 2025-10-09
- Changed: Alex page health now shows percentage with a food icon (applied as drawable), not as a literal "@food.svg" text.
- Bumped version string in settings screen.

## [0.0.36] - 2025-10-09
- Changed: Alex page health label now shows percentage (e.g., "39% @food.svg") instead of fraction.
- Bumped version string in settings screen.

## [0.0.35] - 2025-10-09
- Added: Shop item to unlock renaming Alex (cost: 2 000 points). Once unlocked, tapping opens a rename dialog and saves the new name.
- Bumped version string in settings screen.

## [0.0.34] - 2025-10-09
- Added: After buying sunglasses, the Buy button becomes a toggle to enable/disable them.
- Changed: Alex image now switches to C-variants only when sunglasses are enabled.
- Bumped version string in settings screen.

## [0.0.33] - 2025-10-09
- Changed: Food and Shop buttons on Alex page are now square, icon-only, centered.
- Bumped version string in settings screen.

## [0.0.32] - 2025-10-09
- Changed: Centered icons on Alex page Food/Shop buttons.
- Bumped version string in settings screen.

## [0.0.31] - 2025-10-09
- Changed: Alex page buttons now use SVG icons instead of emojis.
  - Food button uses `ic_food` (from `assets/images/icons/food.svg`).
  - Shop button uses `ic_shop` (from `assets/images/icons/shop.svg`).
- Bumped version string in settings screen.

## [0.0.30] - 2025-10-09
- Fixed: Streak/Points/Lives bottom sheets now use the same icons as the top bar.
- Top bar shows numbers only; icons provided via start-drawables.
- Bumped version string in settings screen.

## [0.0.29] - 2025-10-08
- Replaced emoji headers with colorful SVG icons: streak, points, lives.
- Bottom sheets now show vector icons instead of emojis.
- Bumped version string in settings screen.

## [0.0.28] - 2025-10-08
- Bottom navigation: set to icons-only across all screens (no text labels).
- Ensured icon size is visible (24dp) everywhere.
- Bumped version string in settings screen.

## [0.0.27] - 2025-10-08
- Bottom navigation: replaced emoji titles with SVG-based icons (home, Alex, test, practice, settings).
- Set bottom nav `itemIconSize` to 24dp for visibility.
- Bumped version string in settings screen.

## [0.0.26] - 2025-10-08
- Practice points (⚡): awarded progressively during practice — 1 point per 5 unique questions viewed, even if you close before finishing all questions.
- Removed practice award from results screen to prevent double counting.
- Bumped version string in settings screen.

## [0.0.25] - 2025-10-08
- Points (⚡):
  - Test attempt awards points equal to achieved test score.
  - Practice awards 1 point per 5 questions completed.
- Bumped version string in settings screen.

## [0.0.24] - 2025-10-08
- Settings: Delete all data now also disables kamení (unfreezes hunger).
- Bumped version string in settings screen.

## [0.0.23] - 2025-10-08
- Alex hunger: decay rate now varies by current hunger level.
  - <20: 0.5 pts/hr (1 point per 2 hours)
  - 20–90: smooth ramp up
  - >90: stronger increase, up to 4 pts/hr at 100
- Settings: hunger debug displays fractional rate.
- Bumped version string in settings screen.

## [0.0.22] - 2025-10-08
- Settings: added debug box showing Alex hunger rate and kamení status.
- Exposed hunger freeze helpers in `HungerManager`.
- Bumped version string in settings screen.

## [0.0.21] - 2025-10-07
- Test mode: added 30-minute limit with countdown next to progress bar.
- Test auto-submits and shows results when time ends.
- Bumped version string in settings screen.

## [0.0.20] - 2025-10-07
- Fixed: Bottom nav Alex opens Alex page (no auto food overlay).
- Shop title now includes lion name (e.g., "Doplňky pro Alex").
- Bumped version string in settings screen.

## [0.0.19] - 2025-10-07
- Alex page: replaced single button with two buttons (🍖 food, 🛒 shop).
- Food overlay now shows only food items; sunglasses moved to dedicated shop overlay.
- Created `fragment_alex_shop_sunglasses.xml` and wired purchase flow.
- Bumped version string in settings screen.

## [0.0.18] - 2025-10-07
- Added Accessories section on Alex food page with "Sluneční brýle – 1 000 bodů".
- Purchasing sunglasses is permanent; buy button disabled after purchase.
- Alex images now switch to C-variants when sunglasses are owned.
- Bumped version string in settings screen.

## [0.0.17] - 2025-10-07
- Fixed back navigation from Alex food overlay to return to Alex page.
- Bumped version string in settings screen.

## [0.0.16] - 2025-10-07
- Reduced `alexImage` size in `fragment_alex_page_one.xml` to avoid overlapping text.
- Bumped version string in settings screen.

## [0.0.15] - 2025-10-07
- Alex images: switched to new assets in `assets/images/alex/`.
- Hunger states now map to 5 visuals: Hungry, Sad, Neutral, Happy, Cool.
- Updated image selection in `AlexActivity` and `AlexPageOneFragment`.
- Bumped version string in settings screen.

## [0.0.14] - 2025-10-07
- Kamení box: text updated to "+ 3 dny bez hladovění".
- Kamení box: cost display updated to "- 80 ⚡" (80 points).
- Bumped version string in activity settings screen.

## [0.0.13] - 2025-10-07
- Created CHANGELOG.md to start tracking what each update did.

---

Guidelines for future entries:
- Add a new section for each version: `## [x.y.z] - YYYY-MM-DD`.
- Use short bullets grouped by type if helpful (Added, Changed, Fixed, Removed).
- Keep notes developer-focused; this file lives at the repo root and is not shipped in the app.
