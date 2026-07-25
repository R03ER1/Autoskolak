# Changelog

All notable changes to this project will be documented in this file.

This file follows a simple format inspired by Keep a Changelog.

## [Unreleased]
-

## [2.2.5] - 2026-07-25
- Doplněny chybějící vizuály na 3 obrazovkách: úvodní stránky onboardingu (`OnboardingScreen.kt`) měly u prvních 4 kroků reálně prázdné lottie soubory (`onboarding_welcome.json`, `onboarding_alex.json`, `onboarding_points.json`, `onboarding_test.json` — placeholdery bez vrstev) a zobrazovalo se jen prázdné místo. Nově se v takovém případě zobrazí statická náhrada — bílá Material ikona na barevném gradientovém kruhu (stejný styl jako `SectionMilestoneBadge`/ikony lekcí), pro stránku o Alexovi existující bitmapa lva (`AlexHappy.png`).
- `TestScreen.kt` a `TestStatsScreen.kt`: prázdný stav grafu skóre (dokud uživatel nemá žádný pokus o zkoušku) měl jen jednořádkový text a velký prázdný prostor po grafu — nahrazeno ikonou grafu (`Icons.Filled.BarChart`) v gradientovém kruhu nad textem (`EmptyScoresChartPlaceholder` v `ScoresChart.kt`).
- Načítací (splash) obrazovka (`SplashScreen.kt`) — nahrazena stejná prázdná lottie animace za logo aplikace: vektor `ic_launcher_foreground` (reálná ikona appky) na gradientovém kruhu.

## [2.2.4] - 2026-07-25
- Fix (upřesnění): doplněn chybějící 5. stupeň nálady `AlexMood.Cool` (maximální radost, historicky sytost 81–100 %), který se v `AlexAssetResolver.kt` mapuje na `AlexCool.png`. Předchozí oprava (2.2.3) omylem považovala "cool" obrázky jen za placenou sluneční-brýlovou variantu — ve skutečnosti je `AlexCool.png` nejvyšší nálada v hunger-based mapování, zatímco `C`-prefix soubory (`CAlex*.png`) jsou skutečná placená varianta se slunečními brýlemi a jejich fallback logika zůstává nezměněná/nezávislá.
- `hungerPercentToMood` nyní vrací: `Cool` (81–100 %), `Happy` (80 %), `Neutral` (50–79 %), `Hungry` (20–49 %), `Starving` (0–19 %) — pouze doplnění nejvyššího pásma, ostatní hranice beze změny.

## [2.2.3] - 2026-07-25
- Fix: obrázek Alexe na Alex stránce se opět mění podle sytosti (hladu), jako před redesignem. V `AlexAssetResolver.kt` byly seznamy kandidátních souborů pro nálady popletené — pro `Happy` byl na první místě vždy existující `Alex.png` (takže se nikdy nezobrazil `AlexHappy.png`) a pro `Neutral` se místo neutrálního obrázku vracel `AlexSad.png`. Mapování nyní odpovídá historické logice (`AlexActivity.getAlexImageName`): `Happy → AlexHappy.png`, `Neutral → Alex.png`, `Hungry → AlexSad.png`, `Starving → AlexHungry.png`.
- Obrázky s "cool" (sluneční brýle za coins) zůstávají samostatné a nejsou součástí hunger mapování — jen se jako dřív prefixují `C` variantou, pokud jsou brýle zapnuté.

## [2.2.2] - 2026-07-25
- Fix: odstraněna přerušovaná spojka mezi lekcemi na hlavní obrazovce (`LessonPathBackground.kt` zcela odstraněn) — při scrollování se křivka propočítaná z měřených pozic uzlů občas viditelně "zamotávala"/přeskakovala.
- Fix: odstraněn text s počtem otázek/chyb ("10 otázek (N chyb)") vedle kolečka lekce — při delších textech (víc chyb) přetékal a zalamoval se mimo obrazovku. Popisek zůstává zachován jen pro čtečky obrazovky (accessibility label).
- Rozložení koleček lekcí na cestě (zig-zag odsazení) i sekční odznaky zůstávají beze změny.

## [2.2.1] - 2026-07-25
- Fix: ikony lekcí na hlavní obrazovce (path lekcí) nahrazeny minimalistickou sadou Material ikon namísto starých nekonzistentních bitmapových obrázků z `mediaassets`. Ikony jsou nyní bílé na barevném gradientovém kruhu, konzistentní se stylem ostatních odznaků v appce (`LessonIconMapper.kt`, `LessonNode.kt`).

## [2.2.0] - 2026-07-25
- **Milník: dokončen redesign plán na 162/165 kroků (~98 %).** Fáze 1–13 kompletní kromě kroku 165 (finální manuální QA a release checklist na fyzickém zařízení — crash-free test na API 24–35, LeakCanary, plynulost animací naživo, back navigace, edge-to-edge vzhled). Verze zvednuta na 2.2.0 jako milník ukončení implementační části redesignu; drobné opravy a doladění mohou pokračovat i po tomto milníku.

## [2.1.6] - 2026-07-25
- **Krok 142 dokončen — spaced repetition pro revizi chybných otázek** (`REDESIGN_PLAN.md`):
  - Nový čistě funkční `MistakeReviewScheduler` (bez Android/Context závislostí) — jednoduchý Leitner-like rozvrh, ne plný SM-2: intervaly 1 den / 3 dny / 7 dní; po 3 úspěšných opakováních v řadě je otázka „graduated" a mizí ze seznamu k revizi. Špatná odpověď vrátí otázku na stage 0 (okamžitě znovu k dispozici).
  - `LessonProgress`: nová čistě přídavná perzistentní vrstva `mistake_review_schedule_v1` (Gson JSON, stejný vzor jako existující `mistake_consecutive_wrong`/`practice_store`) mapující ID otázky → `ReviewScheduleEntry(stage, nextReviewAtMs)`. Existující logika (`mistake_consecutive_wrong` streak, `practice_store`) se nemění — nový rozvrh je jen navrstvená vrstva navíc, takže staré uložené progressy uživatelů se načtou bezpečně (chybějící klíč/pole = stage 0 / okamžitě k dispozici, žádná breaking migrace).
  - `getPracticeStatus(CATEGORY_USER_MISTAKES)` rozšířeno: otázky rozjeté v žebříčku (poslední odpověď správná, ale čekají na další interval) zůstávají v „wrong" množině, dokud nevyprší celý rozvrh (graduace) nebo dokud nejsou znovu odpovězeny špatně.
  - Nová `getDueUserMistakeIds()`/`getDueUserMistakeCount()` — podmnožina chyb naplánovaná k revizi dnes/dříve.
  - `ReviseMistakesScreen` a `PracticeQuestionList.buildUserMistakesList` upraveny, aby nabízely jen otázky, které jsou dnes „due" — ne celý pool chybných otázek najednou.
  - Home: nová `ReviewReminderCard` (glass card ve stylu ostatních karet) — zobrazí se jen když je alespoň 1 otázka due ("Dnes máš N otázek k opakování"), klik otevře `ReviseMistakesScreen`. Bez due otázek se karta nevykresluje (žádný prázdný stav).
  - Přidány unit testy (`MistakeReviewSchedulerTest`) pro posun stage, graduaci a bezpečnou deserializaci legacy dat bez nového pole.
  - **Otevřená otázka:** stage se posouvá při KTERÉKOLI správné odpovědi na otázku, která byla mistake (kdekoli v appce, ne jen v revizní obrazovce) — konzistentní s tím, že i dřívější "jedna správná odpověď = zmizí z chyb" fungovalo napříč celou appkou; pokud by bylo žádoucí, aby postup v žebříčku probíhal jen při odpovídání v `ReviseMistakesScreen`, je to změna k dalšímu zvážení.
- Verze bumpnuta na 2.1.6 (`versionCode` 68).

## [2.1.5] - 2026-07-25
- **Kroky 153+154 dokončeny** — odstranění posledních legacy Activity a jejich XML layoutů (`REDESIGN_PLAN.md`):
  - Smazáno 6 legacy Activity: `ResultsActivity`, `StreakActivity`, `PracticeActivity`, `TestAttemptActivity`, `TestAttemptStatsActivity`, `TestResultsActivity`. Statickou analýzou ověřeno, že tvořily uzavřený graf odkazující jen na sebe navzájem a na `HomeActivity`/`MainActivity` (ty zůstávají, dle dřívějšího rozhodnutí, jako fallback pro staré externí vstupní body — `HomeActivity` je jediná `exported="true"` legacy Activity a cíl notifikace z `HeartRefillJobService`).
  - `HomeActivity` (bottom nav „Procvičování"/„Test") a `MainActivity` (dokončení lekce → výsledky, první splnění dne → streak) byly upraveny tak, aby místo spouštění smazaných Activit přesměrovaly do Compose (`ComposeMainActivity` + nová extra `ComposeNavIntent.OPEN_TAB_PRACTICE`/`OPEN_TAB_TEST`/`OPEN_TAB_STREAK`/`OPEN_RESULTS`), takže funkčnost pro uživatele zůstává stejná, jen bez staré View-based obrazovky pod kapotou.
  - `OPEN_RESULTS` nese reálné parametry (lesson id, skóre, body, first-of-day, fromPractice) a otevře `ResultsComposeScreen` se stejnými daty, jaké dřív dostávala legacy `ResultsActivity`; body za lekci se počítají stejným vzorcem (`LessonPoints.computeLessonPointsAwarded`), jen teď přímo v `MainActivity`.
  - Smazána i mrtvá `ScoresChartView.kt` (používaná jen zrušenou `TestAttemptStatsActivity`) a 9 XML layoutů vázaných výhradně na smazané Activity (`activity_results.xml`, `activity_streak.xml`, `activity_practice.xml`, `activity_test_attempt.xml`, `activity_test_stats.xml`, `activity_test_results.xml`, `activity_blank_page.xml`, `dialog_test_details.xml`, `item_test_detail.xml`).
  - `ConfettiView`/`RingProgressDrawable` zůstávají — pořád je aktivně používá `HomeActivity` (ring progress lekcí) a `EventStyleOverlay` (náhodné události), nejsou vázané jen na smazané Activity.
  - **Otevřená otázka:** legacy test-mode a practice-category (kategorie) větve v `MainActivity` jsou po této změně nedosažitelné (nic už do `MainActivity` nepředává `EXTRA_IS_TEST_MODE=true` ani neprázdné `EXTRA_CATEGORY`) — ponechány jako neaktivní kód s komentářem, ne odstraněny, aby úprava zůstala minimální a nízkoriziková.
- Verze bumpnuta na 2.1.5 (`versionCode` 67).

## [2.1.4] - 2026-07-25
- **Krok 159 dořešení (otevřené zjištění z 2.1.2):** cca 30 míst v Compose kódu používalo `AccentCyan`/`WarningAmber` natvrdo jako barvu textu nebo informační ikony (ne přes `MaterialTheme.colorScheme`), přestože composable sedí na theme-aware (přepínajícím se light/dark) pozadí. Ve světlém režimu měly tyto jasné barvy na bílém/světlém pozadí kontrast jen ~1.4–1.5:1 (hluboko pod WCAG AA 4.5:1). Přidány zatemněné, stejně sladěné varianty `accentCyanText()`/`warningAmberText()` (`ui/theme/Color.kt`, `AccentCyanText` `#00728A`, `WarningAmberText` `#7F6B00`) — v tmavém režimu se stále používá původní jasná barva (tam je kontrast > 10:1). Opraveno v `ChangelogScreen`, `SettingsScreen`/`SettingsWidgets`, `StreakScreen`, `TopBar`, `BottomNavBar`, `QuizScreen`/`QuizTopBar`/`TestQuizSession`, `ResultsComposeScreen`, `AlexScreen`, `OnboardingScreen`, `AchievementsScreen`, `WeeklyXpScreen`, `PracticeScreen`.
- Beze změny (ověřeno, že kontrast je v pořádku i s původními barvami): čistě dekorativní použití (gradient/border/glow/pozadí/výplň progress baru) a místa s pevně tmavým pozadím bez ohledu na zvolený režim (`BonusWheelDialog`, `MysteryBoxDialog`, `CoinsSheet`, `StreakSheet`, `SplashScreen`, achievement/level-up/streak/event overlaye, `ShareCardGenerator`).
- **Otevřená otázka (neřešeno v této dávce):** barva palce/dráhy přepínače (`Switch`) v `SettingsWidgets.SwitchSetting` používá `AccentCyan` natvrdo — jde o ovládací prvek, ne o text/ikonu s popiskem, proto ponecháno k samostatnému posouzení.
- Verze bumpnuta na 2.1.4 (`versionCode` 66).

## [2.1.3] - 2026-07-25
- **Krok 155 dokončen** — odstranění mrtvých legacy XML stylů/témat (`REDESIGN_PLAN.md`):
  - Smazány prokazatelně nepoužívané styly z `res/values/styles.xml`: `Widget.Autokolk` (prázdný base styl beze zbytku), `Widget.Autokolk.BottomNav.TransparentIndicator`, `Widget.Autokolk.Popup` a `Widget.Autokolk.Popup.Animation` — nikde v projektu (layouty, manifest, Kotlin kód) na ně nevedla žádná reference.
  - Smazány mrtvé barvy z `res/values/colors.xml`: `progress_track`, `progress_indicator`, `switch_thumb_on`, `switch_track_on` — nikde nepoužité.
  - Smazán `res/values/dimens.xml` (obsahoval jen `button_margin` a `button_stroke_width`, oba nepoužité, soubor by po odstranění zůstal prázdný).
  - Zachovány (stále aktivně používané legacy Activity/layouty — `HomeActivity`, `MainActivity`, `PracticeActivity`, `TestAttemptActivity`, `TestAttemptStatsActivity` a `activity_settings.xml`): `Theme.Autokolk`/`Theme.Autokolk.Base`/`Theme.Autokolk.NoActionBar`, `Widget.Autokolk.Button`, `Widget.Autokolk.Button.Answer`, `Widget.Autokolk.Button.Primary`, `Widget.Autokolk.BottomNav.ActiveIndicator`, `TextAppearance.Autokolk.BottomNav.Large`, `ThemeOverlay.Autokolk.SwitchNeutral`.
- Verze bumpnuta na 2.1.3 (`versionCode` 65).

## [2.1.2] - 2026-07-25
- **Fáze 13 dokončena (kroky 156, 157, 158, 159, 161)** — zbytek performance/accessibility/tablet auditu z `REDESIGN_PLAN.md`:
  - **Krok 156 (recomposition audit):** projity klíčové obrazovky (Home, Alex, Test, Practice, Settings, Quiz) — LazyColumn/LazyRow položky už všude mají `key()`, datové třídy v listech jsou immutable/stabilní. Nalezena a opravena jedna zbytečná recompozice: `buildLessonTitle` (filter/sortedBy nad celým lesson plánem) v `HomeScreen` se teď počítá jen v `remember(lessonNumber, displayNumber)`, ne při každé recompozici obrazovky s otevřeným lesson sheetem. Systematické měření recomposition counts přes Layout Inspector zůstává **known limitation** — vyžaduje běžící zařízení/emulátor, které v headless prostředí nejde spustit.
  - **Krok 157 (performance audit animací):** nekonečná shimmer animace v `AnimatedProgressBar` a `Modifier.shimmerLoading` (používaná na desítkách míst — Home sekce, Alex hunger bar, Practice karty, image loading placeholder) se nyní v nízkovýkonném/reduced-motion režimu (`rememberLowPerformanceModeEnabled`) vypíná a nahrazuje statickým gradientem. `ConfettiOverlay` je aktuálně jen no-op stub (žádné particles), takže tam nebylo co optimalizovat. Reálné FPS profilování na fyzickém zařízení zůstává **known limitation**.
  - **Krok 158 (image loading):** `AssetImageFromPath` (hlavní cesta pro veškerá lokální média — ikony lekcí, obrázky otázek) nyní dekóduje bitmapy asynchronně na `Dispatchers.IO` (dřív blokovalo hlavní vlákno při každé recompozici), se shimmer placeholderem během načítání a error stavem s tlačítkem "Zkusit znovu" při chybě. `App.kt` implementuje `SingletonImageLoader.Factory` s centrálním Coil `ImageLoader` (memory cache 25 %, disk cache 10 % v `cacheDir/image_cache`, crossfade) — připraveno pro budoucí síťový obsah, byť ho appka dnes aktivně nevyužívá (žádné síťové obrázky, vše je v `assets/`).
  - **Krok 159 (accessibility, dokončení kontrastu):** spočítány WCAG kontrast poměry (relative luminance formule) pro všechny hlavní text/pozadí kombinace ve všech 3 vizuálních stylech (Classic, Neon Grid, Sunset Warm) × light/dark = 36 kombinací. Nalezeno a opraveno 5 porušení AA (< 4.5:1): `ErrorRed` ztemněn z `#FF1744` na `#ED002E` (3.85:1 → 4.53:1 s bílým textem); Classic light `onPrimary`/`onSecondary` změněny z bílé na tmavý text (2.3–2.4:1 → 6.8–7.0:1); Neon Grid light `secondary` ztemněna z `#0891B2` na `#07819E` (3.68:1 → 4.52:1); Sunset Warm light `primary` ztemněna z `#E65100` na `#CC4800` (3.79:1 → 4.67:1) a `onTertiary` změněn na tmavý text (2.82:1 → 5.65:1). Ve všech případech zachován původní odstín/styl, jen mírná úprava lightness nebo volba textové barvy. Doplněn touch target 48dp u řádku "Podkategorie"/expand ikonky v `PracticeScreen` (dřív klikatelná plocha jen 24dp). **Otevřené zjištění:** cca 30 míst v UI používá fixní `AccentCyan`/`WarningAmber` přímo jako barvu textu (ne přes `MaterialTheme.colorScheme`) — v light režimu na běžném bílém pozadí to může místy nesplňovat AA; vyžaduje samostatný per-místo audit (viz `REDESIGN_PLAN.md`).
  - **Krok 161 (tablet/landscape):** lightweight vlastní řešení přes `LocalConfiguration.current.screenWidthDp` s prahem 600dp (`ui/util/WindowSize.kt`) — bez nové Gradle závislosti na `androidx.compose.material3.windowsizeclass`. Home dostal na širokých obrazovkách širší sinusovou cestu (250dp → 420dp amplituda) a větší uzly lekcí (64dp → 80dp). Quiz i Test obrazovka v expanded landscape (tablet na boku) zobrazí otázku s médiem v řádku (obrázek/video vlevo, otázka a odpovědi vpravo, scrollovatelné) místo pod sebou — běžný telefon na boku (< 600dp šířky) layout nemění.
- Verze bumpnuta na 2.1.2 (`versionCode` 64).

## [2.1.1] - 2026-07-25
- **Kritická oprava pádu release buildu při startu** (appka po dokončení lekce spadla a nešla už nikdy znovu spustit). Root cause: ProGuard/R8 pravidlo z 2.0.70/2.1.0 pro Gson modely (`Question`, `LessonProgress.LessonStateJson`, `LessonProgress.PracticeStore`) používalo `-keepclassmembers`, což chrání jen pole tříd, ale nechrání identitu/strukturu samotné třídy. Protože tyto třídy jsou vytvářeny čistě reflexí (Gson `TypeToken`, žádné přímé `new` volání v kódu), R8 je vyhodnotil jako bezpečné ke sloučení s jinou třídou (class merging) — Gson pak při deserializaci uloženého progresu vrátil objekt jiného runtime typu než očekávaný, což skončilo `ClassCastException` hned v konstruktoru `HomeViewModel`. Oprava: pravidla přepsána na `-keep class ... { *; }`, což zachovává plnou identitu tříd a zabraňuje jejich sloučení/odstranění. Ověřeno v `mapping.txt` po `assembleRelease` — všechny tři třídy i anonymní `TypeToken` podtřídy si zachovávají původní název/identitu.

## [2.1.0] - 2026-07-25
- Stabilní milník pro externí testování (kamarádi). Shrnuje a uzavírá sérii oprav a vylepšení 2.0.64–2.0.71: legacy cleanup, odznaky/milníky na cestě lekcí, sdílení PNG karty, shared element transition (lekce → kvíz), plošná oprava světlého/tmavého režimu, perf/accessibility audit (reduced motion, battery saver, TalkBack popisky), ProGuard/R8 + zmenšení velikosti aplikace a oprava úklidu videa v kvízu (odstranění rušivých logcat hlášek).

## [2.0.71] - 2026-07-24
- Oprava úklidu videa v kvízu (`QuizMedia`): `VideoView` (interně používá `SurfaceView`) se při opuštění otázky/obrazovky teď korektně zastaví (`stopPlayback()`), místo aby zůstal MediaPlayer připojený k už zrušenému Surface. To odstraňuje neškodné, ale rušivé logcat hlášky `BufferQueueProducer: ... BufferQueue has no connected producer`, které se objevovaly kolem otevírání lekcí s video otázkami.

## [2.0.70] - 2026-07-24
- Performance a accessibility audit (kroky 156–160 z `REDESIGN_PLAN.md`): nové utility `ui/util/ReducedMotion.kt` (`rememberReducedMotionEnabled` — respektuje systémové "Odstranit animace" přes `Settings.Global.ANIMATOR_DURATION_SCALE`) a `ui/util/PerformanceMode.kt` (`rememberPowerSaveModeEnabled` přes `PowerManager.isPowerSaveMode`, kombinovaný `rememberLowPerformanceModeEnabled`). Aplikováno na čistě dekorativní nekonečné animace: `AnimatedBackground` (statický glow), `PulsingGlow` na Home lesson node, shimmer v `PrimaryGradientButton`, "dýchací" smyčka Alexe (`AlexCharacter`), Lottie smyčky a konfety v `LevelUpOverlay`/`StreakMilestoneOverlay`/`EventOverlay`/`AchievementUnlockOverlay` (nejnáročnější fullscreen particle efekt v appce se v reduced motion úplně vypne, v battery saveru přehraje jen jednou).
- Accessibility audit (krok 159): TalkBack popisky doplněny/opraveny na `BottomNavBar` (ikona vybrané záložky už neduplikuje čtení viditelného labelu), `TopBar` (streak/mince/životy měly `contentDescription` chybějící kontext, protože číslo se kreslí po jednotlivých cifrách — doplněn souhrnný popisek), `AnswerButton` (ikony správně/špatně teď TalkBacku oznámí výsledek odpovědi) a `LessonNode` na Home cestě lekcí (uzel dřív neměl žádný vlastní popisek, protože titulek lekce je samostatný text mimo klikatelný strom).
- Drobná recomposition oprava: mapování ikony předmětu lekce (`HomeScreen`) se teď počítá v `remember` místo na každou recompozici řádku.
- ProGuard/R8 audit (kroky 161–163): doplněna `-keepclassmembers` pravidla pro Gson-reflektované modely bez `@SerializedName` (`Question`, `LessonProgress.LessonStateJson`, `LessonProgress.PracticeStore`) — bez nich by R8 v release buildu mohl přejmenovat jejich pole a rozbít načítání existujícího uloženého JSON progresu. Zapnuto `isShrinkResources = true` v release buildu (spolu s již existujícím `isMinifyEnabled = true`), ověřeno plným `assembleRelease`: velikost release APK klesla ze 7,53 MB na 6,83 MB (~9,3 % úspora, cca 0,70 MB). Tablet/landscape support (krok 161) zůstává jen na úrovni auditu — klíčové obrazovky nepoužívají hardcoded plné šířky (převažuje `fillMaxWidth()`/`fillMaxWidth(0.92f)`), takže by neměly na tabletu vizuálně "rozbít" layout, ale `WindowSizeClass` adaptivní layout zatím implementován není.

## [2.0.69] - 2026-07-23
- Oprava kontrastu textu a barev napříč světlým/tmavým režimem (audit celé Compose vrstvy). Root cause "prosvítajícího" glow prstenu při přepnutí na Světlý režim: `Scaffold` mělo průhledný `containerColor`, takže prosvítal statický tmavý `android:windowBackground` z legacy XML tématu (ten se nemění podle volby v Nastavení) — `AnimatedBackground` navíc kreslilo gradient s jedním pevným (tmavým) barevným zastávkovým bodem uprostřed, což vytvořilo viditelný kruh. Oprava: `AutokolkApp.kt` má nový opakní `MaterialTheme.colorScheme.background` podklad pod celým `Scaffold` a `AnimatedBackground` nejprve vyplní plochu plnou theme-správnou barvou a až pak přidá čistě průhledný (na obou koncích) animovaný glow.
- Nový `GlassTone`/`glassPalette` (`ui/theme/GlassTokens.kt`) — `GlassCard`/`GlassButton` nyní respektují aktuální theme (dřív měly natvrdo tmavé "glass" barvy, takže byly ve Světlém režimu prakticky nečitelné/splývaly s pozadím). Overlaye s úmyslně pevným tmavým pozadím (tutorial, event, no-lives) používají `GlassTone.Dark`, aby si zachovaly svůj vzhled v obou režimech.
- Nahrazeny natvrdo bílé `TextPrimary`/`TextSecondary`/`TextTertiary` a `GlassWhite`/`GlassFill` za `MaterialTheme.colorScheme.onSurface`/`onSurfaceVariant` (s odpovídající alfou) ve všech theme-reaktivních obrazovkách a komponentách: Home, Alex (`AlexScreen`, `AlexDeathScreen`, `ShopSheet`, `FoodMenuSheet`, `CoinShopScreen`, `ReviseMistakesScreen`), Practice, Test/Test výsledky/statistiky, Nastavení (`SettingsWidgets`), Onboarding, čtecí lekce (`ReadingLessonComposeScreen`), historie změn (`ChangelogScreen`), achievementy, streak, týdenní XP, top/bottom navigace, odpovědní tlačítka v kvízu, quiz power-up řádek a horní lišta kvízu, graf skóre (`ScoresChart`), shimmer loading efekt, progress bary (`QuizProgressBar`, `AnimatedProgressBar`), média placeholdery v kvízu (`QuizMedia`).
- Opraven kvízový dialog "Ukončit lekci?" (`QuizScreen`), který měl natvrdo tmavé pozadí (`containerColor = DarkSurfaceVariant`), ale theme-aware text — ve Světlém režimu tak vznikal tmavý text na tmavém pozadí. Stejná oprava už dřív proběhla u `TestQuizSession`.
- Overlaye/dialogy s úmyslně pevným tmavým pozadím (achievement unlock, level-up, streak milestone, bonus wheel, mystery box, splash screen, karty s mincemi/streakem/životy) byly ověřeny a zůstávají beze změny — jejich bílý text je záměrně pevný a čitelný v obou režimech.

## [2.0.68] - 2026-07-23
- Shared element transitions (krok 41): `NavGraph.kt` obalen `SharedTransitionLayout`, nové `LocalSharedTransitionScope`/`LocalNavAnimatedVisibilityScope` (`ui/navigation/SharedElementTransition.kt`) zpřístupňují scope hluboko ve stromu bez protahování parametrů. Kolečko lekce na Home cestě (`LessonNode`) má volitelný `transitionKey` — při navigaci do kvízu plynule morphuje (`Modifier.sharedBounds`) pozici a velikost do "hero" pilulky s číslem otázky v hlavičce Quiz obrazovky (`QuizTopBar`), místo obyčejného hard-cut/slide přechodu. Použito nativní Compose Foundation API (BOM 2026.03.01 / navigation-compose 2.9.7 jsou dostatečně nové), žádný upgrade knihoven nebyl potřeba.

## [2.0.67] - 2026-07-23
- Odznaky/milníky na lesson path (krok 141): nová komponenta `SectionMilestoneBadge` (trofej s pop-in animací) se vkládá na cestu lekcí za každou zcela dokončenou sekci (Základní pojmy, Začátečník, Pokročilý, ... Skoro hotovo!). Odemčení se odvozuje z existujícího progressu (`HomePathListBuilder`), první zobrazení se persistuje v `LessonProgress` a spouští `SoundManager.ACHIEVEMENT` + `HapticFeedback.onAchievement`.
- Sdílení jako PNG karta (krok 143): týdenní souhrn (`WeeklyXpScreen`) se teď sdílí jako vizuální obrázek (streak, plamínek, statistiky za 7 dní) přes nový `ShareCardGenerator`, ne jen jako čistý text. Bitmapa se kreslí klasickým `Canvas`+`Paint` (kompatibilní s minSdk 24), ukládá se do cache a nasdílí přes nově přidaný `FileProvider` (`res/xml/file_paths.xml`). Při chybě generování obrázku tichý fallback na původní textové sdílení.

## [2.0.66] - 2026-07-23
- Legacy cleanup (kroky 153–154 z `REDESIGN_PLAN.md`): odstraněna nepoužívaná `LoadingActivity` (nahrazena `ComposeMainActivity` jako launcherem, nikde v kódu už nebyla spouštěna) včetně `activity_loading.xml` a záznamu v `AndroidManifest.xml`.
- Odstraněna mrtvá view třída `CurvyPathView` a její layouty `curvy_lesson_path.xml`, `item_lesson_curvy.xml` — `HomeActivity` už dávno kreslí lekce jako jednoduchý vertikální seznam.
- Odstraněno 11 osiřelých XML layoutů z doby před přechodem na Compose, které už nebyly odkazované z žádného `R.layout.*` (`activity_achievements.xml`, `activity_alex.xml`, `activity_alex_death.xml`, `activity_changelog.xml`, `fragment_alex_page_one.xml`, `fragment_alex_page_two.xml`, `fragment_alex_shop_sunglasses.xml`, `item_category_header.xml`, `item_info_button.xml`, `item_subcategory_header.xml`, `view_hunger_overlay.xml`).
- `EventOverlay` (Compose `RandomEventOverlay`) už nezabaluje legacy `ConfettiView` přes `AndroidView` — používá stejnou Compose komponentu `ConfettiOverlay` jako `LevelUpOverlay`, `ResultsComposeScreen` a další obrazovky.
- Ověřeno (a záměrně NEPROVEDENO): `ResultsActivity`, `StreakActivity`, `PracticeActivity`, `TestAttemptActivity`, `TestAttemptStatsActivity`, `TestResultsActivity` zůstávají — jsou aktivně používané z `HomeActivity`/`MainActivity` (drženy jako fallback pro staré deep linky), jejich smazání by rozbilo kompilaci. Stejně tak zůstávají `ConfettiView`, `ScoresChartView`, `RingProgressDrawable` (stále využívané legacy Activity vrstvou) a všechny jimi používané XML layouty.

## [2.0.65] - 2026-07-23
- Zvýšen `targetSdk` z 35 na 36 (Android 16) — požadavek Google Play na cílení nejnovější dostupné API úrovně. `compileSdk` byl už na 36, takže šlo čistě o změnu `targetSdk` v `app/build.gradle.kts` (`defaultConfig`).
- Zkontrolován dynamic feature modul `:mediaassets` — nemá vlastní `targetSdk` (dědí z `app`), žádná úprava potřebná.
- Zkontrolována verze AGP (9.3.1) a Gradle wrapperu (9.5.0) — obě verze `targetSdk 36` podporují, upgrade nebyl nutný.
- Zkontrolován `AndroidManifest.xml` a kód kolem chování Android 16 (predictive back, `PendingIntent` mutabilita, foreground services, broadcast receivery) — nic nevyžadovalo úpravu.

## [2.0.64] - 2026-07-23
- Debug buildy nyní používají oficiální Google test AdMob ID (produkční ID zůstávají v release buildu) — zdroj pravdy je `app/build.gradle.kts` (`buildConfigField` pro `ADMOB_INTERSTITIAL_ID` a `ADMOB_REWARDED_ID`).
- Sjednocení rewarded ad unit ID do jedné konstanty (`BuildConfig.ADMOB_REWARDED_ID`) — `HeartsRewardAds` i `RewardedAdHelper` čtou z jednoho místa, žádné kopírované řetězce.
- `LessonInterstitialAds.AD_UNIT_ID` také čte z `BuildConfig.ADMOB_INTERSTITIAL_ID`.
- `App.onCreate()` po `MobileAds.initialize` loguje `[Ads] using TEST/PROD ad unit IDs` s tagem `InterstitialAd` pro rychlou kontrolu v logcatu.
- AdMob App ID v `AndroidManifest.xml` zůstává produkční (dle Google doporučení).

## [2.0.63] - 2026-07-23
- Fáze 12 krok 164: interstitial reklamy integrovány do Compose flow — reklama se pokusí zobrazit na primárním CTA („Zpět na cestu") v `ResultsComposeScreen` po dokončení lekce, mimo procvičování / test / firstOfDay streak. Reklama je předem předpřipravena při vstupu do lekce (`QuizScreen`) přes `LessonInterstitialAds.preload`, takže se zobrazí okamžitě.
- Zavedeno pravidlo „reklama po každé 3. dokončené lekci" s grace window: prvních 3 dokončených lekcí je bez reklam. Sjednocené počítadlo `InterstitialAdController` (SharedPreferences) sdílí Compose flow i legacy `ResultsActivity` / `MainActivity`.
- Když v okamžiku CTA není reklama nachystaná, navigace není blokovaná — reklama se přeskočí a počítadlo se resetuje (nebudujeme frontu reklam).
- Debug sekce v Nastavení: „Vynutit interstitial reklamu" a „Reset počítadla reklam" (jen `BuildConfig.DEBUG`).
- Log breadcrumby s prefixem `[Ads]` (tag `InterstitialAd`) pro filtrování v `logcat`.

## [2.0.62] - 2026-07-23
- Fáze 12: zvuky napříč aplikací — placeholder OGG/WAV zvuky v `res/raw` (procedurálně generované ze skriptu `scripts/generate_placeholder_sounds.py`), rozšířený `SoundManager` o eventy `COMBO`, `WHOOSH`, `ACHIEVEMENT`, `WHEEL_TICK`, `WHEEL_WIN` a napojení: kvíz (správně/špatně/combo/countdown v posledních 5 s testu), spodní navigace (jemné klepnutí), spodní sheety (svištění při otevření), Alex (krmení + interakce), overlay úspěchů a level-upu, bonusové kolo (tikot + výherní jingle), mystery box (výherní jingle), milník streaku.
- Sjednocena haptická odezva přes centrální `ui/util/HapticFeedback.kt` — sémantické API `onCorrect/onWrong/onCombo/onTap/onCountdown/onAchievement/onMilestone` volatelné jak z Compose (View), tak z ViewModelů (Context). Odstraněny ad-hoc `Vibrator`/`vibrate()` volání v `QuizViewModel`, `TestViewModel`, `StreakScreen` a `AchievementUnlockOverlay`.
- `SoundManager` i `HapticFeedback` respektují přepínače „Zvuky" a „Vibrace" v `AppSettingsStore` na každém volání.
- Opraven zavádějící podtitulek u „Revize chyb" v Nastavení („Spaced repetition — tvoje chyby" → „Procvič otázky, ve kterých jsi chyboval").

## [2.0.61] - 2026-05-23
- Fáze 11 krok 140: týdenní souhrn — rozšířený `WeeklyXpScreen` o kartu „Souhrn týdne“ (lekce dokončeno, aktivních dní X/7, aktuální streak, osobní rekord) a tlačítko „Sdílet týdenní výsledek“ přes systémový share. Přidán periodický `WeeklySummaryWorker` (WorkManager, neděle 18:00, idempotentní `KEEP`), nový notifikační kanál „Týdenní souhrn“ a deep-link `OPEN_TAB_WEEKLY_XP` z notifikace přímo na obrazovku. `LessonProgress` nově sleduje počet dokončených lekcí za den (`lessons_per_day`, forward-only).

## [2.0.60] - 2026-04-16
- Fáze 11 krok 138: centrální obchod — tři motivy aplikace (Klasický zdarma, Neon mřížka, Západ slunce) s vlastními barvami Material 3, úpravou typografie a tvarů karet; nákup a přepnutí motivu za mince v obchodě. Alex: nové sloty čepice, šála a párty pozadí (emoji placeholdery, zapnutí/vypnutí po koupi), brýle přesunuty do stejného obchodu; list Alex „Obchod“ odkazuje na hlavní obchod. `PrimaryGradientButton` respektuje aktivní barevné schéma.

## [2.0.59] - 2026-04-16
- Oslavy milníků streaku (7 / 30 / 100 / 365 dní): celoobrazovkový overlay s konfety, zvukem a textem; na výsledku lekce po level-up overlay; při návratu do aplikace se zobrazí i zůstal-li pending v úložišti. `AutokolkApp` neodčítá pending na trasách kvízu a výsledku, aby se nekolidovalo s výsledkovou obrazovkou.

## [2.0.58] - 2026-04-15
- Nastavení (jen debug build): sekce „Debug — obchod“ — otevření bonusového kola a mystery boxu, skok do obchodu, reset denních limitů kola/boxu pro testování.

## [2.0.57] - 2026-04-15
- Obchod a bonusy: animované bonusové kolo a mystery box (výsledek až po animaci, zvuk a haptika), přehled zbývajících pokusů na obrazovce obchodu; `LessonProgress` rozšířen o počty zbývajících točení/otevření a jednotný bonus XP u krabičky.

## [2.0.56] - 2026-04-15
- Fáze 11 (gamifikace): XP a úrovně, odměna při level-up na výsledku lekce, denní výzvy na Home, zmrazení streaku za mince, milníky streaku a koruna u Alexe, power-upy v lekci (eliminace / přeskočení / nápověda), lokální týdenní přehled XP, rozšířené achievementy, 2× XP z rewarded reklamy, bonusové kolo a mystery box (denní limity + pity), denní přihlášení, sezónní banner, revize chyb, obchod s bonusy, sdílení streaku, widget na ploše (streak + výzvy).

## [2.0.55] - 2026-04-15
- Fáze 10: Nastavení v Compose (profil, téma Systém/Světlý/Tmavý, zvuky, vibrace, biometrický zámek, denní cíl, úspěchy, changelog, smazání postupu), obrazovky Úspěchy a Historie změn; splash s Lottie a lepší stav stahování DFM; nový celoobrazovkový efekt při odemčení úspěchu (Lottie + konfety). Odstraněny legacy Settings/Achievements/Changelog Activity.

## [2.0.54] - 2026-04-15
- Sloučení dynamických modulů `imageassets` + `videoassets1`–`5` do jednoho modulu `mediaassets` (~155 MB souborů na disku; limit Play pro jeden feature modul je 200 MB komprimovaného stažení). Zjednodušená konfigurace Gradle a jedna žádost Split Install pro obrázky i videa.

## [2.0.53] - 2026-04-15
- Aktualizace závislostí: Kotlin 2.2.21, KSP 2.2.21-2.0.5, AndroidX (core-ktx 1.18, lifecycle 2.10, activity-compose 1.13, WorkManager 2.11.2, Room 2.8.4, testy junit/espresso), Material 1.13, Gson, Commons CSV, Play Services Ads a UMP přes `libs.versions.toml`; WorkManager sjednocen z katalogu místo natvrdo 2.8.1.

## [2.0.52] - 2026-04-15
- Fáze 9 (Procvičování): Compose `PracticeScreen` + `PracticeViewModel` (grid kategorií, filtry Všechny / Nenaučené / Chybné / Správně, podkategorie, „Tvoje chyby“, vyhledávání v CSV, náhled v `ModalBottomSheet` + start kvízu).
- Trasa `Route.PracticeQuiz` a rozšířené `Route.Results` (návrat na procvičování, „Zkusit znovu“ s replay argumenty); `QuizViewModel` / `QuizScreen` podporují `QuizSession.Practice` (bez životů, `savePracticeAnswer`, mince po každých 5 zobrazených otázkách, streak při dokončení).
- `LessonProgress`: `getQuestionsForCategory` s volitelnou podkategorií, `searchQuestions`; `PracticeQuestionList` sdílí logiku výběru otázek s legacy praxí.

## [2.0.51] - 2026-04-15
- Zkouška (Compose): oficiální složení 25 otázek / 50 bodů (Def+Prav, Znak, Bez, Res, Voz, Souv, Med) jako u klasické zkoušky; řádek s kategorií a bodováním pod progress barem.
- Test: viditelná okamžitá volba odpovědi (mapa odpovědí ve stavu); „Dokončit test“ i s nevyplněnými otázkami; skóre a zápis pokusu podle vah otázek (ne průměr z počtu správně).

## [2.0.50] - 2026-04-15
- Oprava buildu po aktualizaci nástrojů: `TestAttemptDao` používá blokující dotazy (`*Blocking`) místo `suspend`, aby Room KSP znovu generoval `TestAttemptDao_Impl` bez konfliktu `Continuation<? super T>` vs `Continuation<T>` u `compileDebugJavaWithJavac`.

## [2.0.49] - 2026-04-15
- Fáze 8 (Zkouška): Compose `TestScreen` (hub, graf, odkaz na statistiky), `TestQuizSession` s `TestViewModel` (25 otázek, 30 min, countdown 3–2–1, `HorizontalPager`, dokončení), `TestResultsScreen` + `Route.TestResults` s `Long` ID, `TestStatsScreen`, `ScoresChart` (Canvas + animace).
- Room (`test_attempts`, `test_answer_rows`), `TestAttemptRepository`, migrace historických skóre z `LessonProgress` prefs; po dokončení testu dual-write `addTestScore` + záznam pokusu s detaily odpovědí.
- `QuizViewModel` už neobsahuje testový režim; v testu se v průběhu nezobrazuje správnost odpovědí u tlačítek (`QuestionContent`).

## [2.0.48] - 2026-04-15
- Fáze 7 (Alex): Compose `AlexScreen`, `AlexCharacter`, `FoodMenuSheet`, `ShopSheet` (brýle + „Již brzy“), `AlexViewModel` napojený na `HungerManager` / `LessonProgress`, animace krmení a `FloatingReward` za útratu bodů.
- `AlexDeathScreen` v navigaci (podrž 3 s, oživení na 50 %, confetti); odstraněny `AlexActivity` / `AlexDeathActivity` a související fragmenty z manifestu; legacy navigace na Alex přes `ComposeMainActivity` + `ComposeNavIntent`.
- Notifikace hladu: prahy 50 / 20 / 5 %, BigPicture, akce „Nakrmit“, deep link na Alex; `HungerManager.millisUntilNextNotificationBandEdge` + `resetTierFlags`.
- Zvuky: `SoundManager` rozšířen o `ALEX_TAP` / `ALEX_FEED` (raw soubory volitelné).
- Volitelné PNG v `imageassets`: `AlexSadC`, `AlexFamine` a CAlex varianty (fallback na stávající výrazy).

## [2.0.47] - 2026-04-15
- Fixed: pád kvízu při vibraci — doplněno `android.permission.VIBRATE` v manifestu; `QuizViewModel.vibrate` kontroluje oprávnění a ignoruje `SecurityException`.

## [2.0.46] - 2026-04-15
- Fixed: `AnswerButton` — částice po správné odpovědi přes `drawBehind` místo `Canvas` nad tlačítkem (zabrání crashi / blokování klepnutí při animaci burst).

## [2.0.45] - 2026-04-15
- Fáze 6 (Quiz Experience): `AnimatedBackground`, životy a combo v `QuizTopBar`, časovač testu (barvy, pulz, zvuk pod 10 s), `QuizPowerUpRow` (placeholder nápověd), `FloatingReward` po správné odpovědi, výběr odpovědi s prodlevou a vibracemi, částice u správně, `QuizResultStrip` s emoji, fun facts a vysvětlením, overlay „došly životy“ s rewarded reklamou (`QuizNoLivesOverlay`), video v `QuizMedia` přes `VideoAssetFileCache`.
- Body za lekci: sdílený výpočet `LessonPoints` (stejná pravidla jako `ResultsActivity`), připsání v `QuizViewModel` + argumenty `Route.Results` (`firstOfDay`, `pointsAwarded`).
- Výsledky: `ResultsComposeScreen` (Lottie, kruh, statistiky, count-up, navigace na streak / opakování kvízu), `StreakScreen` místo placeholderu v `NavGraph`.
- Data: `Question.funFact` / `explanation`, `driving_fun_facts.txt` + `DrivingFunFacts`, Lottie `correct_answer.json` / `broken_heart.json`.

## [2.0.44] - 2026-04-15
- Fixed: při schovávání `QuizResultStrip` po správné odpovědi zůstává zelený tón (stav ve ViewModelu se mezitím vymaže dřív, než doběhla exit animace).
- Fixed: `QuizScreen` a `ReadingLessonComposeScreen` — `statusBarsPadding` + `displayCutoutPadding`, aby obsah nekolidoval s výřezem kamery a status barem.

## [2.0.43] - 2026-04-15
- Home: křivka pozadí jen z měřených uzlů (bez syntetiky přes celou výšku) + ořez výkresu na viewport; opakované klepnutí na „Domů“ znovu posune seznam k aktuální lekci.
- Home: uzly lekcí (CURRENT/LOCKED) — modrozelený gradient z palety (`AccentCyan` / `AccentTeal` / `AccentBlue`) místo duhy podle sekce.

## [2.0.42] - 2026-04-15
- Fáze 5 (Home / lesson path): měřené pozice uzlů pro křivku pozadí (`LessonPathBackground` + `PathMeasure`), animovaný scroll na aktuální lekci, sekční hlavičky v `GlassCard` s `AnimatedProgressBar`.
- Multi-step spotlight tutoriál na Home (`HomeTutorialSpotlightOverlay`), zápis do `TutorialManager` pro náhodné události.
- Náhodné události v Compose (`RandomEventManager.consumeDueRandomEventForCompose`, `RandomEventOverlay` + `ConfettiView`).
- Čtecí lekce: odstraněna `ReadingLessonActivity` a layout `activity_reading_lesson`, overlay z `MainActivity` / `HomeActivity` s `ReadingLessonComposeScreen` a `ReadingLessonExternalExit`.
- `LessonProgress.prefsRevision` (`StateFlow`) pro reaktivní UI.

## [2.0.41] - 2026-04-15
- Fixed: `PrimaryGradientButton` — shimmer přes `drawWithContent` místo sibling `Box(fillMaxSize)`, aby se při puštění prstu tlačítko znovu neroztáhlo na celou šířku rodiče (onboarding Přeskočit/Další).

## [2.0.40] - 2026-04-15
- Fixed: Onboarding patička — CTA max 400dp, vycentrované; řádek Přeskočit/Další bez roztažení přes celou šířku.
- Fixed: `PrimaryGradientButton` — shimmer uvnitř vnitřního `wrapContentWidth` Boxu, aby tlačítko nemělo divnou výšku/šířku při `fillMaxWidth`.

## [2.0.39] - 2026-04-15
- Added: Fáze 4 onboarding — `OnboardingScreen` (HorizontalPager), info stránky s Lottie, denní cíl, pojmenování lva, demo otázka, na Android 13+ krok s žádostí o notifikace (`ui/screens/onboarding/`).
- Added: `OnboardingPreferences` (`onboarding_prefs`), `AnimatedBackground`, `OnboardingData` / `buildOnboardingSteps()`.
- Added: `LessonProgress` — počítadlo lekcí dnes, `getDailyGoal()`, `getLessonsCompletedToday()`, `isDailyGoalMet()`, `registerOnLessonProgressChanged()` / `unregister…`.
- Added: `AnswerButton`, `RingProgress`, `QuizProgressBar`, `ConfettiOverlay` (stub), závislost `material-icons-extended`.
- Changed: `SplashScreen` po splashu vede na onboarding, pokud ještě není dokončen; `NavGraph` napojuje `OnboardingScreen`.
- Changed: `PrimaryGradientButton` shimmer používá `fillMaxSize()` místo `matchParentSize`; oprava `BottomNavBar` padding API.

## [2.0.38] - 2026-04-15
- Added: Animovaný bottom navigation bar s glassmorphism stylem, spring animacemi ikon a glow efektem (`ui/components/navigation/BottomNavBar.kt`).
- Added: Top bar se statistikami (streak, coins, lives) s pulsující animací při nízkém počtu životů (`ui/components/navigation/TopBar.kt`).
- Added: `AnimatedCounter` composable s rolling digit animací (`ui/components/animation/AnimatedCounter.kt`).
- Added: Tab navigation logika `navigateToTab` — prevence stack leaku, back vždy na Home (`ui/navigation/NavigationExtensions.kt`).
- Added: Streak bottom sheet s Lottie animací, 7-denní heatmapou a ochranou streak přes rewarded ad (`ui/components/sheets/StreakSheet.kt`).
- Added: Hearts bottom sheet s animovanými srdíčky, odpočtem do dalšího života a rewarded ad (`ui/components/sheets/HeartsSheet.kt`).
- Added: Coins/XP informativní bottom sheet (`ui/components/sheets/CoinsSheet.kt`).
- Added: Compose-friendly `RewardedAdHelper` wrapper pro rewarded reklamy (`ui/components/sheets/RewardedAdHelper.kt`).
- Added: 7-denní streak history tracking v `LessonProgress` (`getStreakHistory()`, `recordCompletionDate()`).
- Changed: `AutokolkApp` přepsán na Scaffold s animovaným top/bottom barem, sheet stavem a SharedPreferences listener.
- Changed: `SplashScreen` nyní obsahuje kompletní DFM install, UMP ads consent a terms dialog (přesunuto z `LoadingActivity`).
- Changed: Launcher Activity přepojen z `LoadingActivity` na `ComposeMainActivity` v manifestu.

## [2.0.37] - 2026-04-15
- Added: Centrální `NavGraph` — `AutokolkNavGraph` s `NavHost`, přechody (fade pro taby, slide pro detail, slide-up pro modály), napojení všech existujících obrazovek a placeholdery pro zatím neimplementované (`ui/navigation/NavGraph.kt`).
- Changed: `AutokolkApp` nyní vytváří `NavHostController` a spouští `AutokolkNavGraph` místo placeholder textu.

## [2.0.36] - 2026-04-15
- Added: Type-safe navigation routes — `Route` sealed class s 12 statickými a 4 parametrickými routami, argument konstanty a `mainTabs` helper (`ui/navigation/Routes.kt`).
- Marked: Kroky 19–30 (fáze 2 design systém) označeny jako hotové v REDESIGN_PLAN.md.

## [2.0.35] - 2026-04-14
- Added: `PrimaryGradientButton` — CTA s gradientem, animovaným stínem (glow), pružinovým stiskem a volitelným shimmer přes gradient.

## [2.0.34] - 2026-04-14
- Added: Tlačítka glass/CTA — `GlassButton` (slot + pružinová animace), `PrimaryButton`, `SecondaryButton`, `DangerButton` (`ui/components/buttons/GlassButton.kt`).

## [2.0.33] - 2026-04-14
- Added: `GlassCard` a `GlassCardBlur` — glassmorphism karty (`GlassCard` gradient + border; `GlassCardBlur` s Haze `hazeEffect` a `HazeMaterials.thin`).

## [2.0.32] - 2026-04-14
- Added: Design tokeny — `Tokens.kt` s `AutokolkTokens` (elevation, spacing, glass, délky animací, ikony a layout výšky).

## [2.0.31] - 2026-04-14
- Added: Material 3 tvary — `Shape.kt` (`AutokolkShapes`, `PillShape`, `BottomSheetShape`, `LessonNodeShape`) a zapojení `shapes` do `AutokolkTheme`.

## [2.0.30] - 2026-04-14
- Added: Compose typografie — `Type.kt` s rodinou fontů Quicksand a `AutokolkTypography` (Material 3 hierarchie), zapojeno do `AutokolkTheme` přes `MaterialTheme.typography`.

## [2.0.29] - 2026-04-14
- Added: Compose ColorScheme & MaterialTheme — `darkColorScheme()` and `lightColorScheme()` wired into `AutokolkTheme`, user dark/light mode preference persisted in SharedPreferences via `ThemeMode` enum, `CompositionLocalProvider` exposes `LocalIsDarkTheme` to all composables.

## [2.0.28] - 2026-04-14
- Added: Light mode color palette (`Color.kt`) — light backgrounds, light glass-effect tokens, adjusted accent colors for light surfaces, and dark text variants. Enables future light/dark theme switching.

## [2.0.27] - 2026-04-14
- Added: Dark mode color palette (`Color.kt`) — backgrounds, glass-effect tokens, accent colors (cyan/teal/blue gradient), semantic colors (success/error/warning/info) and text opacity variants for glassmorphism design system.

## [2.0.26] - 2026-04-14
- Added: Edge-to-edge display — transparent status/navigation bars with correct dark/light icon handling via `enableEdgeToEdge()` SideEffect in `AutokolkTheme`, `systemBarsPadding()` on root content, removed legacy `windowOptOutEdgeToEdgeEnforcement` workaround (values-v35).

## [2.0.25] - 2026-04-14
- Added: Compose entry point — `ComposeMainActivity` (neaktivní, bez launcher filtru), stub `AutokolkTheme` a `AutokolkApp` composable. Připraveno pro budoucí Compose UI shell.

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
