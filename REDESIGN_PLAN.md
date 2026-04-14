# Autoškolák — Kompletní redesign plán

> **Verze plánu:** 1.0  
> **Datum:** 2026-04-14  
> **Aktuální verze aplikace:** 2.0.15  
> **Cíl:** Moderní, hravá aplikace s glassmorphism designem, Jetpack Compose, single-activity architekturou, bohatými animacemi a gamifikací. Cílová skupina 16–25 let (Gen Z).

---

## Obsah

- [Fáze 1: Příprava projektu a infrastruktura](#fáze-1-příprava-projektu-a-infrastruktura) (kroky 1–12)
- [Fáze 2: Design systém](#fáze-2-design-systém) (kroky 13–30)
- [Fáze 3: Navigace a shell aplikace](#fáze-3-navigace-a-shell-aplikace) (kroky 31–42)
- [Fáze 4: Onboarding](#fáze-4-onboarding) (kroky 43–52)
- [Fáze 5: Home — Lesson Path](#fáze-5-home--lesson-path) (kroky 53–65)
- [Fáze 6: Quiz Experience](#fáze-6-quiz-experience) (kroky 66–85)
- [Fáze 7: Alex — Virtuální mazlíček](#fáze-7-alex--virtuální-mazlíček) (kroky 86–97)
- [Fáze 8: Test / Zkouška](#fáze-8-test--zkouška) (kroky 98–106)
- [Fáze 9: Practice / Procvičování](#fáze-9-practice--procvičování) (kroky 107–114)
- [Fáze 10: Settings a systémové obrazovky](#fáze-10-settings-a-systémové-obrazovky) (kroky 115–122)
- [Fáze 11: Gamifikace a engagement](#fáze-11-gamifikace-a-engagement) (kroky 123–137)
- [Fáze 12: Zvuky a haptika](#fáze-12-zvuky-a-haptika) (kroky 138–145)
- [Fáze 13: Finální polish a performance](#fáze-13-finální-polish-a-performance) (kroky 146–158)

---

## Přehled kroků

| # | Krok | Fáze | Hotovo |
|--:|------|------|:------:|
| 1 | Přidání Compose závislostí do build.gradle.kts | 1 | ✅ |
| 2 | Přidání Navigation Compose | 1 | ✅ |
| 3 | Přidání Lottie Compose | 1 | ✅ |
| 4 | Přidání custom fontu (Nunito / Quicksand) | 1 | ✅ |
| 5 | Přidání Accompanist a dalších utility knihoven | 1 | ✅ |
| 6 | Přidání Haze knihovny pro glassmorphism | 1 | ✅ |
| 7 | Přidání haptic feedback utility | 1 | ✅ |
| 8 | Přidání sound engine | 1 | ✅ |
| 9 | Vytvoření nové package struktury | 1 | ✅ |
| 10 | Dead code cleanup | 1 | ❌ |
| 11 | Vytvoření hlavní Compose Activity | 1 | ✅ |
| 12 | Nastavení edge-to-edge zobrazení | 1 | ✅ |
| 13 | Definice barevné palety (Dark mode) | 2 | ✅ |
| 14 | Definice barevné palety (Light mode) | 2 | ✅ |
| 15 | Compose ColorScheme a MaterialTheme | 2 | ✅ |
| 16 | Definice typografie | 2 | ✅ |
| 17 | Definice tvarů (Shapes) | 2 | ✅ |
| 18 | Rozšířený design token systém | 2 | ✅ |
| 19 | GlassCard composable | 2 | ✅ |
| 20 | GlassButton composable | 2 | ⬜ |
| 21 | PrimaryGradientButton composable | 2 | ⬜ |
| 22 | AnswerButton composable | 2 | ⬜ |
| 23 | AnimatedProgressBar composable | 2 | ⬜ |
| 24 | CircularProgress / RingProgress composable | 2 | ⬜ |
| 25 | AnimatedCounter composable | 2 | ⬜ |
| 26 | CoinPopup / FloatingReward composable | 2 | ⬜ |
| 27 | ConfettiOverlay composable | 2 | ⬜ |
| 28 | Shimmer / skeleton loading efekt | 2 | ⬜ |
| 29 | Animated background (subtle particle / gradient animation) | 2 | ⬜ |
| 30 | Lottie asset příprava | 2 | ⬜ |
| 31 | Definice navigation routes | 3 | ⬜ |
| 32 | NavGraph setup | 3 | ⬜ |
| 33 | Bottom navigation bar (animated) | 3 | ⬜ |
| 34 | Animace ikonky v bottom baru | 3 | ⬜ |
| 35 | Top app bar (streak, coins, lives) | 3 | ⬜ |
| 36 | Streak bottom sheet | 3 | ⬜ |
| 37 | Hearts / Lives bottom sheet | 3 | ⬜ |
| 38 | Coins / XP bottom sheet | 3 | ⬜ |
| 39 | App shell (Scaffold) | 3 | ⬜ |
| 40 | Tab navigation logika | 3 | ⬜ |
| 41 | Shared element transitions (příprava) | 3 | ⬜ |
| 42 | Přepojení launcher Activity na Compose | 3 | ⬜ |
| 43 | Onboarding data model | 4 | ⬜ |
| 44 | Onboarding screen (HorizontalPager) | 4 | ⬜ |
| 45 | Onboarding page content | 4 | ⬜ |
| 46 | Onboarding controls (indikátory, tlačítka) | 4 | ⬜ |
| 47 | Onboarding "Vyber si cíl" | 4 | ⬜ |
| 48 | Onboarding "Nastav denní cíl" | 4 | ⬜ |
| 49 | Onboarding "Pojmenuj lva" | 4 | ⬜ |
| 50 | Onboarding "Demo otázka" | 4 | ⬜ |
| 51 | Onboarding notifikace permission | 4 | ⬜ |
| 52 | Persistentní onboarding stav | 4 | ⬜ |
| 53 | Home screen scaffold | 5 | ⬜ |
| 54 | Lesson path Canvas křivka (pozadí) | 5 | ⬜ |
| 55 | Lesson node composable | 5 | ⬜ |
| 56 | PulsingGlow efekt | 5 | ⬜ |
| 57 | Lesson path sinusový layout | 5 | ⬜ |
| 58 | Section headers na path | 5 | ⬜ |
| 59 | Lesson info popup (BottomSheet) | 5 | ⬜ |
| 60 | Scroll to current lesson | 5 | ⬜ |
| 61 | Tutorial overlay na Home (first time) | 5 | ⬜ |
| 62 | Random event overlay (Compose) | 5 | ⬜ |
| 63 | Home ViewModel | 5 | ⬜ |
| 64 | Reading lesson screen (Compose) | 5 | ⬜ |
| 65 | Integrace se stávajícím LessonProgress | 5 | ⬜ |
| 66 | Quiz screen scaffold | 6 | ⬜ |
| 67 | Quiz top bar (progress + close + timer) | 6 | ⬜ |
| 68 | Question content layout | 6 | ⬜ |
| 69 | Quiz media (obrázky a video) | 6 | ⬜ |
| 70 | Answer selection animace | 6 | ⬜ |
| 71 | Result strip (correct/wrong panel) | 6 | ⬜ |
| 72 | Quiz ViewModel | 6 | ⬜ |
| 73 | Přechod mezi otázkami (slide animace) | 6 | ⬜ |
| 74 | Quiz close confirmation dialog | 6 | ⬜ |
| 75 | Quiz "streak" micro-interaction | 6 | ⬜ |
| 76 | Quiz "power-up" hints (budoucí rozšíření) | 6 | ⬜ |
| 77 | Správná odpověď — particle burst | 6 | ⬜ |
| 78 | Špatná odpověď — screen shake | 6 | ⬜ |
| 79 | Quiz timer animace (test mode) | 6 | ⬜ |
| 80 | Quiz life-loss animace | 6 | ⬜ |
| 81 | Results screen (lesson mode) | 6 | ⬜ |
| 82 | Results statistika s animací count-up | 6 | ⬜ |
| 83 | Streak celebration screen | 6 | ⬜ |
| 84 | Quiz question number indicator | 6 | ⬜ |
| 85 | Quiz "fun fact" po odpovědi (volitelné) | 6 | ⬜ |
| 86 | Alex screen scaffold | 7 | ⬜ |
| 87 | Alex character composable s animacemi | 7 | ⬜ |
| 88 | Hunger bar s gradient a animací | 7 | ⬜ |
| 89 | Food menu bottom sheet | 7 | ⬜ |
| 90 | Feed animace (Alex eating) | 7 | ⬜ |
| 91 | Shop bottom sheet | 7 | ⬜ |
| 92 | Alex rename dialog | 7 | ⬜ |
| 93 | Alex death screen | 7 | ⬜ |
| 94 | Alex ViewModel | 7 | ⬜ |
| 95 | Alex mood systém | 7 | ⬜ |
| 96 | Alex interakční animace (tap/swipe) | 7 | ⬜ |
| 97 | Hunger notifikace redesign | 7 | ⬜ |
| 98 | Test hub screen | 8 | ⬜ |
| 99 | Scores chart (Compose) | 8 | ⬜ |
| 100 | Test results screen | 8 | ⬜ |
| 101 | Test detail řádky | 8 | ⬜ |
| 102 | Test mode specifika v QuizScreen | 8 | ⬜ |
| 103 | Test countdown overlay | 8 | ⬜ |
| 104 | Test stats | 8 | ⬜ |
| 105 | Test attempt ViewModel | 8 | ⬜ |
| 106 | Test history | 8 | ⬜ |
| 107 | Practice screen scaffold | 9 | ⬜ |
| 108 | Category card | 9 | ⬜ |
| 109 | Practice subcategories | 9 | ⬜ |
| 110 | Practice filters | 9 | ⬜ |
| 111 | Practice quiz mode | 9 | ⬜ |
| 112 | Practice ViewModel | 9 | ⬜ |
| 113 | Practice stats | 9 | ⬜ |
| 114 | Practice search | 9 | ⬜ |
| 115 | Settings screen | 10 | ⬜ |
| 116 | Settings komponenty (Switch, Clickable) | 10 | ⬜ |
| 117 | Dark/light mode přepínač | 10 | ⬜ |
| 118 | Achievements screen (Compose) | 10 | ⬜ |
| 119 | Achievement unlock animace | 10 | ⬜ |
| 120 | Changelog screen (Compose) | 10 | ⬜ |
| 121 | Splash screen (Compose) | 10 | ⬜ |
| 122 | Loading states pro DFM | 10 | ⬜ |
| 123 | XP / leveling systém | 11 | ⬜ |
| 124 | Level-up celebration | 11 | ⬜ |
| 125 | Daily challenges | 11 | ⬜ |
| 126 | Streak freeze mechanika | 11 | ⬜ |
| 127 | Streak milestones | 11 | ⬜ |
| 128 | Power-ups implementace | 11 | ⬜ |
| 129 | Weekly leaderboard (lokální) | 11 | ⬜ |
| 130 | Achievement unlock triggers | 11 | ⬜ |
| 131 | Gamifikace — bonus wheel | 11 | ⬜ |
| 132 | Gamifikace — mystery box | 11 | ⬜ |
| 133 | Gamifikace — daily login bonus | 11 | ⬜ |
| 134 | Gamifikace — combo multiplier | 11 | ⬜ |
| 135 | Gamifikace — seasonal events | 11 | ⬜ |
| 136 | Gamifikace — avatar customization | 11 | ⬜ |
| 137 | Gamifikace — social sharing | 11 | ⬜ |
| 138 | Zvukové soubory | 12 | ⬜ |
| 139 | SoundManager implementace | 12 | ⬜ |
| 140 | Haptic patterns | 12 | ⬜ |
| 141 | Integrace zvuků do quiz flow | 12 | ⬜ |
| 142 | Integrace zvuků do Alex | 12 | ⬜ |
| 143 | Integrace zvuků do navigace | 12 | ⬜ |
| 144 | Settings: zvuky a vibrace toggle | 12 | ⬜ |
| 145 | Testování zvuků a haptic feedback | 12 | ⬜ |
| 146 | Odstranění starých Activity souborů | 13 | ⬜ |
| 147 | Odstranění starých XML layoutů | 13 | ⬜ |
| 148 | Odstranění starých stylů a témat | 13 | ⬜ |
| 149 | Performance audit — recomposition | 13 | ⬜ |
| 150 | Performance audit — animace | 13 | ⬜ |
| 151 | Performance audit — image loading | 13 | ⬜ |
| 152 | Accessibility audit | 13 | ⬜ |
| 153 | Reduced motion support | 13 | ⬜ |
| 154 | Tablet / landscape support (základní) | 13 | ⬜ |
| 155 | ProGuard / R8 pravidla pro nové knihovny | 13 | ⬜ |
| 156 | App size audit | 13 | ⬜ |
| 157 | Migrace ad logiky do Compose | 13 | ⬜ |
| 158 | Finální QA a release checklist | 13 | ⬜ |

---

## Fáze 1: Příprava projektu a infrastruktura

### Krok 1 — Přidání Compose závislostí do build.gradle.kts

**Soubory:** `app/build.gradle.kts`, `gradle/libs.versions.toml`

1. V `libs.versions.toml` aktualizovat `composeBom` na nejnovější stabilní verzi (2026.x).
2. V `app/build.gradle.kts`:
   - Přidat plugin `alias(libs.plugins.kotlin.compose)`.
   - Do bloku `android {}` přidat `buildFeatures { compose = true }`.
   - Přidat závislosti:
     ```kotlin
     implementation(platform(libs.androidx.compose.bom))
     implementation(libs.androidx.ui)
     implementation(libs.androidx.ui.graphics)
     implementation(libs.androidx.ui.tooling.preview)
     implementation(libs.androidx.material3)
     implementation(libs.androidx.activity.compose)
     debugImplementation(libs.androidx.ui.tooling)
     debugImplementation(libs.androidx.ui.test.manifest)
     ```
3. Ověřit, že `jvmTarget` je kompatibilní (11 nebo 17 — doporučeno 17 pro Compose 2026).
4. Sync Gradle a ověřit build.

**Výstup:** Projekt buildí s Compose podporou, obě technologie (XML i Compose) koexistují.

---

### Krok 2 — Přidání Navigation Compose

**Soubory:** `app/build.gradle.kts`, `libs.versions.toml`

1. Přidat do version catalogu:
   ```toml
   navigationCompose = "2.9.x"  # nejnovější stabilní
   ```
   ```toml
   androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
   ```
2. V `app/build.gradle.kts`:
   ```kotlin
   implementation(libs.androidx.navigation.compose)
   ```
3. Sync a ověřit build.

**Výstup:** Navigation Compose je k dispozici pro definici NavGraph.

---

### Krok 3 — Přidání Lottie Compose

**Soubory:** `app/build.gradle.kts`

1. Přidat závislost:
   ```kotlin
   implementation("com.airbnb.android:lottie-compose:6.x.x")  // nejnovější
   ```
2. Vytvořit složku `app/src/main/assets/lottie/` pro budoucí Lottie JSON soubory.
3. Sync a ověřit build.

**Výstup:** Lottie Compose připraveno k použití.

---

### Krok 4 — Přidání custom fontu (Nunito / Quicksand)

**Soubory:** `app/src/main/res/font/`

1. Stáhnout font rodinu — doporučuji **Quicksand** (hravý, rounded, výborná čitelnost):
   - `quicksand_light.ttf` (300)
   - `quicksand_regular.ttf` (400)
   - `quicksand_medium.ttf` (500)
   - `quicksand_semibold.ttf` (600)
   - `quicksand_bold.ttf` (700)
2. Vložit do `app/src/main/res/font/`.
3. Alternativa: Použít Google Fonts downloadable fonts v Compose (`GoogleFont` provider), ale lokální je spolehlivější offline.

**Výstup:** Font soubory jsou v projektu, připraveny k registraci v Compose `Typography`.

---

### Krok 5 — Přidání Accompanist a dalších utility knihoven

**Soubory:** `app/build.gradle.kts`

1. Přidat knihovny (pokud nejsou součástí Compose BOM):
   ```kotlin
   // System UI controller (status bar, nav bar barvy)
   implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.x")
   // Pager (pokud není v core compose foundation)
   // Animated navigation
   implementation("androidx.compose.animation:animation:x.x.x")  // z BOM
   ```
2. Zvážit přidání:
   ```kotlin
   // Coil pro async image loading v Compose
   implementation("io.coil-kt:coil-compose:3.x.x")
   // Haze pro glassmorphism blur efekty
   implementation("dev.chrisbanes.haze:haze:1.x.x")
   ```
3. Sync a ověřit build.

**Výstup:** Všechny utility knihovny pro moderní Compose UI jsou k dispozici.

---

### Krok 6 — Přidání Haze knihovny pro glassmorphism

**Soubory:** `app/build.gradle.kts`

1. Přidat [Haze](https://github.com/chrisbanes/haze) — nativní Compose blur:
   ```kotlin
   implementation("dev.chrisbanes.haze:haze:1.x.x")
   implementation("dev.chrisbanes.haze:haze-materials:1.x.x")
   ```
2. Haze podporuje `Modifier.haze()` (zdroj rozmazání) a `Modifier.hazeChild()` (element s blur efektem) — ideální pro glassmorphism karty.
3. Ověřit kompatibilitu s minSdk 24 (Haze fallbackuje na solid color na starších API).

**Výstup:** Glassmorphism blur efekty jsou technicky možné.

---

### Krok 7 — Přidání haptic feedback utility

**Soubory:** Nový soubor `app/src/main/java/cz/autokolk/ui/util/HapticFeedback.kt`

1. Vytvořit utility objekt pro centralizovanou haptiku:
   ```kotlin
   object HapticFeedback {
       fun light(view: View) { ... }      // lehký tap
       fun medium(view: View) { ... }     // potvrzení
       fun heavy(view: View) { ... }      // chyba
       fun success(view: View) { ... }    // správná odpověď
       fun error(view: View) { ... }      // špatná odpověď
       fun streak(view: View) { ... }     // streak milestone
   }
   ```
2. Využít `HapticFeedbackConstants` (API 30+) s fallbackem na `Vibrator` pro starší API.
3. V Compose vytvořit `LocalHapticFeedback` CompositionLocal nebo extension na `View` z `LocalView.current`.

**Výstup:** Centralizovaný haptic feedback systém připravený k použití na libovolné obrazovce.

---

### Krok 8 — Přidání sound engine

**Soubory:** Nový soubor `app/src/main/java/cz/autokolk/audio/SoundManager.kt`, nová složka `app/src/main/res/raw/`

1. Vytvořit `SoundManager` singleton:
   ```kotlin
   object SoundManager {
       private lateinit var soundPool: SoundPool
       enum class Sound { CORRECT, WRONG, STREAK, COIN, TAP, LEVELUP, COUNTDOWN }
       fun init(context: Context) { ... }
       fun play(sound: Sound) { ... }
       fun setEnabled(enabled: Boolean) { ... }
   }
   ```
2. Využít `SoundPool` pro nízkolatenční přehrávání krátkých efektů.
3. Zatím vytvořit placeholder — skutečné zvukové soubory budou přidány ve Fázi 12.

**Výstup:** Sound engine je připraven, zvuky budou doplněny později.

---

### Krok 9 — Vytvoření nové package struktury

**Soubory:** Nové složky pod `app/src/main/java/cz/autokolk/`

1. Vytvořit novou package strukturu pro Compose kód:
   ```
   cz/autokolk/
   ├── ui/
   │   ├── theme/           # Compose theme, barvy, typografie
   │   ├── components/      # Sdílené UI komponenty
   │   │   ├── glass/       # Glassmorphism komponenty
   │   │   ├── buttons/     # Tlačítka
   │   │   ├── navigation/  # Bottom bar, top bar
   │   │   ├── progress/    # Progress bary, ringy
   │   │   ├── animation/   # Reusable animace
   │   │   └── feedback/    # Snackbar, toasty, overlays
   │   ├── screens/
   │   │   ├── home/        # Home + lesson path
   │   │   ├── alex/        # Alex stránka
   │   │   ├── quiz/        # Otázky (lesson + test mode)
   │   │   ├── test/        # Test hub + stats
   │   │   ├── practice/    # Procvičování
   │   │   ├── settings/    # Nastavení
   │   │   ├── onboarding/  # Onboarding flow
   │   │   ├── results/     # Výsledky lekce/testu
   │   │   ├── streak/      # Streak celebration
   │   │   └── achievements/# Achievementy
   │   └── navigation/      # NavGraph, route definitions
   ├── audio/                # SoundManager
   ├── data/                 # Existující data modely
   └── util/                 # Utility funkce
   ```
2. Stávající kód v `cz/autokolk/autokolk/` nechat — bude postupně nahrazován.

**Výstup:** Čistá package struktura pro nový kód.

---

### Krok 10 — Dead code cleanup ⏭️ PŘESKOČENO

**Soubory k odstranění:**
- `CurvyPathView.kt` — nepoužívaný custom view
- `AlexPagerAdapter.kt` — nepoužívaný adapter
- `res/layout/curvy_lesson_path.xml` — nepoužívaný layout
- `res/layout/item_lesson_curvy.xml` — nepoužívaný layout
- `res/layout/item_info_button.xml` — nepoužívaný layout
- `res/layout/item_category_header.xml` — ověřit použití, případně smazat
- `res/layout/item_subcategory_header.xml` — ověřit použití, případně smazat

1. Před smazáním každého souboru udělat grep v celém projektu, zda opravdu není referencován.
2. Smazat identifikované mrtvé soubory.
3. Build ověřit.

**Výstup:** Codebase zbavený legacy kódu.

---

### Krok 11 — Vytvoření hlavní Compose Activity

**Soubory:** Nový `app/src/main/java/cz/autokolk/ComposeMainActivity.kt`, úprava `AndroidManifest.xml`

1. Vytvořit novou `ComposeMainActivity`:
   ```kotlin
   class ComposeMainActivity : ComponentActivity() {
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           enableEdgeToEdge()
           setContent {
               AutokolkTheme {
                   AutokolkApp()
               }
           }
       }
   }
   ```
2. Zatím **nepřesouvat** launcher intent — stará `LoadingActivity` zůstává entry point.
3. `ComposeMainActivity` bude aktivována až po dokončení Compose shellu.
4. V manifestu přidat `ComposeMainActivity` bez launcher filtru.

**Výstup:** Entry point pro Compose UI existuje, ale není ještě aktivní.

---

### Krok 12 — Nastavení edge-to-edge zobrazení

**Soubory:** `ComposeMainActivity.kt`, theme

1. V `ComposeMainActivity.onCreate()` volat `enableEdgeToEdge()` z `androidx.activity`.
2. V Compose theme nastavit průhledný status bar a navigation bar:
   ```kotlin
   val systemUiController = rememberSystemUiController()
   SideEffect {
       systemUiController.setSystemBarsColor(
           color = Color.Transparent,
           darkIcons = !isDarkTheme
       )
   }
   ```
3. Použít `Modifier.systemBarsPadding()` nebo `WindowInsets` pro obsah.
4. Odstranit `windowOptOutEdgeToEdgeEnforcement` z `values-v35/themes.xml` (ten byl jen workaround).

**Výstup:** Aplikace využívá plnou obrazovku, obsah se vykresluje pod systémovými bary.

---

## Fáze 2: Design systém

### Krok 13 — Definice barevné palety (Dark mode)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/theme/Color.kt`

1. Definovat tmavou paletu inspirovanou glassmorphism:
   ```kotlin
   // Backgrounds
   val DarkBackground = Color(0xFF0A0E21)      // Hluboká temně modrá (ne čistá černá)
   val DarkSurface = Color(0xFF1A1F36)          // Lehce světlejší povrch
   val DarkSurfaceVariant = Color(0xFF252A42)   // Karty, elevated surfaces
   
   // Glass efekty
   val GlassWhite = Color(0x1AFFFFFF)           // 10% bílá pro glass borders
   val GlassFill = Color(0x0DFFFFFF)            // 5% bílá pro glass fill
   val GlassHighlight = Color(0x33FFFFFF)        // 20% pro hover/active
   
   // Akcentové barvy (modro-zelená)
   val AccentCyan = Color(0xFF00E5FF)           // Hlavní akcent — cyan
   val AccentTeal = Color(0xFF1DE9B6)           // Sekundární — teal
   val AccentBlue = Color(0xFF2979FF)           // Terciární — modrá
   val AccentGradientStart = Color(0xFF00E5FF)  // Gradient start
   val AccentGradientEnd = Color(0xFF1DE9B6)    // Gradient end
   
   // Semantic
   val SuccessGreen = Color(0xFF00E676)
   val ErrorRed = Color(0xFFFF1744)
   val WarningAmber = Color(0xFFFFD600)
   val InfoBlue = Color(0xFF2196F3)
   
   // Text
   val TextPrimary = Color(0xFFFFFFFF)
   val TextSecondary = Color(0xB3FFFFFF)        // 70% bílá
   val TextTertiary = Color(0x80FFFFFF)         // 50% bílá
   ```
2. Barvy by měly být dostatečně kontrastní pro WCAG AA na tmavém pozadí.

**Výstup:** Kompletní dark palette definovaná v kódu.

---

### Krok 14 — Definice barevné palety (Light mode)

**Soubory:** `app/src/main/java/cz/autokolk/ui/theme/Color.kt`

1. Definovat světlou paletu:
   ```kotlin
   // Backgrounds
   val LightBackground = Color(0xFFF0F4FF)      // Jemně modrý off-white
   val LightSurface = Color(0xFFFFFFFF)          // Čistá bílá pro karty
   val LightSurfaceVariant = Color(0xFFE8EEFF)   // Lehce tónovaná
   
   // Glass efekty (light)
   val LightGlassFill = Color(0x80FFFFFF)        // Poloprůhledná bílá
   val LightGlassBorder = Color(0x33000000)      // Jemný tmavý border
   
   // Akcentové barvy — stejné nebo mírně tlumenější
   val LightAccentCyan = Color(0xFF00B8D4)
   val LightAccentTeal = Color(0xFF00BFA5)
   
   // Text
   val LightTextPrimary = Color(0xFF1A1F36)
   val LightTextSecondary = Color(0xFF4A5568)
   ```
2. Akcentové barvy by měly zůstat rozpoznatelné i na světlém pozadí.

**Výstup:** Light palette hotová. Uživatel bude moci přepínat v nastavení.

---

### Krok 15 — Compose ColorScheme a MaterialTheme

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/theme/Theme.kt`

1. Vytvořit `darkColorScheme()` a `lightColorScheme()` s Material3 rolemi:
   ```kotlin
   private val DarkColors = darkColorScheme(
       primary = AccentCyan,
       secondary = AccentTeal,
       tertiary = AccentBlue,
       background = DarkBackground,
       surface = DarkSurface,
       surfaceVariant = DarkSurfaceVariant,
       onPrimary = DarkBackground,
       onBackground = TextPrimary,
       onSurface = TextPrimary,
       error = ErrorRed,
   )
   ```
2. Vytvořit `AutokolkTheme` composable:
   ```kotlin
   @Composable
   fun AutokolkTheme(
       darkTheme: Boolean = isSystemInDarkTheme(),
       content: @Composable () -> Unit
   ) {
       val colorScheme = if (darkTheme) DarkColors else LightColors
       MaterialTheme(
           colorScheme = colorScheme,
           typography = AutokolkTypography,
           shapes = AutokolkShapes,
           content = content
       )
   }
   ```
3. Přidat `CompositionLocal` pro uživatelskou preferenci dark/light mode (z SharedPreferences).

**Výstup:** Plně funkční Compose theme s přepínáním dark/light.

---

### Krok 16 — Definice typografie

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/theme/Type.kt`

1. Vytvořit Quicksand `FontFamily`:
   ```kotlin
   val Quicksand = FontFamily(
       Font(R.font.quicksand_light, FontWeight.Light),
       Font(R.font.quicksand_regular, FontWeight.Normal),
       Font(R.font.quicksand_medium, FontWeight.Medium),
       Font(R.font.quicksand_semibold, FontWeight.SemiBold),
       Font(R.font.quicksand_bold, FontWeight.Bold),
   )
   ```
2. Definovat `Typography` s hierarchií:
   ```kotlin
   val AutokolkTypography = Typography(
       displayLarge  = TextStyle(fontFamily = Quicksand, fontWeight = Bold, fontSize = 34.sp),
       headlineLarge = TextStyle(fontFamily = Quicksand, fontWeight = Bold, fontSize = 28.sp),
       headlineMedium = TextStyle(fontFamily = Quicksand, fontWeight = SemiBold, fontSize = 24.sp),
       titleLarge    = TextStyle(fontFamily = Quicksand, fontWeight = SemiBold, fontSize = 20.sp),
       titleMedium   = TextStyle(fontFamily = Quicksand, fontWeight = Medium, fontSize = 16.sp),
       bodyLarge     = TextStyle(fontFamily = Quicksand, fontWeight = Normal, fontSize = 16.sp),
       bodyMedium    = TextStyle(fontFamily = Quicksand, fontWeight = Normal, fontSize = 14.sp),
       labelLarge    = TextStyle(fontFamily = Quicksand, fontWeight = SemiBold, fontSize = 14.sp),
       labelMedium   = TextStyle(fontFamily = Quicksand, fontWeight = Medium, fontSize = 12.sp),
   )
   ```
3. Všechny text styly budou automaticky dostupné přes `MaterialTheme.typography`.

**Výstup:** Jednotná typografie s hravým rounded fontem.

---

### Krok 17 — Definice tvarů (Shapes)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/theme/Shape.kt`

1. Definovat zaoblené tvary vhodné pro glassmorphism:
   ```kotlin
   val AutokolkShapes = Shapes(
       extraSmall = RoundedCornerShape(8.dp),
       small = RoundedCornerShape(12.dp),
       medium = RoundedCornerShape(16.dp),
       large = RoundedCornerShape(24.dp),
       extraLarge = RoundedCornerShape(32.dp),
   )
   ```
2. Přidat custom shapes pro specifické případy:
   ```kotlin
   val PillShape = RoundedCornerShape(50)           // Plně zaoblené pilulky
   val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
   val LessonNodeShape = CircleShape                 // Lesson path uzly
   ```

**Výstup:** Konzistentní zaoblení napříč celou aplikací.

---

### Krok 18 — Rozšířený design token systém

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/theme/Tokens.kt`

1. Vytvořit object `AutokolkTokens` s konstantami mimo MaterialTheme:
   ```kotlin
   object AutokolkTokens {
       // Elevation
       val ElevationNone = 0.dp
       val ElevationLow = 2.dp
       val ElevationMedium = 8.dp
       val ElevationHigh = 16.dp
       
       // Spacing
       val SpacingXs = 4.dp
       val SpacingSm = 8.dp
       val SpacingMd = 16.dp
       val SpacingLg = 24.dp
       val SpacingXl = 32.dp
       val SpacingXxl = 48.dp
       
       // Glass properties
       val GlassBlurRadius = 20.dp
       val GlassBorderWidth = 1.dp
       val GlassOpacity = 0.1f
       
       // Animation durations
       val AnimFast = 150
       val AnimNormal = 300
       val AnimSlow = 500
       val AnimVerySlow = 800
       
       // Sizes
       val LessonNodeSize = 64.dp
       val LessonNodeSizeLarge = 80.dp
       val BottomBarHeight = 72.dp
       val TopBarHeight = 56.dp
       val IconSizeSm = 20.dp
       val IconSizeMd = 24.dp
       val IconSizeLg = 32.dp
   }
   ```

**Výstup:** Centralizované design tokeny pro konzistenci.

---

### Krok 19 — GlassCard composable

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/glass/GlassCard.kt`

1. Vytvořit základní glassmorphism kartu:
   ```kotlin
   @Composable
   fun GlassCard(
       modifier: Modifier = Modifier,
       shape: Shape = AutokolkShapes.medium,
       borderGradient: List<Color> = listOf(GlassWhite, Color.Transparent),
       content: @Composable () -> Unit
   ) {
       Box(
           modifier = modifier
               .clip(shape)
               .background(
                   brush = Brush.linearGradient(
                       colors = listOf(
                           GlassFill,
                           GlassFill.copy(alpha = 0.02f)
                       ),
                       start = Offset(0f, 0f),
                       end = Offset.Infinite
                   )
               )
               .border(
                   width = AutokolkTokens.GlassBorderWidth,
                   brush = Brush.linearGradient(borderGradient),
                   shape = shape
               )
       ) {
           content()
       }
   }
   ```
2. Pro skutečný blur efekt (obsah za kartou je rozmazaný) použít Haze:
   ```kotlin
   @Composable
   fun GlassCardBlur(
       hazeState: HazeState,
       modifier: Modifier = Modifier,
       ...
   ) {
       Box(
           modifier = modifier
               .hazeChild(hazeState, style = HazeMaterials.thin())
               .clip(shape)
               .border(...)
       ) { content() }
   }
   ```
3. Oba varianty — `GlassCard` (fake glass bez blur, levnější) a `GlassCardBlur` (skutečný blur, dražší na render).

**Výstup:** Reusable glassmorphism komponenta.

---

### Krok 20 — GlassButton composable

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/buttons/GlassButton.kt`

1. Vytvořit tlačítko s glass efektem a animovaným presnutím:
   ```kotlin
   @Composable
   fun GlassButton(
       onClick: () -> Unit,
       modifier: Modifier = Modifier,
       enabled: Boolean = true,
       content: @Composable RowScope.() -> Unit
   ) {
       val interactionSource = remember { MutableInteractionSource() }
       val isPressed by interactionSource.collectIsPressedAsState()
       val scale by animateFloatAsState(
           targetValue = if (isPressed) 0.95f else 1f,
           animationSpec = spring(dampingRatio = 0.6f)
       )
       
       Box(
           modifier = modifier
               .scale(scale)
               .clip(PillShape)
               .background(GlassFill)
               .border(GlassBorderWidth, GlassWhite, PillShape)
               .clickable(interactionSource, indication = rememberRipple(), onClick = onClick)
               .padding(horizontal = 24.dp, vertical = 12.dp)
       ) {
           Row(content = content)
       }
   }
   ```
2. Přidat varianty:
   - `PrimaryButton` — gradient fill (AccentCyan → AccentTeal), bílý text.
   - `SecondaryButton` — glass fill, akcentový text.
   - `DangerButton` — červený gradient.
3. Všechna tlačítka mají `spring()` press animaci (scale down + bounce back).

**Výstup:** Sada animovaných tlačítek konzistentních s glassmorphism designem.

---

### Krok 21 — PrimaryGradientButton composable

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/buttons/PrimaryGradientButton.kt`

1. Hlavní CTA tlačítko s gradient pozadím a glow efektem:
   ```kotlin
   @Composable
   fun PrimaryGradientButton(
       text: String,
       onClick: () -> Unit,
       modifier: Modifier = Modifier,
       icon: ImageVector? = null,
       enabled: Boolean = true,
   ) {
       // Press animace + subtle glow shadow
       val interactionSource = remember { MutableInteractionSource() }
       val isPressed by interactionSource.collectIsPressedAsState()
       val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, spring(0.5f))
       
       Box(
           modifier = modifier
               .scale(scale)
               .shadow(
                   elevation = if (isPressed) 4.dp else 12.dp,
                   shape = PillShape,
                   ambientColor = AccentCyan.copy(alpha = 0.3f),
                   spotColor = AccentTeal.copy(alpha = 0.3f),
               )
               .clip(PillShape)
               .background(
                   Brush.horizontalGradient(listOf(AccentCyan, AccentTeal))
               )
               .clickable(interactionSource, null, enabled, onClick = onClick)
               .padding(horizontal = 32.dp, vertical = 16.dp),
           contentAlignment = Alignment.Center
       ) {
           Row(verticalAlignment = Alignment.CenterVertically) {
               if (icon != null) {
                   Icon(icon, null, tint = DarkBackground)
                   Spacer(Modifier.width(8.dp))
               }
               Text(text, color = DarkBackground, fontWeight = FontWeight.Bold)
           }
       }
   }
   ```
2. Glow efekt: `shadow()` s akcentovou barvou vytváří neonový záblesk pod tlačítkem.
3. Nepovinný shimmer efekt na idle (nekonečná animace světelného pruhu přes gradient).

**Výstup:** Vizuálně výrazné CTA tlačítko s glow a animací.

---

### Krok 22 — AnswerButton composable

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/buttons/AnswerButton.kt`

1. Tlačítko pro odpovědi v quizu se 4 stavy:
   - **Default** — glass card, bílý text.
   - **Selected** — zvýrazněný border, mírný scale-up.
   - **Correct** — zelený gradient + ✓ ikona + pulse animace + konfety particles.
   - **Wrong** — červený gradient + ✗ ikona + shake animace.
   ```kotlin
   @Composable
   fun AnswerButton(
       text: String,
       state: AnswerState,      // DEFAULT, SELECTED, CORRECT, WRONG
       label: String,            // "A", "B", "C"
       onClick: () -> Unit,
       modifier: Modifier = Modifier,
   ) {
       val shakeOffset = remember { Animatable(0f) }
       val borderColor by animateColorAsState(
           when (state) {
               CORRECT -> SuccessGreen
               WRONG -> ErrorRed
               SELECTED -> AccentCyan
               DEFAULT -> GlassWhite
           }
       )
       // Shake animace pro WRONG
       LaunchedEffect(state) {
           if (state == WRONG) {
               repeat(3) {
                   shakeOffset.animateTo(10f, tween(50))
                   shakeOffset.animateTo(-10f, tween(50))
               }
               shakeOffset.animateTo(0f, spring())
           }
       }
       ...
   }
   ```
2. Label ("A", "B", "C") v kruhovém badge vlevo, text vpravo.
3. Přechod mezi stavy animovaný (barva, border, ikona).

**Výstup:** Interaktivní answer button s bohatými animacemi.

---

### Krok 23 — AnimatedProgressBar composable

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/progress/AnimatedProgressBar.kt`

1. Horizontální progress bar s gradient fill a animací:
   ```kotlin
   @Composable
   fun AnimatedProgressBar(
       progress: Float,     // 0f..1f
       modifier: Modifier = Modifier,
       gradient: List<Color> = listOf(AccentCyan, AccentTeal),
       height: Dp = 8.dp,
       animated: Boolean = true,
   ) {
       val animatedProgress by animateFloatAsState(
           targetValue = progress,
           animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f)
       )
       Canvas(modifier.height(height).fillMaxWidth().clip(PillShape)) {
           // Track
           drawRoundRect(color = GlassWhite, cornerRadius = CornerRadius(height.toPx()))
           // Fill
           drawRoundRect(
               brush = Brush.horizontalGradient(gradient),
               size = Size(size.width * animatedProgress, size.height),
               cornerRadius = CornerRadius(height.toPx())
           )
           // Glow dot na konci
           val dotX = size.width * animatedProgress
           drawCircle(color = gradient.last(), radius = height.toPx(), center = Offset(dotX, size.height / 2))
       }
   }
   ```
2. Varianty:
   - Hunger bar (zelená → žlutá → červená dle hodnoty).
   - Quiz progress (AccentCyan → AccentTeal).
   - XP progress (zlatá).

**Výstup:** Universální progress bar s plynulou animací.

---

### Krok 24 — CircularProgress / RingProgress composable

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/progress/RingProgress.kt`

1. Nahradit stávající `RingProgressDrawable.kt` Compose variantou:
   ```kotlin
   @Composable
   fun RingProgress(
       progress: Float,
       modifier: Modifier = Modifier,
       strokeWidth: Dp = 4.dp,
       gradient: List<Color> = listOf(AccentCyan, AccentTeal),
       trackColor: Color = GlassWhite,
       size: Dp = 64.dp,
       content: @Composable () -> Unit = {}
   ) {
       val sweep by animateFloatAsState(progress * 360f, spring(0.8f))
       Box(modifier.size(size), contentAlignment = Alignment.Center) {
           Canvas(Modifier.matchParentSize()) {
               drawArc(trackColor, 0f, 360f, false, style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round))
               drawArc(
                   brush = Brush.sweepGradient(gradient),
                   startAngle = -90f,
                   sweepAngle = sweep,
                   useCenter = false,
                   style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
               )
           }
           content()
       }
   }
   ```
2. Bude použit na lesson path uzlech, Alex hunger, achievement progress.

**Výstup:** Animovaný kruhový progress indikátor.

---

### Krok 25 — AnimatedCounter composable

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/animation/AnimatedCounter.kt`

1. Animovaný čítač pro coins, XP, streak — čísla se "přetáčejí" jako na letištní tabuli:
   ```kotlin
   @Composable
   fun AnimatedCounter(
       targetValue: Int,
       modifier: Modifier = Modifier,
       style: TextStyle = MaterialTheme.typography.titleMedium,
       color: Color = TextPrimary,
   ) {
       var oldValue by remember { mutableIntStateOf(targetValue) }
       val animatedValue by animateIntAsState(
           targetValue = targetValue,
           animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
       )
       
       // Každá číslice animovaná zvlášť (slide up/down)
       Row(modifier) {
           animatedValue.toString().forEachIndexed { index, char ->
               AnimatedContent(
                   targetState = char,
                   transitionSpec = {
                       slideInVertically { -it } + fadeIn() togetherWith
                       slideOutVertically { it } + fadeOut()
                   }
               ) { digit ->
                   Text(digit.toString(), style = style, color = color)
               }
           }
       }
       
       LaunchedEffect(targetValue) { oldValue = targetValue }
   }
   ```
2. Použití: coins v top baru, XP na results, streak číslo.

**Výstup:** Vizuálně atraktivní animace čísel.

---

### Krok 26 — CoinPopup / FloatingReward composable

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/animation/FloatingReward.kt`

1. Nahradit stávající `coin_popup.xml` Compose animací:
   ```kotlin
   @Composable
   fun FloatingReward(
       visible: Boolean,
       amount: Int,
       icon: ImageVector,           // coin, heart, star
       color: Color = WarningAmber,
       onDismiss: () -> Unit,
   ) {
       AnimatedVisibility(
           visible = visible,
           enter = slideInVertically(initialOffsetY = { it }) + fadeIn() + scaleIn(initialScale = 0.5f),
           exit = slideOutVertically(targetOffsetY = { -it * 2 }) + fadeOut()
       ) {
           Row(
               modifier = Modifier
                   .background(GlassFill, PillShape)
                   .border(GlassBorderWidth, GlassWhite, PillShape)
                   .padding(horizontal = 16.dp, vertical = 8.dp),
               verticalAlignment = Alignment.CenterVertically
           ) {
               Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
               Spacer(Modifier.width(4.dp))
               Text("+$amount", fontWeight = FontWeight.Bold, color = color)
           }
       }
       
       // Auto-dismiss po 2s
       LaunchedEffect(visible) {
           if (visible) { delay(2000); onDismiss() }
       }
   }
   ```
2. Popup se vynoří zdola, letí nahoru a zmizí — efekt "sbírání" bodů.

**Výstup:** Animovaný popup pro odměny.

---

### Krok 27 — ConfettiOverlay composable

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/animation/ConfettiOverlay.kt`

1. Nahradit stávající `ConfettiView.kt` Compose implementací:
   ```kotlin
   @Composable
   fun ConfettiOverlay(
       isActive: Boolean,
       particleCount: Int = 100,
       colors: List<Color> = listOf(AccentCyan, AccentTeal, WarningAmber, ErrorRed, SuccessGreen),
       durationMs: Int = 3000,
   ) {
       if (!isActive) return
       val particles = remember { generateParticles(particleCount, colors) }
       val progress = remember { Animatable(0f) }
       
       LaunchedEffect(Unit) {
           progress.animateTo(1f, tween(durationMs, easing = LinearEasing))
       }
       
       Canvas(Modifier.fillMaxSize()) {
           particles.forEach { p ->
               val t = progress.value
               val x = p.startX + p.velocityX * t
               val y = p.startY + p.velocityY * t + 0.5f * p.gravity * t * t
               val rotation = p.rotationSpeed * t
               rotate(rotation, Offset(x, y)) {
                   drawRect(p.color, Offset(x, y), Size(p.width, p.height))
               }
           }
       }
   }
   ```
2. Podpora různých tvarů (obdélníky, kroužky, hvězdičky).
3. Použití: streak celebration, achievement unlock, test pass.

**Výstup:** GPU-akcelerovaný konfetový efekt.

---

### Krok 28 — Shimmer / skeleton loading efekt

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/animation/ShimmerEffect.kt`

1. Vytvořit `Modifier.shimmer()` extension:
   ```kotlin
   fun Modifier.shimmer(
       isLoading: Boolean = true,
       highlightColor: Color = GlassHighlight,
       baseColor: Color = GlassFill,
   ): Modifier = composed {
       if (!isLoading) return@composed this
       val transition = rememberInfiniteTransition()
       val offset by transition.animateFloat(
           initialValue = -1f,
           targetValue = 2f,
           animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing))
       )
       this.drawWithContent {
           drawContent()
           val brush = Brush.linearGradient(
               colors = listOf(baseColor, highlightColor, baseColor),
               start = Offset(size.width * offset, 0f),
               end = Offset(size.width * (offset + 1), size.height),
           )
           drawRect(brush)
       }
   }
   ```
2. Použití: skeleton UI při načítání dat, loading overlay u obrázků.

**Výstup:** Plynulý shimmer efekt pro loading states.

---

### Krok 29 — Animated background (subtle particle / gradient animation)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/animation/AnimatedBackground.kt`

1. Pozadí celé aplikace nebude statické — jemný pohyblivý gradient:
   ```kotlin
   @Composable
   fun AnimatedBackground(
       modifier: Modifier = Modifier,
       content: @Composable () -> Unit
   ) {
       val infiniteTransition = rememberInfiniteTransition()
       val offset by infiniteTransition.animateFloat(
           initialValue = 0f,
           targetValue = 1f,
           animationSpec = infiniteRepeatable(
               tween(10000, easing = LinearEasing),
               RepeatMode.Reverse
           )
       )
       
       Box(modifier.background(
           Brush.radialGradient(
               colors = listOf(
                   AccentCyan.copy(alpha = 0.05f),
                   DarkBackground,
                   AccentTeal.copy(alpha = 0.03f),
               ),
               center = Offset(offset * 1000f, offset * 1500f),
               radius = 800f
           )
       )) {
           content()
       }
   }
   ```
2. Efekt je velmi subtilní (5% opacity), ale dodá pocit "živosti".
3. Alternativa: pomalý pohyb 2–3 velkých rozmazaných kruhů (bokeh).

**Výstup:** Dynamické pozadí aplikace, které nikdy nepůsobí staticky.

---

### Krok 30 — Lottie asset příprava

**Soubory:** `app/src/main/assets/lottie/`

1. Připravit a stáhnout (nebo vytvořit) klíčové Lottie animace:
   - `splash_loading.json` — loading spinner pro splash screen (např. auto jedoucí po silnici).
   - `correct_answer.json` — zelené zatržítko s particles.
   - `wrong_answer.json` — červený křížek s shake.
   - `streak_fire.json` — animovaný plamen.
   - `coin_burst.json` — explodující mince.
   - `heart_pulse.json` — pulzující srdce.
   - `achievement_unlock.json` — odemknutí achievementu.
   - `level_up.json` — level up efekt.
   - `confetti.json` — konfety.
   - `empty_state.json` — prázdný stav (např. pro "žádné výsledky").
2. Zdroje: LottieFiles.com (bezplatné s atribucí) nebo vlastní tvorba.
3. Doporučená velikost: max 50kB na animaci (optimalizované).

**Výstup:** Klíčové Lottie animace připraveny k použití.

---

## Fáze 3: Navigace a shell aplikace

### Krok 31 — Definice navigation routes

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/navigation/Routes.kt`

1. Vytvořit sealed class/object pro type-safe routes:
   ```kotlin
   sealed class Route(val route: String) {
       object Splash : Route("splash")
       object Onboarding : Route("onboarding")
       object Home : Route("home")
       object Alex : Route("alex")
       object Test : Route("test")
       object TestStats : Route("test/stats")
       object Practice : Route("practice")
       object Settings : Route("settings")
       object Achievements : Route("achievements")
       object Changelog : Route("changelog")
       object Streak : Route("streak")
       object AlexDeath : Route("alex/death")
       
       // Parametrické routes
       data class Quiz(val lessonId: Int = -1, val isTest: Boolean = false, val categoryId: Int = -1) 
           : Route("quiz/{lessonId}/{isTest}/{categoryId}")
       data class ReadingLesson(val lessonId: Int) : Route("reading/{lessonId}")
       data class Results(val lessonId: Int, val score: Int, val total: Int) 
           : Route("results/{lessonId}/{score}/{total}")
       data class TestResults(val testId: Int) : Route("test/results/{testId}")
   }
   ```
2. Pro Compose Navigation 2.8+ zvážit použití Kotlin Serialization pro type-safe args.

**Výstup:** Definovaný routing systém.

---

### Krok 32 — NavGraph setup

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/navigation/NavGraph.kt`

1. Vytvořit centrální `NavHost`:
   ```kotlin
   @Composable
   fun AutokolkNavGraph(
       navController: NavHostController,
       startDestination: String = Route.Splash.route,
   ) {
       NavHost(
           navController = navController,
           startDestination = startDestination,
           enterTransition = { fadeIn(tween(300)) + slideInHorizontally { it / 4 } },
           exitTransition = { fadeOut(tween(300)) + slideOutHorizontally { -it / 4 } },
           popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally { -it / 4 } },
           popExitTransition = { fadeOut(tween(300)) + slideOutHorizontally { it / 4 } },
       ) {
           composable(Route.Splash.route) { SplashScreen(navController) }
           composable(Route.Onboarding.route) { OnboardingScreen(navController) }
           // Main tabs — bez přechodové animace (instant switch)
           composable(Route.Home.route, enterTransition = { fadeIn(tween(150)) }, ...) { HomeScreen(navController) }
           composable(Route.Alex.route, ...) { AlexScreen(navController) }
           composable(Route.Test.route, ...) { TestScreen(navController) }
           composable(Route.Practice.route, ...) { PracticeScreen(navController) }
           composable(Route.Settings.route, ...) { SettingsScreen(navController) }
           // Detail screens
           composable("quiz/{lessonId}/{isTest}/{categoryId}", arguments = ...) { ... }
           ...
       }
   }
   ```
2. Tab přechody: fade (bez slide) — instant feel.
3. Detail přechody: slide in/out s fade.
4. Modální: slide up from bottom (streak, results).

**Výstup:** Kompletní navigační graf.

---

### Krok 33 — Bottom navigation bar (animated)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/navigation/BottomNavBar.kt`

1. Custom bottom navigation bar s glassmorphism stylem:
   ```kotlin
   @Composable
   fun AutokolkBottomBar(
       currentRoute: String,
       onNavigate: (Route) -> Unit,
       modifier: Modifier = Modifier,
   ) {
       val items = listOf(
           BottomNavItem(Route.Home, Icons.Home, "Domů"),
           BottomNavItem(Route.Alex, Icons.Alex, "Alex"),
           BottomNavItem(Route.Test, Icons.Test, "Zkouška"),
           BottomNavItem(Route.Practice, Icons.Practice, "Praxe"),
           BottomNavItem(Route.Settings, Icons.Settings, "Více"),
       )
       
       GlassCard(
           modifier = modifier
               .fillMaxWidth()
               .padding(horizontal = 16.dp, bottom = 8.dp),
           shape = PillShape,
       ) {
           Row(
               Modifier.fillMaxWidth().padding(vertical = 8.dp),
               horizontalArrangement = Arrangement.SpaceEvenly,
               verticalAlignment = Alignment.CenterVertically,
           ) {
               items.forEach { item ->
                   BottomNavItemView(
                       item = item,
                       isSelected = currentRoute == item.route.route,
                       onClick = { onNavigate(item.route) }
                   )
               }
           }
       }
   }
   ```
2. **Animace ikonek:**
   - Vybraná: bounce scale (1.0 → 1.2 → 1.0), ikona se zabarví akcentem, label slide-in zdola.
   - Nevybraná: scale down na 0.9, šedá barva, label schovaný.
   - Přechod: `animateFloatAsState` se spring physics.
3. **Active indicator:** Jemný glow pod vybranou ikonou (akcentový kruh s blur).
4. Bar je "plovoucí" — margins od krajů, zaoblený, glass background.

**Výstup:** Moderní animovaný bottom bar odlišný od standardního Material NavigationBar.

---

### Krok 34 — Animace ikonky v bottom baru

**Soubory:** Součást `BottomNavBar.kt`

1. Každá ikonka má 3 animované vlastnosti při výběru:
   ```kotlin
   @Composable
   private fun BottomNavItemView(item: BottomNavItem, isSelected: Boolean, onClick: () -> Unit) {
       val scale by animateFloatAsState(
           if (isSelected) 1.15f else 1f,
           spring(dampingRatio = 0.5f, stiffness = 800f)
       )
       val iconColor by animateColorAsState(
           if (isSelected) AccentCyan else TextSecondary,
           tween(200)
       )
       val labelAlpha by animateFloatAsState(
           if (isSelected) 1f else 0f, tween(200)
       )
       
       Column(
           Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
               onClick()
           },
           horizontalAlignment = Alignment.CenterHorizontally,
       ) {
           Box(contentAlignment = Alignment.Center) {
               // Glow circle za ikonou
               if (isSelected) {
                   Box(Modifier.size(40.dp).background(AccentCyan.copy(alpha = 0.15f), CircleShape))
               }
               Icon(item.icon, null, Modifier.size(24.dp).scale(scale), tint = iconColor)
           }
           AnimatedVisibility(isSelected, enter = slideInVertically { it } + fadeIn()) {
               Text(item.label, style = labelSmall, color = iconColor, modifier = Modifier.padding(top = 2.dp))
           }
       }
   }
   ```

**Výstup:** Ikonky "skáčou" při výběru s glow efektem.

---

### Krok 35 — Top app bar (streak, coins, lives)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/navigation/TopBar.kt`

1. Glassmorphism top bar se třemi stat ukazateli:
   ```kotlin
   @Composable
   fun AutokolkTopBar(
       streak: Int,
       coins: Int,
       lives: Int,
       onStreakClick: () -> Unit,
       onCoinsClick: () -> Unit,
       onLivesClick: () -> Unit,
       modifier: Modifier = Modifier,
   ) {
       Row(
           modifier = modifier
               .fillMaxWidth()
               .statusBarsPadding()
               .padding(horizontal = 16.dp, vertical = 8.dp),
           horizontalArrangement = Arrangement.SpaceBetween,
       ) {
           StatBadge(
               icon = painterResource(R.drawable.ic_streak),
               value = streak,
               color = WarningAmber,
               onClick = onStreakClick,
           )
           StatBadge(
               icon = painterResource(R.drawable.ic_coin),
               value = coins,
               color = WarningAmber,
               onClick = onCoinsClick,
           )
           StatBadge(
               icon = painterResource(R.drawable.ic_heart),
               value = lives,
               color = ErrorRed,
               onClick = onLivesClick,
               pulse = lives <= 1,  // pulsuje při málo životech
           )
       }
   }
   ```
2. `StatBadge` — glass pill s ikonou a `AnimatedCounter`:
   ```kotlin
   @Composable
   private fun StatBadge(icon, value, color, onClick, pulse = false) {
       val pulseScale by rememberInfiniteTransition().animateFloat(
           1f, 1.1f, infiniteRepeatable(tween(800), RepeatMode.Reverse)
       )
       GlassButton(onClick = onClick) {
           Icon(icon, null, Modifier.size(20.dp).scale(if (pulse) pulseScale else 1f), tint = color)
           Spacer(Modifier.width(6.dp))
           AnimatedCounter(value, color = TextPrimary)
       }
   }
   ```
3. Lives badge pulsuje červeně, když má uživatel 1 nebo 0 životů — urgentní vizuální signál.

**Výstup:** Animovaný top bar s live-updating statistikami.

---

### Krok 36 — Streak bottom sheet

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/sheets/StreakSheet.kt`

1. Material3 `ModalBottomSheet` s glass pozadím:
   ```kotlin
   @Composable
   fun StreakSheet(
       isVisible: Boolean,
       streak: Int,
       streakHistory: List<Boolean>,  // posledních 7 dní
       onDismiss: () -> Unit,
       onWatchAd: () -> Unit,
   ) {
       if (isVisible) {
           ModalBottomSheet(
               onDismissRequest = onDismiss,
               containerColor = DarkSurfaceVariant,
               shape = BottomSheetShape,
           ) {
               Column(Modifier.padding(24.dp), horizontalAlignment = CenterHorizontally) {
                   // Lottie flame animace
                   LottieAnimation(composition, iterations = LottieConstants.IterateForever, Modifier.size(80.dp))
                   Spacer(Modifier.height(8.dp))
                   AnimatedCounter(streak, style = displayLarge, color = WarningAmber)
                   Text("dní v řadě!", style = titleMedium, color = TextSecondary)
                   Spacer(Modifier.height(24.dp))
                   // 7-denní heatmap
                   StreakWeekRow(streakHistory)
                   Spacer(Modifier.height(24.dp))
                   // "Ochrana streak" — reklama
                   if (shouldShowProtection) {
                       PrimaryGradientButton("Ochránit streak", onClick = onWatchAd, icon = Icons.Shield)
                   }
               }
           }
       }
   }
   ```
2. 7-denní řada: řada kruhů (vyplněný = splněno, prázdný = nesplněno, dnešek zvýrazněný).

**Výstup:** Moderní streak sheet s Lottie animací a heatmapou.

---

### Krok 37 — Hearts / Lives bottom sheet

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/sheets/HeartsSheet.kt`

1. Zobrazení aktuálních životů s animací:
   ```kotlin
   @Composable
   fun HeartsSheet(
       isVisible: Boolean,
       lives: Int,
       maxLives: Int,
       nextHeartIn: Duration?,
       onDismiss: () -> Unit,
       onWatchAd: () -> Unit,
   ) {
       // ... ModalBottomSheet ...
       // Řada srdíček — plná/prázdná s pulse animací
       Row {
           repeat(maxLives) { index ->
               val isFull = index < lives
               val scale by animateFloatAsState(if (isFull) 1f else 0.7f)
               Icon(
                   if (isFull) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                   null,
                   tint = if (isFull) ErrorRed else TextTertiary,
                   modifier = Modifier.size(32.dp).scale(scale)
               )
           }
       }
       // Odpočet do dalšího srdce
       if (nextHeartIn != null) {
           Text("Další ❤️ za ${nextHeartIn.format()}", color = TextSecondary)
       }
       // Tlačítko "Získat život za reklamu"
       if (lives < maxLives) {
           PrimaryGradientButton("Získat život", onClick = onWatchAd, icon = Icons.PlayCircle)
       }
   }
   ```

**Výstup:** Vizuálně bohatý sheet pro životové mechaniky.

---

### Krok 38 — Coins / XP bottom sheet

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/sheets/CoinsSheet.kt`

1. Jednoduchý sheet ukazující celkové body a jak je získat:
   ```kotlin
   // Coin icon s animací rotace
   // AnimatedCounter pro celkový počet
   // Seznam "Jak získat body":
   //   - Dokončit lekci: +10
   //   - Bezchybná lekce: +20
   //   - Denní streak: +5
   //   - Nakrmit Alexe: -5 (útraty)
   ```

**Výstup:** Informativní coins sheet.

---

### Krok 39 — App shell (Scaffold)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/AutokolkApp.kt`

1. Hlavní composable obalující celou aplikaci:
   ```kotlin
   @Composable
   fun AutokolkApp() {
       val navController = rememberNavController()
       val currentRoute by navController.currentBackStackEntryAsState()
       val route = currentRoute?.destination?.route
       
       val showBottomBar = route in listOf(
           Route.Home.route, Route.Alex.route, Route.Test.route,
           Route.Practice.route, Route.Settings.route
       )
       val showTopBar = showBottomBar
       
       Scaffold(
           bottomBar = {
               AnimatedVisibility(showBottomBar, enter = slideInVertically { it }, exit = slideOutVertically { it }) {
                   AutokolkBottomBar(route ?: "", onNavigate = { navController.navigateToTab(it) })
               }
           },
           containerColor = Color.Transparent,
       ) { paddingValues ->
           AnimatedBackground {
               Column {
                   if (showTopBar) {
                       AutokolkTopBar(streak, coins, lives, ...)
                   }
                   Box(Modifier.padding(paddingValues)) {
                       AutokolkNavGraph(navController)
                   }
               }
           }
       }
   }
   ```
2. Top bar a bottom bar se animovaně skryjí na detail obrazovkách (quiz, results, streak).
3. `navigateToTab` — helper s `launchSingleTop = true` a `popUpTo(home) { inclusive = false }`.

**Výstup:** Hlavní shell s animovanými systémovými bary.

---

### Krok 40 — Tab navigation logika

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/navigation/NavigationExtensions.kt`

1. Extension funkce pro tab navigaci:
   ```kotlin
   fun NavHostController.navigateToTab(route: Route) {
       navigate(route.route) {
           popUpTo(Route.Home.route) { saveState = true }
           launchSingleTop = true
           restoreState = true
       }
   }
   ```
2. Tím se zabrání hromadění back stacku při přepínání tabů.
3. Back button z non-Home tabu vždy vrátí na Home.

**Výstup:** Korektní tab navigace bez stack leaku.

---

### Krok 41 — Shared element transitions (příprava)

**Soubory:** Žádné nové soubory — přípravný krok

1. Compose Navigation 2.8+ podporuje `SharedTransitionLayout` a `sharedElement()` modifier.
2. Identifikovat páry pro shared element transitions:
   - **Lesson node** (Home) → **Quiz header** (detail) — ikona lekce.
   - **Alex obrázek** (Alex page) → **Alex obrázek** (AlexDeath).
   - **Achievement card** → **Achievement detail**.
3. V dalších krocích implementovat na konkrétních přechodech.

**Výstup:** Plán shared element přechodů.

---

### Krok 42 — Přepojení launcher Activity na Compose

**Soubory:** `AndroidManifest.xml`, `ComposeMainActivity.kt`, `LoadingActivity.kt`

1. Přesunout launcher intent filter na `ComposeMainActivity`.
2. Loading/splash logiku (DFM install, consent) přesunout do Compose `SplashScreen` composable.
3. Ponechat `LoadingActivity.kt` jako fallback (nebo smazat po migraci).
4. Manifest:
   ```xml
   <activity android:name=".ComposeMainActivity"
       android:exported="true"
       android:theme="@style/Theme.Autokolk.NoActionBar"
       android:screenOrientation="portrait">
       <intent-filter>
           <action android:name="android.intent.action.MAIN" />
           <category android:name="android.intent.category.LAUNCHER" />
       </intent-filter>
   </activity>
   ```
5. Odebrat launcher filter z `LoadingActivity`.

**Výstup:** Aplikace startuje do Compose UI.

---

## Fáze 4: Onboarding

### Krok 43 — Onboarding data model

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/onboarding/OnboardingData.kt`

1. Definovat pages onboardingu:
   ```kotlin
   data class OnboardingPage(
       val title: String,
       val description: String,
       val lottieRes: String,    // cesta k Lottie JSON v assets
       val accentColor: Color,
   )
   
   val onboardingPages = listOf(
       OnboardingPage("Vítej v Autoškoláku!", "Připrav se na zkoušku hravě a rychle.", "lottie/onboarding_welcome.json", AccentCyan),
       OnboardingPage("Tohle je Alex", "Tvůj lev, který potřebuje tvoji pomoc. Uč se a krmí ho!", "lottie/onboarding_alex.json", AccentTeal),
       OnboardingPage("Sbírej body", "Za každou lekci získáš body a prodloužíš svůj streak.", "lottie/onboarding_points.json", WarningAmber),
       OnboardingPage("Zvládni zkoušku", "Až budeš připraven, vyzkoušej si ostrý test.", "lottie/onboarding_test.json", AccentBlue),
   )
   ```

**Výstup:** Datový model pro 4-stránkový onboarding.

---

### Krok 44 — Onboarding screen (HorizontalPager)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/onboarding/OnboardingScreen.kt`

1. Implementovat paginated onboarding:
   ```kotlin
   @Composable
   fun OnboardingScreen(navController: NavHostController) {
       val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
       val scope = rememberCoroutineScope()
       
       Box(Modifier.fillMaxSize()) {
           // Animated background per page
           AnimatedBackground(accentColor = onboardingPages[pagerState.currentPage].accentColor)
           
           Column(Modifier.fillMaxSize().systemBarsPadding()) {
               // Pager
               HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                   OnboardingPageContent(onboardingPages[page])
               }
               // Indicators + buttons
               OnboardingControls(pagerState, scope, navController)
           }
       }
   }
   ```

**Výstup:** Swipovatelný onboarding.

---

### Krok 45 — Onboarding page content

**Soubory:** Součást `OnboardingScreen.kt`

1. Každá stránka:
   ```kotlin
   @Composable
   private fun OnboardingPageContent(page: OnboardingPage) {
       Column(
           Modifier.fillMaxSize().padding(32.dp),
           horizontalAlignment = CenterHorizontally,
           verticalArrangement = Arrangement.Center,
       ) {
           // Lottie animace (velká, 250dp)
           val composition by rememberLottieComposition(LottieCompositionSpec.Asset(page.lottieRes))
           LottieAnimation(composition, iterations = IterateForever, Modifier.size(250.dp))
           
           Spacer(Modifier.height(32.dp))
           
           Text(page.title, style = headlineLarge, textAlign = TextAlign.Center)
           Spacer(Modifier.height(12.dp))
           Text(page.description, style = bodyLarge, color = TextSecondary, textAlign = TextAlign.Center)
       }
   }
   ```

**Výstup:** Vizuálně bohatá onboarding stránka.

---

### Krok 46 — Onboarding controls (indikátory, tlačítka)

**Soubory:** Součást `OnboardingScreen.kt`

1. Spodní ovládací prvky:
   ```kotlin
   @Composable
   private fun OnboardingControls(pagerState: PagerState, scope: CoroutineScope, navController: NavHostController) {
       Column(Modifier.padding(24.dp), horizontalAlignment = CenterHorizontally) {
           // Dot indicators s animací
           Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
               repeat(onboardingPages.size) { index ->
                   val width by animateDpAsState(if (index == pagerState.currentPage) 24.dp else 8.dp)
                   val color by animateColorAsState(
                       if (index == pagerState.currentPage) onboardingPages[index].accentColor else GlassWhite
                   )
                   Box(Modifier.height(8.dp).width(width).clip(PillShape).background(color))
               }
           }
           Spacer(Modifier.height(24.dp))
           
           if (pagerState.currentPage == onboardingPages.lastIndex) {
               PrimaryGradientButton("Začít!", onClick = {
                   // uložit onboarding_completed = true
                   navController.navigate(Route.Home.route) { popUpTo(Route.Onboarding.route) { inclusive = true } }
               })
           } else {
               Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                   TextButton(onClick = { /* skip */ }) { Text("Přeskočit", color = TextSecondary) }
                   PrimaryGradientButton("Další", onClick = {
                       scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                   })
               }
           }
       }
   }
   ```
2. Aktivní dot se prodlouží na "pilulku" (animovaná šířka).

**Výstup:** Kompletní ovládání onboardingu.

---

### Krok 47 — Onboarding "Vyber si cíl"

**Soubory:** Součást onboarding flow

1. Přidat 5. stránku: výběr řidičského oprávnění (B, A, C, T...):
   ```kotlin
   OnboardingPage("Co chceš řídit?", "Vyber si skupinu oprávnění", ...)
   ```
2. Grid 2×2 nebo 3×2 s ikonami vozidel, selectable chips.
3. Výběr ovlivní filtraci otázek (pokud je v datech).

**Výstup:** Personalizovaný onboarding.

---

### Krok 48 — Onboarding "Nastav denní cíl"

**Soubory:** Součást onboarding flow

1. Přidat 6. stránku: denní cíl (kolik lekcí denně):
   - 🐢 Pohoda (1 lekce/den)
   - 🐇 Normální (3 lekce/den)
   - 🔥 Intenzivní (5 lekcí/den)
   - 💀 Šílený (10 lekcí/den)
2. Animated selection — vybraný řádek se zvětší, glow efekt.
3. Cíl se uloží a ovlivní streak/notifikační logiku.

**Výstup:** Uživatel si nastaví tempo.

---

### Krok 49 — Onboarding "Pojmenuj lva"

**Soubory:** Součást onboarding flow

1. Přidat 7. stránku: pojmenování Alexe:
   - Velký obrázek Alexe (happy).
   - Text field pro jméno (výchozí "Alex").
   - Animace: Alex reaguje na každý napsaný znak (bounce, happy face).
2. Uložit jméno do SharedPreferences / data modelu.

**Výstup:** Emocionální vazba na maskota od začátku.

---

### Krok 50 — Onboarding "Demo otázka"

**Soubory:** Součást onboarding flow

1. Přidat 8. stránku: ukázková otázka:
   - Jednoduchá otázka ze skutečných dat.
   - Plná UI s AnswerButtons.
   - Po odpovědi: "Vidíš? To zvládneš!" + confetti pokud správně.
2. Tutorial overlay vysvětlující UI prvky (streak, coins, lives).

**Výstup:** Uživatel si okamžitě vyzkouší produkt.

---

### Krok 51 — Onboarding notifikace permission

**Soubory:** Součást onboarding flow

1. Na konci onboardingu (Android 13+) požádat o notifikace:
   - Vysvětlující text: "Pošleme ti reminder, abys nepřišel o streak!"
   - Hezké UI (ne systémový dialog rovnou).
   - Pak teprve spustit systémový permission request.

**Výstup:** Vyšší opt-in rate díky kontextu.

---

### Krok 52 — Persistentní onboarding stav

**Soubory:** SharedPreferences / DataStore

1. Uložit:
   - `onboarding_completed: Boolean`
   - `selected_license_type: String`
   - `daily_goal: Int`
   - `lion_name: String`
2. V `AutokolkApp` kontrolovat: pokud `!onboarding_completed`, startDestination = `Route.Onboarding`.
3. Nabídnout "replay onboarding" v Settings.

**Výstup:** Onboarding se zobrazí jen jednou.

---

## Fáze 5: Home — Lesson Path

### Krok 53 — Home screen scaffold

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/home/HomeScreen.kt`

1. Základní layout:
   ```kotlin
   @Composable
   fun HomeScreen(navController: NavHostController) {
       val lessons = remember { LessonProgress.getGlobalLessonPlan() }
       val scrollState = rememberLazyListState()
       
       LazyColumn(
           state = scrollState,
           modifier = Modifier.fillMaxSize(),
           contentPadding = PaddingValues(bottom = 100.dp), // space for bottom bar
           horizontalAlignment = Alignment.CenterHorizontally,
       ) {
           // Section headers + lesson nodes
           lessons.forEachIndexed { index, lesson ->
               item(key = lesson.id) {
                   LessonPathNode(lesson, index, navController)
               }
           }
       }
   }
   ```

**Výstup:** Základní scrollovatelná lesson path stránka.

---

### Krok 54 — Lesson path Canvas křivka (pozadí)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/home/LessonPathCanvas.kt`

1. Vytvořit křivku spojující lesson nodes na pozadí:
   ```kotlin
   @Composable
   fun LessonPathBackground(
       nodePositions: List<Offset>,  // pozice center jednotlivých nodes
       modifier: Modifier = Modifier,
   ) {
       Canvas(modifier.fillMaxSize()) {
           if (nodePositions.size < 2) return@Canvas
           val path = Path()
           path.moveTo(nodePositions[0].x, nodePositions[0].y)
           
           for (i in 1 until nodePositions.size) {
               val prev = nodePositions[i - 1]
               val curr = nodePositions[i]
               val midY = (prev.y + curr.y) / 2
               // S-křivka (kubický Bézier)
               path.cubicTo(prev.x, midY, curr.x, midY, curr.x, curr.y)
           }
           
           // Dashed track line
           drawPath(path, color = GlassWhite, style = Stroke(
               width = 4.dp.toPx(),
               pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
           ))
           
           // Filled progress overlay (gradient)
           val progressPath = /* subsekce cesty do aktuálního nodu */
           drawPath(progressPath, brush = Brush.verticalGradient(listOf(AccentCyan, AccentTeal)),
               style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
       }
   }
   ```
2. Čárkovaná šedá linka pro nedokončené, plný gradient pro dokončené.
3. Aktuální node má glow efekt.

**Výstup:** Vizuální "cestička" à la Duolingo.

---

### Krok 55 — Lesson node composable

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/home/LessonNode.kt`

1. Každý lesson node:
   ```kotlin
   @Composable
   fun LessonNode(
       lesson: LessonInfo,
       state: LessonNodeState,  // LOCKED, CURRENT, COMPLETED, PERFECT
       sectionHue: Float,
       onClick: () -> Unit,
       modifier: Modifier = Modifier,
   ) {
       val scale by animateFloatAsState(
           when (state) {
               CURRENT -> 1.15f
               else -> 1f
           },
           spring(0.5f)
       )
       
       Box(modifier.size(AutokolkTokens.LessonNodeSize).scale(scale), contentAlignment = Alignment.Center) {
           // Ring progress
           if (state == COMPLETED || state == PERFECT) {
               RingProgress(progress = lesson.progress, gradient = gradientForHue(sectionHue))
           }
           
           // Node circle
           val bgColor = when (state) {
               LOCKED -> GlassFill
               CURRENT -> AccentCyan
               COMPLETED -> SuccessGreen.copy(alpha = 0.8f)
               PERFECT -> WarningAmber
           }
           Box(
               Modifier
                   .size(56.dp)
                   .clip(CircleShape)
                   .background(bgColor)
                   .clickable(enabled = state != LOCKED, onClick = onClick)
                   .then(if (state == CURRENT) Modifier.glowBorder(AccentCyan) else Modifier),
               contentAlignment = Alignment.Center,
           ) {
               when (state) {
                   LOCKED -> Icon(Icons.Lock, null, tint = TextTertiary)
                   CURRENT -> Icon(painterResource(lesson.iconRes), null, tint = DarkBackground)
                   COMPLETED -> Icon(Icons.Check, null, tint = DarkBackground)
                   PERFECT -> Text("⭐", fontSize = 20.sp)
               }
           }
           
           // Pulsující glow pro current
           if (state == CURRENT) {
               PulsingGlow(color = AccentCyan, size = 72.dp)
           }
       }
   }
   ```

**Výstup:** Vizuálně odlišené lesson nodes s animacemi.

---

### Krok 56 — PulsingGlow efekt

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/animation/PulsingGlow.kt`

1. Nekonečný pulse efekt pro zvýraznění aktuální lekce:
   ```kotlin
   @Composable
   fun PulsingGlow(color: Color, size: Dp, modifier: Modifier = Modifier) {
       val transition = rememberInfiniteTransition()
       val scale by transition.animateFloat(1f, 1.4f, infiniteRepeatable(tween(1500), RepeatMode.Reverse))
       val alpha by transition.animateFloat(0.4f, 0f, infiniteRepeatable(tween(1500), RepeatMode.Reverse))
       
       Box(
           modifier
               .size(size)
               .scale(scale)
               .background(color.copy(alpha = alpha), CircleShape)
       )
   }
   ```

**Výstup:** Upoutání pozornosti na aktuální úkol.

---

### Krok 57 — Lesson path sinusový layout

**Soubory:** Součást `HomeScreen.kt`

1. Nodes nejsou ve vertikální linii — pohybují se sinusoidálně:
   ```kotlin
   items(lessons.size) { index ->
       val waveOffset = sin(index * 0.8f) * 80.dp.value  // dp offset
       Box(
           Modifier
               .fillMaxWidth()
               .padding(vertical = 8.dp)
               .offset(x = waveOffset.dp),
           contentAlignment = Alignment.Center
       ) {
           LessonNode(...)
       }
   }
   ```
2. Wave amplituda a frekvence mohou záviset na sekci.
3. Koordináty nodes reportovat přes `onGloballyPositioned` pro Canvas křivku.

**Výstup:** Organicky zvlněná cesta.

---

### Krok 58 — Section headers na path

**Soubory:** Součást `HomeScreen.kt`

1. Mezi skupinami lekcí vložit section headers:
   ```kotlin
   // "Základní pojmy" — velký text + emoji + glass card pozadí
   GlassCard(Modifier.fillMaxWidth(0.8f).padding(vertical = 16.dp)) {
       Column(Modifier.padding(16.dp), horizontalAlignment = CenterHorizontally) {
           Text("🚗 Základní pojmy", style = headlineMedium)
           Text("12/15 dokončeno", style = bodyMedium, color = TextSecondary)
           AnimatedProgressBar(progress = 12f / 15f, Modifier.padding(top = 8.dp))
       }
   }
   ```
2. Každá sekce má vlastní barvu/hue pro vizuální odlišení.

**Výstup:** Strukturovaný přehled postupu.

---

### Krok 59 — Lesson info popup (BottomSheet místo PopupWindow)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/home/LessonInfoSheet.kt`

1. Nahradit stávající `PopupWindow` za `ModalBottomSheet`:
   ```kotlin
   @Composable
   fun LessonInfoSheet(
       lesson: LessonInfo,
       isVisible: Boolean,
       onDismiss: () -> Unit,
       onStart: () -> Unit,
   ) {
       if (isVisible) {
           ModalBottomSheet(onDismissRequest = onDismiss, containerColor = DarkSurfaceVariant) {
               Column(Modifier.padding(24.dp)) {
                   // Ikona + název
                   Row(verticalAlignment = Alignment.CenterVertically) {
                       RingProgress(lesson.progress, size = 48.dp) {
                           Icon(painterResource(lesson.iconRes), null, Modifier.size(24.dp))
                       }
                       Spacer(Modifier.width(16.dp))
                       Column {
                           Text(lesson.title, style = titleLarge)
                           Text(lesson.category, style = bodyMedium, color = TextSecondary)
                       }
                   }
                   Spacer(Modifier.height(16.dp))
                   // Statistiky
                   Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                       StatItem("Otázek", "${lesson.questionCount}")
                       StatItem("Nejlepší", "${lesson.bestScore}%")
                       StatItem("Pokusů", "${lesson.attempts}")
                   }
                   Spacer(Modifier.height(24.dp))
                   PrimaryGradientButton("Začít lekci", onClick = onStart, Modifier.fillMaxWidth())
               }
           }
       }
   }
   ```

**Výstup:** Moderní lesson info dialog.

---

### Krok 60 — Scroll to current lesson

**Soubory:** Součást `HomeScreen.kt`

1. Při prvním zobrazení (nebo reselectu Home tabu) automaticky scrollnout na aktuální lekci:
   ```kotlin
   LaunchedEffect(Unit) {
       val currentIndex = lessons.indexOfFirst { it.state == CURRENT }
       if (currentIndex >= 0) {
           scrollState.animateScrollToItem(currentIndex, scrollOffset = -200)
       }
   }
   ```
2. Scroll animace s `FastOutSlowInEasing`.

**Výstup:** Uživatel vždy vidí, kde je.

---

### Krok 61 — Tutorial overlay na Home (first time)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/feedback/TutorialOverlay.kt`

1. Poloprůhledný overlay se spotlight na konkrétní UI element:
   ```kotlin
   @Composable
   fun TutorialOverlay(
       targetBounds: Rect,       // pozice zvýrazněného elementu
       message: String,
       onDismiss: () -> Unit,
   ) {
       Box(Modifier.fillMaxSize()) {
           // Tmavý overlay s "dírou" u target
           Canvas(Modifier.fillMaxSize()) {
               drawRect(Color.Black.copy(alpha = 0.7f))
               // Vykrojit díru
               drawRoundRect(
                   color = Color.Transparent,
                   topLeft = targetBounds.topLeft - Offset(8f, 8f),
                   size = targetBounds.size + Size(16f, 16f),
                   cornerRadius = CornerRadius(16f),
                   blendMode = BlendMode.Clear
               )
           }
           // Tooltip pod/nad dírou
           GlassCard(Modifier.align(/* pod dírou */).padding(24.dp)) {
               Text(message, style = bodyLarge)
               Spacer(Modifier.height(8.dp))
               PrimaryGradientButton("OK, rozumím!", onClick = onDismiss)
           }
       }
   }
   ```
2. Postupně zvýraznit: top bar → lesson node → bottom bar.

**Výstup:** Kontextový tutorial pro nové uživatele.

---

### Krok 62 — Random event overlay (Compose)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/components/feedback/EventOverlay.kt`

1. Migrovat `EventStyleOverlay` do Compose:
   ```kotlin
   @Composable
   fun EventOverlay(
       event: RandomEvent?,
       onDismiss: () -> Unit,
   ) {
       AnimatedVisibility(event != null, enter = fadeIn() + scaleIn(initialScale = 0.8f)) {
           Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).clickable { onDismiss() }) {
               GlassCard(Modifier.align(Alignment.Center).padding(32.dp)) {
                   Column(Modifier.padding(24.dp), horizontalAlignment = CenterHorizontally) {
                       LottieAnimation(/* event animace */)
                       Text(event?.title ?: "", style = headlineMedium)
                       Text(event?.message ?: "", style = bodyLarge, color = TextSecondary)
                       Text(event?.reward ?: "", style = titleLarge, color = WarningAmber)
                       Spacer(Modifier.height(16.dp))
                       PrimaryGradientButton("Super!", onClick = onDismiss)
                   }
               }
               // Konfety na pozadí
               ConfettiOverlay(isActive = event != null)
           }
       }
   }
   ```

**Výstup:** Animovaný event overlay s konfety.

---

### Krok 63 — Home ViewModel

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/home/HomeViewModel.kt`

1. Vytvořit ViewModel pro oddělení business logiky od UI:
   ```kotlin
   class HomeViewModel : ViewModel() {
       private val _lessons = MutableStateFlow<List<LessonInfo>>(emptyList())
       val lessons: StateFlow<List<LessonInfo>> = _lessons.asStateFlow()
       
       private val _currentEvent = MutableStateFlow<RandomEvent?>(null)
       val currentEvent: StateFlow<RandomEvent?> = _currentEvent
       
       private val _selectedLesson = MutableStateFlow<LessonInfo?>(null)
       val selectedLesson: StateFlow<LessonInfo?> = _selectedLesson
       
       init { loadLessons() }
       
       fun selectLesson(lesson: LessonInfo) { _selectedLesson.value = lesson }
       fun dismissLesson() { _selectedLesson.value = null }
       fun dismissEvent() { _currentEvent.value = null }
       
       private fun loadLessons() { /* z LessonProgress */ }
   }
   ```
2. V `HomeScreen` observovat přes `collectAsStateWithLifecycle()`.

**Výstup:** Čistá architektura s MVVM.

---

### Krok 64 — Reading lesson screen (Compose)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/home/ReadingLessonScreen.kt`

1. Migrovat `ReadingLessonActivity` do Compose:
   ```kotlin
   @Composable
   fun ReadingLessonScreen(lessonId: Int, navController: NavHostController) {
       val pages = remember { loadReadingPages(lessonId) }
       val pagerState = rememberPagerState(pageCount = { pages.size })
       
       Column(Modifier.fillMaxSize().systemBarsPadding()) {
           // Progress bar
           AnimatedProgressBar(progress = (pagerState.currentPage + 1f) / pages.size)
           
           // Page content
           HorizontalPager(state = pagerState) { pageIndex ->
               Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
                   // Obrázek s fade-in
                   AsyncImage(pages[pageIndex].imageUrl, null, Modifier.fillMaxWidth().clip(AutokolkShapes.medium))
                   Spacer(Modifier.height(16.dp))
                   Text(pages[pageIndex].text, style = bodyLarge)
               }
           }
           
           // Tlačítko
           PrimaryGradientButton(
               if (pagerState.currentPage == pages.lastIndex) "Začít kvíz" else "Další",
               onClick = { /* page++ nebo navigate to quiz */ },
               Modifier.fillMaxWidth().padding(24.dp)
           )
       }
   }
   ```
2. Přechod mezi stránkami: slide + fade animace.

**Výstup:** Čtecí lekce v Compose.

---

### Krok 65 — Integrace se stávajícím LessonProgress

**Soubory:** Úpravy v data vrstvě

1. `LessonProgress` (stávající singleton/helper) adaptovat pro Compose:
   - Přidat `Flow` / `StateFlow` variabty pro reaktivní observování.
   - Nebo obalit `SharedPreferences` do `DataStore` a emitovat `Flow`.
2. Vytvořit mapper: `LessonProgress` → `LessonInfo` (UI model pro Home).
3. Zajistit, že staré Activity kód může koexistovat s novým Compose kódem (sdílený data layer).

**Výstup:** Data propojení starého a nového kódu.

---

## Fáze 6: Quiz Experience

### Krok 66 — Quiz screen scaffold

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/quiz/QuizScreen.kt`

1. Hlavní composable pro kvízový zážitek (nahrazuje `MainActivity`):
   ```kotlin
   @Composable
   fun QuizScreen(
       lessonId: Int,
       isTestMode: Boolean,
       categoryId: Int,
       navController: NavHostController,
       viewModel: QuizViewModel = viewModel()
   ) {
       val state by viewModel.state.collectAsStateWithLifecycle()
       
       Box(Modifier.fillMaxSize()) {
           AnimatedBackground()
           
           Column(Modifier.fillMaxSize().systemBarsPadding()) {
               // Quiz top bar (close, progress, timer)
               QuizTopBar(
                   progress = state.progress,
                   onClose = { /* confirm dialog */ },
                   timer = if (isTestMode) state.remainingTime else null,
               )
               
               // Otázka content s AnimatedContent pro přechody
               AnimatedContent(
                   targetState = state.currentQuestion,
                   transitionSpec = { slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut() }
               ) { question ->
                   QuestionContent(question, state, viewModel)
               }
           }
           
           // Result strip (correct/wrong) overlay zdola
           QuizResultStrip(state.lastResult, onNext = { viewModel.nextQuestion() })
           
           // Floating reward popup
           FloatingReward(state.showCoinPopup, state.coinAmount, Icons.Star, onDismiss = { viewModel.dismissPopup() })
       }
   }
   ```

**Výstup:** Hlavní quiz container.

---

### Krok 67 — Quiz top bar (progress + close + timer)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/quiz/QuizTopBar.kt`

1. Horní lišta během kvízu:
   ```kotlin
   @Composable
   fun QuizTopBar(
       progress: Float,
       onClose: () -> Unit,
       timer: Duration? = null,
       onInfoClick: (() -> Unit)? = null,
   ) {
       Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
           // X button
           IconButton(onClick = onClose) {
               Icon(Icons.Close, "Zavřít", tint = TextSecondary)
           }
           // Progress bar (s bounce animací při postupu)
           AnimatedProgressBar(
               progress = progress,
               modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
               height = 10.dp,
           )
           // Timer (test mode)
           if (timer != null) {
               GlassCard(shape = PillShape) {
                   Text(
                       timer.format(),
                       Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                       style = labelLarge,
                       color = if (timer < 60.seconds) ErrorRed else TextPrimary,
                   )
               }
           }
       }
   }
   ```
2. Timer pulsuje červeně pod 60s.

**Výstup:** Informativní quiz header.

---

### Krok 68 — Question content layout

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/quiz/QuestionContent.kt`

1. Layout otázky — image/video nahoře, text uprostřed, odpovědi dole:
   ```kotlin
   @Composable
   fun QuestionContent(
       question: Question,
       state: QuizState,
       viewModel: QuizViewModel,
   ) {
       Column(
           Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
       ) {
           // Media (obrázek nebo video)
           if (question.hasImage) {
               QuizMedia(question, Modifier.fillMaxWidth().aspectRatio(16f/9f).clip(AutokolkShapes.medium))
           }
           
           Spacer(Modifier.height(20.dp))
           
           // Otázka
           GlassCard(Modifier.fillMaxWidth()) {
               Text(
                   question.text,
                   Modifier.padding(20.dp),
                   style = titleMedium,
                   textAlign = TextAlign.Center,
               )
           }
           
           Spacer(Modifier.height(24.dp))
           
           // Odpovědi
           question.answers.forEachIndexed { index, answer ->
               val label = ('A' + index).toString()
               AnswerButton(
                   text = answer.text,
                   label = label,
                   state = state.answerStates[index],
                   onClick = { viewModel.selectAnswer(index) },
                   modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
               )
           }
           
           Spacer(Modifier.height(80.dp)) // prostor pro result strip
       }
   }
   ```

**Výstup:** Čistý quiz layout.

---

### Krok 69 — Quiz media (obrázky a video)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/quiz/QuizMedia.kt`

1. Obrázky přes Coil s fade-in:
   ```kotlin
   @Composable
   fun QuizMedia(question: Question, modifier: Modifier = Modifier) {
       if (question.videoPath != null) {
           QuizVideo(question.videoPath, modifier)
       } else if (question.imagePath != null) {
           AsyncImage(
               model = question.imagePath,
               contentDescription = null,
               modifier = modifier,
               contentScale = ContentScale.Crop,
               placeholder = painterResource(R.drawable.ic_blank),
           )
       }
   }
   ```
2. Video: `AndroidView` s `VideoView` nebo `ExoPlayer` obalené v Compose.
3. Zaoblené rohy, stín, shimmer loading placeholder.

**Výstup:** Media zobrazení s polished loading.

---

### Krok 70 — Answer selection animace

**Soubory:** Součást `AnswerButton.kt` a `QuizViewModel.kt`

1. Flow po kliknutí na odpověď:
   - **0ms:** Tlačítko se zvýrazní (selected state — cyan border, mírný scale-up).
   - **200ms:** Haptic feedback.
   - **300ms:** Vyhodnocení — přechod do CORRECT nebo WRONG stavu.
   - **Correct:** Zelený gradient + ✓ ikona, jemný bounce, Lottie checkmark, "+bodů" popup, ding zvuk.
   - **Wrong:** Červený gradient + ✗ ikona, shake animace (3x horizontální kmit), buzz zvuk, vibrací. Správná odpověď se zvýrazní zeleně.
   - **800ms:** Result strip vyjede zdola.
2. Celý flow orchestrovaný přes `LaunchedEffect` a `delay()`.

**Výstup:** Dynamická, game-like odezva na odpovědi.

---

### Krok 71 — Result strip (correct/wrong panel)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/quiz/QuizResultStrip.kt`

1. Panel vyjíždějící zdola po odpovědi:
   ```kotlin
   @Composable
   fun QuizResultStrip(
       result: AnswerResult?,
       onNext: () -> Unit,
   ) {
       AnimatedVisibility(
           result != null,
           enter = slideInVertically { it } + fadeIn(),
           exit = slideOutVertically { it } + fadeOut(),
       ) {
           val isCorrect = result?.isCorrect == true
           val bgGradient = if (isCorrect)
               Brush.horizontalGradient(listOf(SuccessGreen.copy(0.9f), SuccessGreen.copy(0.7f)))
           else
               Brush.horizontalGradient(listOf(ErrorRed.copy(0.9f), ErrorRed.copy(0.7f)))
           
           Column(
               Modifier.fillMaxWidth().background(bgGradient).padding(20.dp).navigationBarsPadding()
           ) {
               Text(
                   if (isCorrect) "Správně! 🎉" else "Špatně 😬",
                   style = titleLarge,
                   color = Color.White,
               )
               if (!isCorrect && result?.explanation != null) {
                   Text(result.explanation, style = bodyMedium, color = Color.White.copy(0.9f))
               }
               Spacer(Modifier.height(12.dp))
               Button(
                   onClick = onNext,
                   colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.2f)),
                   modifier = Modifier.fillMaxWidth(),
               ) {
                   Text("Pokračovat", color = Color.White, fontWeight = FontWeight.Bold)
               }
           }
       }
   }
   ```

**Výstup:** Informativní result panel s dalším krokem.

---

### Krok 72 — Quiz ViewModel

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/quiz/QuizViewModel.kt`

1. ViewModel spravující celý quiz state:
   ```kotlin
   class QuizViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
       data class QuizState(
           val questions: List<Question>,
           val currentIndex: Int = 0,
           val answerStates: List<AnswerState> = List(3) { AnswerState.DEFAULT },
           val lastResult: AnswerResult? = null,
           val correctCount: Int = 0,
           val showCoinPopup: Boolean = false,
           val coinAmount: Int = 0,
           val progress: Float = 0f,
           val remainingTime: Duration? = null,
           val isFinished: Boolean = false,
       )
       
       private val _state = MutableStateFlow(QuizState(questions = emptyList()))
       val state: StateFlow<QuizState> = _state.asStateFlow()
       
       fun selectAnswer(index: Int) { /* evaluate, animate, update state */ }
       fun nextQuestion() { /* advance or finish */ }
       fun dismissPopup() { _state.update { it.copy(showCoinPopup = false) } }
   }
   ```

**Výstup:** Čistá quiz business logika.

---

### Krok 73 — Přechod mezi otázkami (slide animace)

**Soubory:** Součást `QuizScreen.kt`

1. Při přechodu na další otázku:
   - Aktuální otázka slide-out doleva + fade-out.
   - Nová otázka slide-in zprava + fade-in.
   - Progress bar bounce animace.
2. Implementace přes `AnimatedContent` s `transitionSpec`:
   ```kotlin
   transitionSpec = {
       (slideInHorizontally { fullWidth -> fullWidth } + fadeIn())
           .togetherWith(slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut())
   }
   ```
3. Haptic feedback "light" při každém přechodu.

**Výstup:** Plynulé přechody mezi otázkami.

---

### Krok 74 — Quiz close confirmation dialog

**Soubory:** Součást `QuizScreen.kt`

1. Při kliknutí na X — dialog:
   ```kotlin
   @Composable
   fun QuitQuizDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
       AlertDialog(
           onDismissRequest = onDismiss,
           containerColor = DarkSurfaceVariant,
           title = { Text("Ukončit lekci?") },
           text = { Text("Tvůj postup nebude uložen.", color = TextSecondary) },
           confirmButton = {
               TextButton(onClick = onConfirm) { Text("Ukončit", color = ErrorRed) }
           },
           dismissButton = {
               PrimaryGradientButton("Pokračovat", onClick = onDismiss)
           }
       )
   }
   ```

**Výstup:** Prevence nechtěného odchodu.

---

### Krok 75 — Quiz "streak" micro-interaction

**Soubory:** Součást quiz flow

1. Při správné odpovědi v řadě zobrazit combo counter:
   - 2× v řadě: "🔥 2× combo!"
   - 5× v řadě: "🔥🔥 5× combo! Super!" + extra body.
   - 10× v řadě: "💯 PERFEKTNÍ!" + konfety.
2. Counter se zobrazí jako floating text nad progress barem, animovaný scale-in + float up.
3. Combo resetován při špatné odpovědi.

**Výstup:** Motivace k soustředění, game-like feel.

---

### Krok 76 — Quiz "power-up" hints (budoucí rozšíření)

**Soubory:** Připravit UI, logiku implementovat později

1. Pod otázkou přidat řadu hint tlačítek (za coins):
   - 🗑️ Odstranit 1 špatnou odpověď (5 coins)
   - ⏭️ Přeskočit otázku (10 coins)
   - 💡 Zobrazit nápovědu (3 coins)
2. Glass pills s ikonami, kliknutí odečte body a aktivuje efekt.
3. Zatím připravit UI placeholder, logiku napojit v Fázi 11 (gamifikace).

**Výstup:** Prostor pro power-upy.

---

### Krok 77 — Správná odpověď — particle burst

**Soubory:** Nový efekt v `AnswerButton.kt`

1. Při CORRECT stavu: z tlačítka vylétnou malé zelené/zlaté particles (hvězdičky):
   ```kotlin
   @Composable
   fun AnswerParticleBurst(isActive: Boolean, origin: Offset) {
       if (!isActive) return
       val particles = remember { List(20) { Particle.random(origin, SuccessGreen, WarningAmber) } }
       val progress = remember { Animatable(0f) }
       LaunchedEffect(Unit) { progress.animateTo(1f, tween(800)) }
       Canvas(Modifier.fillMaxSize()) {
           particles.forEach { p -> /* draw at position based on progress */ }
       }
   }
   ```
2. Efekt je krátký (800ms), nedominantní, ale dodá pocit odměny.

**Výstup:** Micro-celebration při správné odpovědi.

---

### Krok 78 — Špatná odpověď — screen shake

**Soubory:** Součást `QuizScreen.kt`

1. Při špatné odpovědi celá obrazovka jemně "zatřese":
   ```kotlin
   val shakeOffset = remember { Animatable(0f) }
   LaunchedEffect(state.lastResult) {
       if (state.lastResult?.isCorrect == false) {
           repeat(3) {
               shakeOffset.animateTo(8f, tween(40))
               shakeOffset.animateTo(-8f, tween(40))
           }
           shakeOffset.animateTo(0f, spring())
       }
   }
   
   Column(Modifier.offset(x = shakeOffset.value.dp)) { /* quiz content */ }
   ```
2. Kombinace se zvukem a vibrací = silná senzorická odezva.

**Výstup:** Fyzický feedback na chybu.

---

### Krok 79 — Quiz timer animace (test mode)

**Soubory:** Součást `QuizTopBar.kt`

1. Časovač pro test mode:
   - Zobrazuje zbývající čas ve formátu MM:SS.
   - Pod 5 minut: text žlutý.
   - Pod 1 minutu: text červený + pulsující scale.
   - Pod 10s: countdown animace (každá sekunda scale bounce).
2. Sound: pod 10s tikání hodinek.
3. Po vypršení: automatické ukončení testu.

**Výstup:** Napínavý countdown efekt.

---

### Krok 80 — Quiz life-loss animace

**Soubory:** Součást quiz flow

1. Při špatné odpovědi v lesson mode (ne test):
   - V top baru srdce se "rozbije" — animace scale-down + fade-out + particle shatter.
   - Číslo se sníží (`AnimatedCounter`).
   - Pokud 0 životů → overlay "Došly ti životy!" s opcemi.
2. "Došly životy" overlay:
   ```kotlin
   // Glass card uprostřed
   // Lottie animace broken heart
   // "Podívej se na reklamu" → získej 1 život
   // "Počkej X minut" → countdown
   // "Zavřít" → zpět na Home
   ```

**Výstup:** Dramatická vizualizace ztráty života.

---

### Krok 81 — Results screen (lesson mode)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/results/ResultsScreen.kt`

1. Migrovat `ResultsActivity` do Compose:
   ```kotlin
   @Composable
   fun ResultsScreen(score: Int, total: Int, lessonId: Int, navController: NavHostController) {
       val percentage = (score.toFloat() / total * 100).toInt()
       val passed = percentage >= 80
       
       Column(
           Modifier.fillMaxSize().systemBarsPadding().padding(24.dp),
           horizontalAlignment = CenterHorizontally,
           verticalArrangement = Arrangement.Center,
       ) {
           // Lottie animace (success/fail)
           val lottieRes = if (passed) "lottie/correct_answer.json" else "lottie/wrong_answer.json"
           LottieAnimation(/* ... */, Modifier.size(150.dp))
           
           Spacer(Modifier.height(24.dp))
           
           // Score s animovaným counter
           Text(if (passed) "Výborně!" else "Zkus to znovu!", style = headlineLarge)
           Spacer(Modifier.height(8.dp))
           
           // Animovaný kruhový progress
           RingProgress(progress = percentage / 100f, size = 120.dp, strokeWidth = 8.dp) {
               AnimatedCounter(percentage, style = displayLarge)
               Text("%", style = titleMedium, color = TextSecondary)
           }
           
           Spacer(Modifier.height(32.dp))
           
           // Statistiky v řadě
           Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
               StatCard("Správně", "$score/$total", SuccessGreen)
               StatCard("Body", "+${score * 2}", WarningAmber)
               StatCard("Čas", formatTime(elapsed), AccentCyan)
           }
           
           Spacer(Modifier.height(32.dp))
           
           PrimaryGradientButton("Pokračovat", onClick = { navController.popBackStack() }, Modifier.fillMaxWidth())
           Spacer(Modifier.height(12.dp))
           TextButton(onClick = { /* retry */ }) { Text("Zkusit znovu", color = AccentCyan) }
       }
       
       // Konfety pro 100%
       if (percentage == 100) ConfettiOverlay(isActive = true)
   }
   ```

**Výstup:** Motivační results screen.

---

### Krok 82 — Results statistika s animací count-up

**Soubory:** Součást `ResultsScreen.kt`

1. Při otevření results screen se čísla animují od 0 do finální hodnoty:
   - Score ring: 0% → skutečný % (1.5s, overshoot).
   - Správně: 0 → X (stagger po 200ms).
   - Body: 0 → +Y (stagger po 400ms).
2. Každý stat má vlastní `LaunchedEffect` s `delay` pro postupné odhalení.

**Výstup:** Dramatický reveal výsledků.

---

### Krok 83 — Streak celebration screen

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/streak/StreakScreen.kt`

1. Migrovat `StreakActivity`:
   ```kotlin
   @Composable
   fun StreakScreen(streak: Int, navController: NavHostController) {
       Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
           // Animated gradient background (warm → hot)
           AnimatedBackground(warmColors = true)
           
           Column(horizontalAlignment = CenterHorizontally) {
               // Velká Lottie flame
               LottieAnimation(/* streak_fire.json */, Modifier.size(200.dp), iterations = IterateForever)
               Spacer(Modifier.height(16.dp))
               // Číslo s výraznou animací
               AnimatedCounter(streak, style = displayLarge.copy(fontSize = 72.sp), color = WarningAmber)
               Text("dní v řadě!", style = headlineMedium, color = TextSecondary)
               Spacer(Modifier.height(48.dp))
               PrimaryGradientButton("Pokračovat", onClick = { navController.popBackStack() })
           }
           
           ConfettiOverlay(isActive = true)
       }
   }
   ```
2. Haptic: heavy buzz při zobrazení.
3. Sound: streak fanfáre.

**Výstup:** Efektní streak oslava.

---

### Krok 84 — Quiz question number indicator

**Soubory:** Součást `QuizScreen.kt`

1. Malý badge zobrazující "3/15" — aktuální otázka z celku:
   ```kotlin
   GlassCard(shape = PillShape) {
       Text("${state.currentIndex + 1}/${state.questions.size}", Modifier.padding(8.dp, 4.dp), style = labelMedium)
   }
   ```
2. Při přechodu: číslo se animuje (slide up).

**Výstup:** Jasná orientace v kvízu.

---

### Krok 85 — Quiz "fun fact" po odpovědi (volitelné)

**Soubory:** Data model + `QuizResultStrip.kt`

1. Po správné odpovědi zobrazit zajímavost ("Věděl jsi, že...?"):
   - Krátký fun fact související s otázkou.
   - Zobrazí se v result stripu pod "Správně!".
2. Data: přidat `funFact: String?` do `Question` modelu.
3. Zdroj: předpřipravit 50–100 fun facts o řízení.

**Výstup:** Edukativní i zábavný doplněk ke kvízu.

---

## Fáze 7: Alex — Virtuální mazlíček

### Krok 86 — Alex screen scaffold

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/alex/AlexScreen.kt`

1. Hlavní Alex composable:
   ```kotlin
   @Composable
   fun AlexScreen(navController: NavHostController, viewModel: AlexViewModel = viewModel()) {
       val state by viewModel.state.collectAsStateWithLifecycle()
       
       Box(Modifier.fillMaxSize()) {
           Column(
               Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
               horizontalAlignment = CenterHorizontally,
           ) {
               Spacer(Modifier.height(16.dp))
               // Alex jméno a titul
               Text(state.lionName, style = headlineLarge)
               Text(state.title, style = bodyMedium, color = TextSecondary) // "Spokojený lev" / "Hladový lev"
               
               Spacer(Modifier.height(24.dp))
               
               // Alex obrázek s animací
               AlexCharacter(state.mood, state.hasSunglasses, Modifier.size(250.dp))
               
               Spacer(Modifier.height(24.dp))
               
               // Hunger bar
               HungerBar(state.hungerPercent, state.isFrozen)
               
               Spacer(Modifier.height(24.dp))
               
               // Akční tlačítka
               Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                   ActionButton("Nakrmit", Icons.Food, AccentTeal, onClick = { viewModel.openFoodMenu() })
                   ActionButton("Obchod", Icons.Shop, AccentCyan, onClick = { viewModel.openShop() })
               }
           }
           
           // Food menu overlay
           if (state.showFoodMenu) FoodMenuSheet(state, viewModel)
           // Shop overlay
           if (state.showShop) ShopSheet(state, viewModel)
       }
   }
   ```

**Výstup:** Čistá Alex stránka.

---

### Krok 87 — Alex character composable s animacemi

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/alex/AlexCharacter.kt`

1. Alex obrázek s reaktivními animacemi:
   ```kotlin
   @Composable
   fun AlexCharacter(mood: AlexMood, hasSunglasses: Boolean, modifier: Modifier = Modifier) {
       val imageName = when {
           hasSunglasses && mood == HAPPY -> "AlexCool"
           mood == HAPPY -> "Alex"
           mood == NEUTRAL -> "AlexSad"
           mood == HUNGRY -> "AlexSadC"
           mood == STARVING -> "AlexFamine"
           else -> "Alex"
       }
       
       // Idle animace — mírný "breathing" efekt
       val breathScale by rememberInfiniteTransition().animateFloat(
           1f, 1.02f, infiniteRepeatable(tween(2000), RepeatMode.Reverse)
       )
       // Bounce při krmení
       val bounceScale = remember { Animatable(1f) }
       
       Box(modifier, contentAlignment = Alignment.Center) {
           AsyncImage(
               model = "file:///android_asset/images/alex/$imageName.png",
               contentDescription = "Alex",
               modifier = Modifier.fillMaxSize().scale(breathScale * bounceScale.value),
           )
       }
   }
   ```
2. **Krmení animace:** Alex bounce (scale 1 → 1.15 → 1, spring), srdíčkové particles vyletí nahoru.
3. **Tap animace:** Při tapnutí na Alexe → wiggle (rotace ±5°), zvuk "mňau"/roar.

**Výstup:** Živý, interaktivní maskot.

---

### Krok 88 — Hunger bar s gradient a animací

**Soubory:** Součást `AlexScreen.kt`

1. Hunger bar se mění dle procent:
   ```kotlin
   @Composable
   fun HungerBar(percent: Int, isFrozen: Boolean) {
       val gradient = when {
           percent > 60 -> listOf(SuccessGreen, AccentTeal)
           percent > 30 -> listOf(WarningAmber, WarningAmber.copy(alpha = 0.7f))
           else -> listOf(ErrorRed, ErrorRed.copy(alpha = 0.7f))
       }
       Column(Modifier.fillMaxWidth()) {
           Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
               Text("Sytost", style = labelLarge)
               Text("$percent%", style = labelLarge, color = gradient.first())
           }
           Spacer(Modifier.height(4.dp))
           AnimatedProgressBar(progress = percent / 100f, gradient = gradient, height = 12.dp)
           if (isFrozen) {
               Text("❄️ Hlad zmrazen", style = labelMedium, color = AccentCyan, modifier = Modifier.padding(top = 4.dp))
           }
       }
   }
   ```

**Výstup:** Vizuálně informativní hunger indikátor.

---

### Krok 89 — Food menu bottom sheet

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/alex/FoodMenuSheet.kt`

1. Bottom sheet s jídly:
   ```kotlin
   @Composable
   fun FoodMenuSheet(state: AlexState, viewModel: AlexViewModel) {
       ModalBottomSheet(onDismissRequest = { viewModel.closeFoodMenu() }) {
           LazyColumn(Modifier.padding(16.dp)) {
               item {
                   Text("Nakrmit Alexe", style = headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
               }
               items(state.foodItems) { food ->
                   FoodItemRow(food, state.coins, onClick = { viewModel.feed(food) })
               }
           }
       }
   }
   
   @Composable
   fun FoodItemRow(food: FoodItem, coins: Int, onClick: () -> Unit) {
       GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
           Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
               AsyncImage(food.imageUrl, null, Modifier.size(48.dp).clip(CircleShape))
               Spacer(Modifier.width(12.dp))
               Column(Modifier.weight(1f)) {
                   Text(food.name, style = titleMedium)
                   Text("+${food.hungerValue}% sytost", style = bodySmall, color = SuccessGreen)
               }
               PrimaryGradientButton(
                   "${food.price}",
                   onClick = onClick,
                   enabled = coins >= food.price,
                   icon = Icons.Coin,
               )
           }
       }
   }
   ```

**Výstup:** Přehledné menu s jídly.

---

### Krok 90 — Feed animace (Alex eating)

**Soubory:** Součást `AlexScreen.kt`

1. Při krmení:
   - Jídlo "letí" z tlačítka k Alexovi (animated offset).
   - Alex bounce + happy face.
   - Hunger bar se animovaně zvýší.
   - FloatingReward popup "-5 coins".
   - Srdíčkové particles nad Alexem.
2. Celá sekvence orchestrovaná v `AlexViewModel` + coroutines.

**Výstup:** Satisfying feeding interaction.

---

### Krok 91 — Shop bottom sheet

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/alex/ShopSheet.kt`

1. Obchod s cosmetics pro Alexe:
   - Sluneční brýle (switch on/off).
   - Budoucí: čepice, šály, pozadí.
2. Glass card layout, preview Alexe s položkou.

**Výstup:** Shop UI.

---

### Krok 92 — Alex rename dialog

**Soubory:** Součást Alex UI

1. Dlouhý tap na jméno → dialog pro přejmenování:
   ```kotlin
   // AlertDialog s TextField
   // Preview: "Tvůj lev se bude jmenovat: [input]"
   // Validace: 1-20 znaků, bez emoji
   ```

**Výstup:** Personalizace maskota.

---

### Krok 93 — Alex death screen

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/alex/AlexDeathScreen.kt`

1. Migrovat `AlexDeathActivity`:
   ```kotlin
   @Composable
   fun AlexDeathScreen(navController: NavHostController) {
       // Tmavé pozadí, dramatic
       // Alex "mrtvý" obrázek s desaturací
       // "Alex vyhladověl!" — headlineLarge, ErrorRed
       // Příběhový text
       // Hold-to-revive tlačítko:
       //   - Uživatel drží prst, ring progress se plní
       //   - Po 3s: Alex revival animace (fade zpátky, bounce, confetti)
       //   - Navigate zpět na Alex page
   }
   ```
2. Hold-to-revive: `pointerInput` s `detectTapGestures(onPress = { ... })` + ring progress.

**Výstup:** Dramatický revival moment.

---

### Krok 94 — Alex ViewModel

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/alex/AlexViewModel.kt`

1. ViewModel pro Alex state management:
   ```kotlin
   class AlexViewModel(application: Application) : AndroidViewModel(application) {
       data class AlexState(
           val lionName: String,
           val hungerPercent: Int,
           val mood: AlexMood,
           val hasSunglasses: Boolean,
           val isFrozen: Boolean,
           val coins: Int,
           val foodItems: List<FoodItem>,
           val showFoodMenu: Boolean,
           val showShop: Boolean,
       )
       // ... StateFlow + functions
   }
   ```

**Výstup:** Čistá state management pro Alex.

---

### Krok 95 — Alex mood systém

**Soubory:** Součást `AlexViewModel.kt`

1. Alex mood závisí na hunger:
   - 80–100%: HAPPY (happy face, bright colors)
   - 50–79%: NEUTRAL (normal face)
   - 20–49%: HUNGRY (sad face, žlutý tint)
   - 0–19%: STARVING (very sad, červený pulsující border)
2. Mood ovlivňuje:
   - Obrázek Alexe.
   - Barvu hunger baru.
   - Titulní text ("Spokojený lev" vs "Hladový lev").
   - Případně background tint celé stránky.

**Výstup:** Emocionální systém maskota.

---

### Krok 96 — Alex interakční animace (tap/swipe)

**Soubory:** Součást `AlexCharacter.kt`

1. Různé gesta na Alexovi:
   - **Tap:** Wiggle animace (rotace), zvuk.
   - **Double tap:** Alex udělá salto (360° rotace, 600ms).
   - **Long press:** Alex se rozmrká / zobrazí srdíčko.
   - **Swipe:** Alex se posune a vrátí se zpět (spring).
2. Každá interakce má haptic feedback.

**Výstup:** Alex jako interaktivní "tamagotchi" prvek.

---

### Krok 97 — Hunger notifikace redesign

**Soubory:** Existující `HungerNotificationService.kt` — aktualizace obsahu

1. Notifikace s obrázkem Alexe (BigPictureStyle).
2. Různé texty dle hunger levelu:
   - 50%: "Alex má trochu hlad 🦁"
   - 20%: "Alex potřebuje jídlo! 😿"
   - 5%: "Alex je na pokraji! 🚨 Zachraň ho!"
3. Action button "Nakrmit" → deep link na Alex stránku.

**Výstup:** Engaging notifikace.

---

## Fáze 8: Test / Zkouška

### Krok 98 — Test hub screen

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/test/TestScreen.kt`

1. Hlavní "Zkouška" tab:
   ```kotlin
   @Composable
   fun TestScreen(navController: NavHostController) {
       Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = CenterHorizontally) {
           // Hero section
           GlassCard(Modifier.fillMaxWidth()) {
               Column(Modifier.padding(24.dp), horizontalAlignment = CenterHorizontally) {
                   LottieAnimation(/* test icon anim */, Modifier.size(120.dp))
                   Text("Zkouška z teorie", style = headlineLarge)
                   Text("25 otázek • 30 minut • min. 43 bodů", style = bodyMedium, color = TextSecondary)
                   Spacer(Modifier.height(20.dp))
                   PrimaryGradientButton("Spustit zkoušku", onClick = { /* navigate to quiz test mode */ }, Modifier.fillMaxWidth())
               }
           }
           Spacer(Modifier.height(24.dp))
           // Statistiky
           GlassCard(Modifier.fillMaxWidth()) {
               Column(Modifier.padding(16.dp)) {
                   Text("Tvoje výsledky", style = titleLarge)
                   Spacer(Modifier.height(12.dp))
                   // Průměrné skóre
                   // ScoresChart (Compose verze)
                   // Počet pokusů, úspěšnost
               }
           }
       }
   }
   ```

**Výstup:** Přehledný test hub.

---

### Krok 99 — Scores chart (Compose)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/test/ScoresChart.kt`

1. Migrovat `ScoresChartView` do Compose Canvas:
   ```kotlin
   @Composable
   fun ScoresChart(scores: List<Int>, threshold: Int = 43, modifier: Modifier = Modifier) {
       Canvas(modifier.fillMaxWidth().height(200.dp)) {
           // Osa Y: 0–50 bodů
           // Threshold čára (čárkovaná červená na 43)
           // Čára grafu s gradient fill pod ní
           // Body jako kroužky na čáře
           // Animace: čára se "kreslí" zleva doprava
       }
   }
   ```
2. Animace draw-in efektu při prvním zobrazení.

**Výstup:** Animovaný graf výsledků.

---

### Krok 100 — Test results screen

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/test/TestResultsScreen.kt`

1. Výsledky testu s pass/fail:
   ```kotlin
   @Composable
   fun TestResultsScreen(score: Int, details: List<QuestionResult>, navController: NavHostController) {
       val passed = score >= 43
       Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
           // Velký pass/fail indikátor
           Box(Modifier.fillMaxWidth().background(if (passed) SuccessGreen.copy(0.1f) else ErrorRed.copy(0.1f))) {
               Column(Modifier.padding(32.dp), horizontalAlignment = CenterHorizontally) {
                   LottieAnimation(if (passed) "success" else "fail")
                   Text(if (passed) "Úspěšně složeno!" else "Nesloženo", style = headlineLarge)
                   AnimatedCounter(score, style = displayLarge)
                   Text("z 50 bodů", style = bodyLarge, color = TextSecondary)
               }
           }
           // Detail otázek
           Text("Podrobnosti", style = titleLarge, Modifier.padding(16.dp))
           details.forEach { detail ->
               TestDetailRow(detail)
           }
       }
       if (passed) ConfettiOverlay(isActive = true)
   }
   ```

**Výstup:** Detailní test results.

---

### Krok 101 — Test detail řádky

**Soubory:** Součást `TestResultsScreen.kt`

1. Každá otázka v testu jako expandable row:
   ```kotlin
   @Composable
   fun TestDetailRow(detail: QuestionResult) {
       var expanded by remember { mutableStateOf(false) }
       GlassCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable { expanded = !expanded }) {
           Column(Modifier.padding(12.dp)) {
               Row(verticalAlignment = Alignment.CenterVertically) {
                   Icon(if (detail.correct) Icons.Check else Icons.Close, null,
                       tint = if (detail.correct) SuccessGreen else ErrorRed)
                   Spacer(Modifier.width(8.dp))
                   Text(detail.questionText, style = bodyMedium, maxLines = if (expanded) Int.MAX_VALUE else 1)
                   Spacer(Modifier.weight(1f))
                   Text("${detail.points}b", style = labelLarge)
               }
               AnimatedVisibility(expanded) {
                   Text("Tvoje odpověď: ${detail.userAnswer}\nSprávně: ${detail.correctAnswer}", style = bodySmall, color = TextSecondary)
               }
           }
       }
   }
   ```

**Výstup:** Expandable detail pro každou otázku.

---

### Krok 102 — Test mode specifika v QuizScreen

**Soubory:** Úpravy `QuizScreen.kt`, `QuizViewModel.kt`

1. V test mode:
   - Zobrazit timer v top baru.
   - Navigační šipky (předchozí/další) — uživatel se může vracet.
   - Žádný result strip po odpovědi — výsledky až nakonec.
   - Žádné combo/streak micro-interactions.
2. Navigace: `HorizontalPager` místo `AnimatedContent` — uživatel swipuje mezi otázkami.
3. Bottom bar: "Otázka X/25" + "Dokončit test" tlačítko (aktivní po odpovědi na všechny).

**Výstup:** Realistický testový zážitek.

---

### Krok 103 — Test countdown overlay

**Soubory:** Součást `QuizScreen.kt`

1. Před spuštěním testu: 3-2-1 countdown:
   ```kotlin
   @Composable
   fun CountdownOverlay(onFinish: () -> Unit) {
       var count by remember { mutableIntStateOf(3) }
       LaunchedEffect(Unit) {
           repeat(3) {
               delay(1000)
               count--
           }
           onFinish()
       }
       Box(Modifier.fillMaxSize().background(Color.Black.copy(0.8f)), contentAlignment = Alignment.Center) {
           AnimatedContent(count) { value ->
               Text(
                   if (value > 0) "$value" else "Start!",
                   style = displayLarge.copy(fontSize = 96.sp),
                   color = AccentCyan,
               )
           }
       }
   }
   ```
2. Každé číslo: scale-in + fade-out, haptic tick.

**Výstup:** Dramatický start testu.

---

### Krok 104–106 — Test stats, test attempt ViewModel, test history

Tyto kroky zahrnují:
- **Krok 104:** Compose verze `TestAttemptStatsActivity` s animovaným grafem a statistikami.
- **Krok 105:** `TestViewModel` spravující test state, timer, navigaci mezi otázkami, finální vyhodnocení.
- **Krok 106:** Persistentní test history (Room/DataStore) pro graf výsledků v čase.

---

## Fáze 9: Practice / Procvičování

### Krok 107 — Practice screen scaffold

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/practice/PracticeScreen.kt`

1. Grid kategorií:
   ```kotlin
   @Composable
   fun PracticeScreen(navController: NavHostController) {
       val categories = remember { loadCategories() }
       LazyVerticalGrid(
           columns = GridCells.Fixed(2),
           contentPadding = PaddingValues(16.dp),
           verticalArrangement = Arrangement.spacedBy(12.dp),
           horizontalArrangement = Arrangement.spacedBy(12.dp),
       ) {
           items(categories) { category ->
               CategoryCard(category, onClick = { /* navigate to quiz with categoryId */ })
           }
       }
   }
   ```

**Výstup:** Grid-based practice screen.

---

### Krok 108 — Category card

**Soubory:** Součást `PracticeScreen.kt`

1. Každá kategorie jako glass card s ikonou a progress:
   ```kotlin
   @Composable
   fun CategoryCard(category: Category, onClick: () -> Unit) {
       GlassCard(Modifier.aspectRatio(1f).clickable(onClick = onClick)) {
           Column(Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
               AsyncImage(category.iconUrl, null, Modifier.size(40.dp))
               Text(category.name, style = titleMedium, maxLines = 2)
               Row(verticalAlignment = Alignment.CenterVertically) {
                   AnimatedProgressBar(category.progress, Modifier.weight(1f), height = 6.dp)
                   Spacer(Modifier.width(8.dp))
                   Text("${(category.progress * 100).toInt()}%", style = labelSmall, color = TextSecondary)
               }
           }
       }
   }
   ```
2. Hover/press animace: scale + glow.
3. Completed kategorie: checkmark badge, zlatý border.

**Výstup:** Vizuálně přitažlivé kategorie.

---

### Krok 109–114 — Practice subcategories, filters, practice quiz mode, practice ViewModel, practice stats, search

Tyto kroky zahrnují:
- **Krok 109:** Subcategory expandable groups uvnitř kategorie.
- **Krok 110:** Filter chips (Všechny / Nenaučené / Chybné) pro filtraci otázek.
- **Krok 111:** Practice quiz mode v `QuizScreen` — bez časového limitu, nekonečné pokusy, instant feedback.
- **Krok 112:** `PracticeViewModel` se state management.
- **Krok 113:** Per-category statistiky (% správně, počet pokusů, nejhorší otázky).
- **Krok 114:** Search bar pro vyhledávání otázek napříč kategoriemi.

---

## Fáze 10: Settings a systémové obrazovky

### Krok 115 — Settings screen

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/settings/SettingsScreen.kt`

1. Moderní settings layout se skupinami:
   ```kotlin
   @Composable
   fun SettingsScreen(navController: NavHostController) {
       LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
           // Profil sekce
           item { ProfileSection() }
           // Vzhled
           item { SettingsGroup("Vzhled") {
               SwitchSetting("Tmavý režim", isDarkMode, onToggle = { ... })
               SwitchSetting("Zvuky", soundEnabled, onToggle = { ... })
               SwitchSetting("Vibrace", hapticEnabled, onToggle = { ... })
           }}
           // Učení
           item { SettingsGroup("Učení") {
               SwitchSetting("Biometrický zámek", ...)
               ClickableSetting("Denní cíl", dailyGoal.toString(), onClick = { ... })
           }}
           // O aplikaci
           item { SettingsGroup("O aplikaci") {
               ClickableSetting("Achievementy", onClick = { navController.navigate(Route.Achievements.route) })
               ClickableSetting("Changelog", onClick = { navController.navigate(Route.Changelog.route) })
               ClickableSetting("Verze", versionName)
           }}
           // Danger zone
           item { SettingsGroup("Nebezpečná zóna", isDanger = true) {
               ClickableSetting("Smazat veškerý postup", color = ErrorRed, onClick = { ... })
           }}
       }
   }
   ```

**Výstup:** Přehledné, moderní nastavení.

---

### Krok 116 — Settings komponenty (Switch, Clickable)

**Soubory:** Součást settings

1. `SwitchSetting` — glass row s Material3 Switch:
   ```kotlin
   @Composable
   fun SwitchSetting(title: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
       GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
           Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
               Text(title, style = bodyLarge, modifier = Modifier.weight(1f))
               Switch(checked = checked, onCheckedChange = onToggle, colors = autkolkSwitchColors())
           }
       }
   }
   ```
2. Switch s akcentovými barvami (cyan/teal).

**Výstup:** Konzistentní settings prvky.

---

### Krok 117 — Dark/light mode přepínač

**Soubory:** Settings + Theme

1. V Settings přidat přepínač dark/light/system:
   ```kotlin
   SegmentedButton(
       options = listOf("Systém", "Světlý", "Tmavý"),
       selectedIndex = themePreference,
       onSelect = { viewModel.setTheme(it) }
   )
   ```
2. Preference uložit do DataStore.
3. V `AutokolkTheme` číst tuto preferenci.
4. Přechod: animovaný crossfade mezi light/dark (celá obrazovka fade 300ms).

**Výstup:** Funkční přepínání tématu.

---

### Krok 118 — Achievements screen (Compose)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/achievements/AchievementsScreen.kt`

1. Seznam achievementů s lock/unlock stavy:
   ```kotlin
   @Composable
   fun AchievementsScreen(navController: NavHostController) {
       LazyColumn(contentPadding = PaddingValues(16.dp)) {
           items(achievements) { achievement ->
               AchievementCard(achievement)
           }
       }
   }
   
   @Composable
   fun AchievementCard(achievement: Achievement) {
       GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp).alpha(if (achievement.unlocked) 1f else 0.5f)) {
           Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
               // Ikona (locked = zámek, unlocked = custom icon)
               Box(Modifier.size(48.dp).background(if (achievement.unlocked) AccentCyan.copy(0.2f) else GlassFill, CircleShape), contentAlignment = Alignment.Center) {
                   Icon(if (achievement.unlocked) achievement.icon else Icons.Lock, null)
               }
               Spacer(Modifier.width(12.dp))
               Column(Modifier.weight(1f)) {
                   Text(achievement.title, style = titleMedium)
                   Text(achievement.description, style = bodySmall, color = TextSecondary)
                   if (!achievement.unlocked) {
                       AnimatedProgressBar(achievement.progress, Modifier.padding(top = 4.dp), height = 4.dp)
                   }
               }
               if (achievement.unlocked) {
                   Text("⭐", fontSize = 24.sp)
               }
           }
       }
   }
   ```

**Výstup:** Polished achievements list.

---

### Krok 119 — Achievement unlock animace

**Soubory:** Nový efekt

1. Při odemknutí achievementu (kdekoli v appce):
   - Full-screen overlay s Lottie "achievement_unlock.json".
   - Achievement ikona se zvětší a zazáří (glow pulse).
   - Konfety.
   - Název achievementu + popis.
   - Haptic: heavy + success.
   - Sound: unlock fanfáre.
2. Toast/snackbar pro menší achievementy.

**Výstup:** Rewarding unlock moment.

---

### Krok 120 — Changelog screen (Compose)

**Soubory:** Nový Compose screen

1. Markdown renderer (nebo jednoduché parsování `CHANGELOG.md`):
   ```kotlin
   @Composable
   fun ChangelogScreen(navController: NavHostController) {
       val changelog = remember { loadChangelog() }
       LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
           items(changelog.entries) { entry ->
               GlassCard(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                   Column(Modifier.padding(16.dp)) {
                       Text("v${entry.version}", style = titleMedium, color = AccentCyan)
                       Text(entry.date, style = labelSmall, color = TextSecondary)
                       Spacer(Modifier.height(8.dp))
                       entry.changes.forEach { change ->
                           Text("• $change", style = bodyMedium)
                       }
                   }
               }
           }
       }
   }
   ```

**Výstup:** Přehledný changelog.

---

### Krok 121 — Splash screen (Compose)

**Soubory:** Nový `app/src/main/java/cz/autokolk/ui/screens/splash/SplashScreen.kt`

1. Moderní splash s Lottie animací:
   ```kotlin
   @Composable
   fun SplashScreen(navController: NavHostController) {
       var isLoading by remember { mutableStateOf(true) }
       
       LaunchedEffect(Unit) {
           // Load assets, check DFM, consent
           loadEverything()
           isLoading = false
           val destination = if (isFirstLaunch()) Route.Onboarding.route else Route.Home.route
           navController.navigate(destination) { popUpTo(Route.Splash.route) { inclusive = true } }
       }
       
       Box(Modifier.fillMaxSize().background(DarkBackground), contentAlignment = Alignment.Center) {
           Column(horizontalAlignment = CenterHorizontally) {
               LottieAnimation(/* splash_loading.json */, Modifier.size(200.dp), iterations = IterateForever)
               Spacer(Modifier.height(24.dp))
               Text("Autoškolák", style = displayLarge, color = AccentCyan)
               Spacer(Modifier.height(8.dp))
               if (isLoading) {
                   CircularProgressIndicator(color = AccentCyan, strokeWidth = 2.dp, Modifier.size(24.dp))
               }
           }
       }
   }
   ```

**Výstup:** Profesionální splash screen.

---

### Krok 122 — Loading states pro DFM (Dynamic Feature Modules)

**Soubory:** Součást splash a quiz flow

1. Při stahování images/videos DFM:
   - Progress bar s procentem.
   - Lottie animace (auto jedoucí po silnici, progress = kolik ujelo).
   - Text "Stahuji materiály... 45%".
2. Error state s retry tlačítkem.

**Výstup:** User-friendly DFM loading.

---

## Fáze 11: Gamifikace a engagement

### Krok 123 — XP / leveling systém

**Soubory:** Nový `app/src/main/java/cz/autokolk/data/XpSystem.kt`

1. Definovat XP level tabulku:
   ```kotlin
   object XpSystem {
       val levels = listOf(
           Level(1, "Začátečník", 0),
           Level(2, "Učeň", 100),
           Level(3, "Student", 300),
           Level(4, "Řidič-junior", 600),
           Level(5, "Řidič", 1000),
           // ...
           Level(20, "Mistr volantu", 10000),
       )
       fun getLevelForXp(xp: Int): Level { ... }
       fun getProgressToNextLevel(xp: Int): Float { ... }
   }
   ```
2. XP se získávají za: lekce, testy, streak, achievementy, krmení Alexe.
3. Level-up trigger: overlay s Lottie animací, nový titul, konfety.

**Výstup:** Motivační leveling systém.

---

### Krok 124 — Level-up celebration

**Soubory:** Nový overlay

1. Při dosažení nového levelu:
   - Full-screen overlay.
   - Lottie "level_up.json".
   - "Level X!" s animovaným counter.
   - Nový titul (např. "Řidič-junior").
   - Odměna (bonus coins).
   - Konfety + haptic + sound.

**Výstup:** Dopaminový hit při level-up.

---

### Krok 125 — Daily challenges

**Soubory:** Nový systém

1. Každý den vygenerovat 3 výzvy:
   - "Odpověz správně na 10 otázek" (10 XP)
   - "Dokonči 2 lekce" (20 XP)
   - "Nakrmí Alexe" (5 XP)
2. UI: Na Home screen nad lesson path, horizontální scroll:
   ```kotlin
   LazyRow {
       items(dailyChallenges) { challenge ->
           DailyChallengeCard(challenge)
       }
   }
   ```
3. Progress bar na každé výzvě, checkmark po splnění.

**Výstup:** Denní engagement loop.

---

### Krok 126 — Streak freeze mechanika

**Soubory:** Data + UI

1. Uživatel může "zmrazit" streak na 1 den za 20 coins.
2. UI v streak sheet: "Zmrazit na zítra" tlačítko.
3. Vizuál: ikona sněhové vločky, zmrazený streak den v heatmapě.

**Výstup:** Ochrana proti ztrátě streak.

---

### Krok 127 — Streak milestones

**Soubory:** Data + overlay

1. Speciální odměny za streak milestones:
   - 7 dní: 50 bonus coins + achievement.
   - 30 dní: 200 bonus coins + achievement + special Alex outfit.
   - 100 dní: 1000 bonus coins + achievement.
   - 365 dní: special achievement + title.
2. Celebration overlay s příslušnou animací.

**Výstup:** Dlouhodobá motivace.

---

### Krok 128 — Power-ups implementace

**Soubory:** `QuizViewModel.kt`, UI v `QuizScreen.kt`

1. Implementovat logiku power-upů z kroku 76:
   - 🗑️ **Eliminace:** Odstraní 1 špatnou odpověď (answer button fade-out + strikethrough). Cena: 5 coins.
   - ⏭️ **Přeskočení:** Otázka se přeskočí bez penalizace. Cena: 10 coins.
   - 💡 **Nápověda:** Zobrazí krátkou textovou nápovědu. Cena: 3 coins.
2. Cooldown: max 1 power-up na otázku.
3. Animace: tlačítka se animovaně "spotřebují" (scale-down + particle burst).

**Výstup:** Strategický element v kvízu.

---

### Krok 129 — Weekly leaderboard (lokální)

**Soubory:** Nový

1. Týdenní přehled výkonu:
   - Graf XP za posledních 7 dní.
   - Srovnání s předchozím týdnem.
   - "Osobní rekord" highlight.
2. Přístupné z Home screen nebo Settings.

**Výstup:** Self-competition element.

---

### Krok 130 — Achievement unlock triggers

**Soubory:** `AchievementManager.kt` (nový nebo refaktor stávajícího)

1. Definovat achievement triggers:
   ```kotlin
   enum class AchievementType {
       FIRST_LESSON,        // Dokončit první lekci
       PERFECT_LESSON,      // 100% v lekci
       STREAK_7,            // 7-denní streak
       STREAK_30,           // 30-denní streak
       ALL_CATEGORIES,      // Dokončit všechny kategorie
       FEED_ALEX_100,       // Nakrmit Alexe 100×
       PASS_TEST,           // Složit zkoušku
       SPEED_DEMON,         // Dokončit lekci pod 2 minuty
       NIGHT_OWL,           // Učit se po půlnoci
       EARLY_BIRD,          // Učit se před 6:00
       COMBO_10,            // 10× combo v lekci
       // ... 30+ achievementů
   }
   ```
2. Check po každé relevantní akci (lesson complete, answer, feed, etc.).

**Výstup:** Komplexní achievement systém.

---

### Krok 131–137 — Další gamifikace kroky

- **Krok 131:** Coin shop (cosmetics pro UI — themes, Alex outfity).
- **Krok 132:** "Dvojitý XP" boost (za reklamu, 30 minut 2× body).
- **Krok 133:** Weekly summary screen (push notifikace + in-app přehled týdne).
- **Krok 134:** Progress milestones na lesson path (badges po sekcích).
- **Krok 135:** "Revize" systém — spaced repetition pro chybné otázky.
- **Krok 136:** Social sharing (sdílet streak/výsledek jako obrázek).
- **Krok 137:** Widget na homescreen (streak, denní challenge progress).

---

## Fáze 12: Zvuky a haptika

### Krok 138 — Zvukové soubory

**Soubory:** `app/src/main/res/raw/`

1. Přidat zvukové efekty (MP3/OGG, max 50kB každý):
   - `correct.ogg` — krátký ding/chime
   - `wrong.ogg` — krátký buzz/error tone
   - `tap.ogg` — jemný click
   - `streak.ogg` — rostoucí fanfáre
   - `coin.ogg` — mince cinknutí
   - `levelup.ogg` — triumfální jingle
   - `countdown_tick.ogg` — tiknutí hodin
   - `achievement.ogg` — unlock sound
   - `combo.ogg` — stoupající tón pro combo
   - `swipe.ogg` — jemný whoosh
2. Zdroje: freesound.org (CC0) nebo vlastní generace.

**Výstup:** Kompletní sada zvukových efektů.

---

### Krok 139 — SoundManager implementace

**Soubory:** `app/src/main/java/cz/autokolk/audio/SoundManager.kt`

1. Finální implementace:
   ```kotlin
   object SoundManager {
       private var soundPool: SoundPool? = null
       private val soundMap = mutableMapOf<Sound, Int>()
       private var enabled = true
       
       fun init(context: Context) {
           val attrs = AudioAttributes.Builder()
               .setUsage(AudioAttributes.USAGE_GAME)
               .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
               .build()
           soundPool = SoundPool.Builder().setMaxStreams(5).setAudioAttributes(attrs).build()
           Sound.entries.forEach { sound ->
               soundMap[sound] = soundPool!!.load(context, sound.resId, 1)
           }
       }
       
       fun play(sound: Sound, rate: Float = 1f) {
           if (!enabled) return
           soundMap[sound]?.let { id -> soundPool?.play(id, 1f, 1f, 1, 0, rate) }
       }
   }
   ```

**Výstup:** Funkční sound engine.

---

### Krok 140 — Haptic patterns

**Soubory:** `app/src/main/java/cz/autokolk/ui/util/HapticFeedback.kt`

1. Finální haptic patterns:
   ```kotlin
   // Compose extension
   @Composable
   fun rememberHapticFeedback(): HapticManager {
       val view = LocalView.current
       return remember { HapticManager(view) }
   }
   
   class HapticManager(private val view: View) {
       fun tap() = view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
       fun success() = view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
       fun error() = view.performHapticFeedback(HapticFeedbackConstants.REJECT)
       fun heavy() = view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
   }
   ```

**Výstup:** Platform-native haptic feedback.

---

### Krok 141 — Integrace zvuků do quiz flow

**Soubory:** `QuizScreen.kt`, `QuizViewModel.kt`

1. Mapování:
   - Klik na odpověď → `tap`
   - Správná odpověď → `correct` + haptic success
   - Špatná odpověď → `wrong` + haptic error
   - Combo 5+ → `combo` (pitch roste s combo)
   - Přechod otázky → `swipe`
   - Konec lekce → `levelup` nebo nic (dle výsledku)

**Výstup:** Zvukově bohatý quiz.

---

### Krok 142 — Integrace zvuků do Alex

**Soubory:** `AlexScreen.kt`

1. Krmení → `coin` sound (za útratu)
2. Tap na Alexe → lion roar / meow
3. Alex hungry pulse → subtle heartbeat

**Výstup:** Zvuky na Alex stránce.

---

### Krok 143 — Integrace zvuků do navigace

**Soubory:** `AutokolkBottomBar.kt`, sheets

1. Tab switch → `tap`
2. Sheet open → `swipe`
3. Sheet close → `swipe` (reversed)

**Výstup:** Subtle navigační zvuky.

---

### Krok 144 — Settings: zvuky a vibrace toggle

**Soubory:** `SettingsScreen.kt`

1. Přepínač "Zvukové efekty" → `SoundManager.setEnabled()`
2. Přepínač "Vibrace" → `HapticManager.setEnabled()`
3. Uložit do DataStore.

**Výstup:** Uživatelská kontrola nad zvuky/vibrací.

---

### Krok 145 — Testování zvuků a haptic feedback

**Soubory:** —

1. Projít všechny flows a ověřit:
   - Zvuky se nepřekrývají.
   - Haptic feedback je konzistentní.
   - Při vypnutých zvucích/vibracích nic nepřehrává/nevibruje.
   - Žádný zvuk nehrazen při zamčené obrazovce.

**Výstup:** QA zvuků a haptiky.

---

## Fáze 13: Finální polish a performance

### Krok 146 — Odstranění starých Activity souborů

**Soubory:** Všechny `*Activity.kt` a `*Fragment.kt` v `cz/autokolk/autokolk/`

1. Po dokončení všech Compose obrazovek:
   - Smazat `HomeActivity.kt`, `AlexActivity.kt`, `PracticeActivity.kt`, `MainActivity.kt`, `SettingsActivity.kt`, `TestAttemptActivity.kt`, `TestAttemptStatsActivity.kt`, `TestResultsActivity.kt`, `ResultsActivity.kt`, `StreakActivity.kt`, `AchievementsActivity.kt`, `ChangelogActivity.kt`, `ReadingLessonActivity.kt`, `AlexDeathActivity.kt`, `LoadingActivity.kt`.
   - Smazat `AlexPageOneFragment.kt`, `AlexPageTwoFragment.kt`.
   - Smazat `AutokolkActivity.kt`.
2. Smazat `EventStyleOverlay.kt`, `ConfettiView.kt`, `CurvyPathView.kt`, `ScoresChartView.kt`, `RingProgressDrawable.kt`, `CustomMediaController.kt`.
3. Smazat `AlexPagerAdapter.kt`.

**Výstup:** Čistý single-activity Compose codebase.

---

### Krok 147 — Odstranění starých XML layoutů

**Soubory:** `app/src/main/res/layout/`

1. Smazat všech 33 XML layout souborů.
2. Ponechat pouze pokud nějaký je stále potřeba (AndroidView wrapper v Compose pro VideoView).

**Výstup:** Žádné legacy XML layouty.

---

### Krok 148 — Odstranění starých stylů a témat

**Soubory:** `res/values/themes.xml`, `styles.xml`, `colors.xml`

1. Ponechat minimální theme pro `ComposeMainActivity` (Material3 NoActionBar).
2. Smazat `Widget.Autokolk.*` styly, `ThemeOverlay.Autokolk.*`, popup animation styly.
3. Barvy ponechat jen jako fallback — hlavní paleta je v Compose `Color.kt`.

**Výstup:** Minimální XML resources.

---

### Krok 149 — Performance audit — recomposition

**Soubory:** Compose screens

1. Pomocí Layout Inspector v Android Studio ověřit recomposition counts.
2. Identifikovat a opravit zbytečné recompositions:
   - `remember` pro kalkulace.
   - `derivedStateOf` pro odvozené stavy.
   - `key()` pro list items.
   - Stabilní data třídy (immutable).
3. Cíl: žádná obrazovka by neměla recomposovat víc než 1–2× na frame.

**Výstup:** Optimalizovaný Compose rendering.

---

### Krok 150 — Performance audit — animace

**Soubory:** Animační kód

1. Ověřit, že animace běží na 60 FPS (nebo 120 FPS na podporovaných zařízeních).
2. Problematické oblasti:
   - `ConfettiOverlay` — Canvas s mnoha particles.
   - `AnimatedBackground` — nekonečné gradient animace.
   - `LessonPathCanvas` — komplexní Path drawing.
3. Optimalizace:
   - Použít `drawWithCache` kde je to možné.
   - Omezit particle count na starších zařízeních.
   - `graphicsLayer` pro hardware-accelerated transformace.

**Výstup:** Plynulé animace na všech zařízeních.

---

### Krok 151 — Performance audit — image loading

**Soubory:** Coil konfigurace

1. Nastavit Coil pro optimální loading:
   ```kotlin
   ImageLoader.Builder(context)
       .memoryCache { MemoryCache.Builder(context).maxSizePercent(0.25).build() }
       .diskCache { DiskCache.Builder().directory(cacheDir / "image_cache").maxSizePercent(0.1).build() }
       .crossfade(true)
       .build()
   ```
2. Placeholder shimmer efekt během načítání.
3. Error state s retry.

**Výstup:** Rychlé a spolehlivé načítání obrázků.

---

### Krok 152 — Accessibility audit

**Soubory:** Všechny Compose screens

1. Ověřit `contentDescription` na všech `Icon` a `Image`.
2. Ověřit `semantics` na custom komponentách.
3. Touch target minimum 48dp.
4. Kontrast ≥ 4.5:1 pro text (WCAG AA).
5. Screen reader navigace — logické pořadí.
6. Animace respektují `Settings.Global.ANIMATOR_DURATION_SCALE` (reduced motion).

**Výstup:** Přístupná aplikace.

---

### Krok 153 — Reduced motion support

**Soubory:** Theme/Animation utility

1. Detekovat reduced motion preference:
   ```kotlin
   @Composable
   fun isReducedMotionEnabled(): Boolean {
       val context = LocalContext.current
       return remember {
           Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
       }
   }
   ```
2. Pokud enabled: přeskočit všechny dekorativní animace, zachovat jen funkční (progress bar fill).
3. `ConfettiOverlay` — skrýt.
4. `AnimatedBackground` — statický gradient.
5. `PulsingGlow` — statický glow.

**Výstup:** Respektování systémových preferencí.

---

### Krok 154 — Tablet / landscape support (základní)

**Soubory:** Compose screens

1. Použít `WindowSizeClass` pro adaptivní layout:
   ```kotlin
   val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
   if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
       // Tablet: side-by-side layout (nav rail + content)
   } else {
       // Phone: stacked layout
   }
   ```
2. Quiz na tabletu: media vlevo, otázka + odpovědi vpravo.
3. Home na tabletu: širší path, větší nodes.

**Výstup:** Základní tablet podpora.

---

### Krok 155 — ProGuard / R8 pravidla pro nové knihovny

**Soubory:** `proguard-rules.pro`

1. Přidat pravidla pro:
   - Lottie
   - Haze
   - Coil
   - Compose (většinou automaticky přes AGP)
2. Ověřit release build s minifyEnabled = true.

**Výstup:** Fungující release build.

---

### Krok 156 — App size audit

**Soubory:** Build output

1. Analyzovat APK/AAB velikost po přidání Compose + Lottie + fonts:
   - Compose runtime: ~3–5 MB
   - Lottie: ~500 KB + JSON assets
   - Font: ~500 KB
   - Haze: ~200 KB
2. Optimalizace:
   - Lottie JSON komprese.
   - Font subsetting (jen použité glyfy).
   - R8 full mode.

**Výstup:** Kontrolovaná velikost aplikace.

---

### Krok 157 — Migrace ad logiky do Compose

**Soubory:** Ad wrappers

1. `HeartsRewardAds` a `LessonInterstitialAds` obalit do Compose-friendly wrapperů:
   ```kotlin
   @Composable
   fun rememberRewardedAd(): RewardedAdState {
       val context = LocalContext.current
       val activity = context as Activity
       // load, show, callback přes state
   }
   ```
2. Interstitial po lekci: zobrazit přes `Activity` reference z `LocalContext`.

**Výstup:** Reklamy fungují v Compose.

---

### Krok 158 — Finální QA a release checklist

**Soubory:** —

1. Projít každou obrazovku a ověřit:
   - [ ] Vizuální shoda s design systémem.
   - [ ] Animace plynulé a nedráždivé.
   - [ ] Zvuky a vibrace správně mapované.
   - [ ] Dark/light mode funguje všude.
   - [ ] Edge-to-edge korektní (žádný obsah pod systémovými bary).
   - [ ] Back navigation funguje logicky.
   - [ ] Žádné memory leaky (LeakCanary).
   - [ ] Crash-free na Android 7–15 (API 24–35).
   - [ ] Landscape graceful handling (ne break).
   - [ ] Accessibility: screen reader, touch targets.
2. Release build + upload na Play Store (internal testing).

**Výstup:** Produkčně připravená aplikace.

---

## Shrnutí

| Fáze | Kroky | Hlavní zaměření |
|------|-------|-----------------|
| 1. Příprava | 1–12 | Compose deps, package struktura, cleanup, entry point |
| 2. Design systém | 13–30 | Barvy, typografie, glass komponenty, animace, Lottie |
| 3. Navigace | 31–42 | NavGraph, bottom bar, top bar, shell, tab switching |
| 4. Onboarding | 43–52 | 8-stránkový onboarding, personalizace, demo otázka |
| 5. Home | 53–65 | Lesson path (canvas křivka, nodes, sections, popup) |
| 6. Quiz | 66–85 | Otázky, odpovědi, animace, result strip, streak, combo |
| 7. Alex | 86–97 | Maskot, krmení, hunger, shop, interakce, death |
| 8. Test | 98–106 | Test hub, timer, výsledky, detaily, statistiky |
| 9. Practice | 107–114 | Kategorie grid, filtry, procvičovací mód |
| 10. Settings | 115–122 | Nastavení, achievements, changelog, splash |
| 11. Gamifikace | 123–137 | XP/leveling, challenges, power-ups, milestones |
| 12. Zvuky | 138–145 | Sound files, SoundManager, haptic, integrace |
| 13. Polish | 146–158 | Cleanup, performance, accessibility, release |

**Celkový počet kroků: 158**

> Každý krok je navržen jako samostatná implementační jednotka (1–4 hodiny práce). Kroky v rámci fáze jsou sekvenční, fáze 1–3 musí předcházet ostatním. Fáze 4–10 mohou být částečně paralelizovány, ale doporučený postup je sekvenční dle priorit (UX flow → gamifikace → vizuální polish → animace → performance).
