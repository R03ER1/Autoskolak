# Changelog

All notable changes to this project will be documented in this file.

This file follows a simple format inspired by Keep a Changelog.

## [Unreleased]
-

## [2.2.13] - 2026-08-05
- Mystery box má teď truhlu místo dárečku — vypadá jako pořádná pirátská truhla, která se po otevření viditelně změní z zavřené na otevřenou se zlatou září uvnitř.
- Otevírání Mystery boxu je teď mnohem šťavnatější — truhla se před otevřením zatřese a po odhalení odměny "vyskočí" s pružným odskokem a krátkým zábleskem, plus vibrace přesně v okamžiku otevření.

## [2.2.12] - 2026-08-05
- Mystery box má nový vzhled — místo prázdného čtverce s otazníkem teď uvidíš hezkou ilustraci dárkové krabičky, a po otevření se navíc objeví malý odznak s fajfkou.
- Na hlavní obrazovce s lekcemi je nové tlačítko vlevo dole (nad kolem) s ikonou dárkové krabičky — ukazuje, kolik ti dnes zbývá otevření mystery boxu, a jedním klepnutím ho otevře.

## [2.2.11] - 2026-08-04
- Dialog bonusového kola je teď méně průhledný — obsah obrazovky pod ním už skrz něj neprosvítá a je lépe čitelný.
- Tlačítko "Zatočit!" teď při vyčerpání denních točení viditelně zešedne, takže je na první pohled jasné, že dnes už nejde použít.

## [2.2.10] - 2026-08-04
- Bonusové kolo má nový, hezčí vzhled — teď ladí s jemným "glass" stylem zbytku appky místo staré křiklavé barevné palety.
- Kolo teď nabízí víc druhů výher: kromě mincí i extra kola navíc, dočasné neomezené životy nebo bonusovou truhlu — a na každém dílku kola hned vidíš, co můžeš vyhrát, ještě než zatočíš.
- Na hlavní obrazovce s lekcemi je nové tlačítko vlevo dole s malým otáčejícím se kolem — ukazuje, kolik ti dnes zbývá točení, a jedním klepnutím otevře bonusové kolo.

## [2.2.9] - 2026-08-04
- Fix: po podržení tlačítka a "oživení" vyhladovělého lva se objevovala rozbitá prázdná obrazovka bez možnosti pokračovat. Teď se po úspěšném oživení zobrazí hláška "zachráněn!" a tlačítko "Pokračovat", které tě vrátí zpátky ke svému lvovi.

## [2.2.8] - 2026-08-04
- Fix: na načítací obrazovce se omylem zobrazoval generický "android robot" placeholder místo skutečného loga appky. Teď je tam napevno zabudované skutečné logo Autoškoláka (lev v autě), vždy dostupné hned od prvního spuštění appky.

## [2.2.7] - 2026-08-04
- Logo na načítací obrazovce je zase to původní (vždy okamžitě dostupné), aby se při prvním spuštění appky nikdy nezobrazil placeholder místo něj. Logo na úvodní onboarding stránce zůstává beze změny.

## [2.2.6] - 2026-07-25
- Na úvodní obrazovce onboardingu ("Vítej v Autoškoláku!") a na načítací obrazovce je teď místo dosavadní jednoduché ikony vidět skutečné logo Autoškoláka.

## [2.2.5] - 2026-07-25
- Doplněny chybějící obrázky na 3 místech: úvodní stránky onboardingu (přivítání, Alex, body, zkouška) teď mají viditelnou ikonu/obrázek místo prázdného místa.
- Na obrazovce zkoušky (a ve statistikách) je u prázdného grafu skóre teď hezká ikonka namísto velkého prázdného prostoru.
- Načítací obrazovka při startu appky má nově logo Autoškoláka.

## [2.2.4] - 2026-07-25
- Doplněn 5. nejvyšší stupeň nálady Alexe — při plné (81–100 %) sytosti se teď zobrazuje speciální nejveselejší obrázek.

## [2.2.3] - 2026-07-25
- Oprava obrázku Alexe — po redesignu se ukazovala jen jedna (neutrální) tvář. Alex se teď zase tváří vesele nebo smutně/hladově podle toho, jak je sytý.

## [2.2.2] - 2026-07-25
- Odstraněna přerušovaná spojka mezi lekcemi, která se při scrollování zamotávala.
- Odstraněn přetékající text s počtem otázek u lekcí — kolečka lekcí zůstávají na svém místě.

## [2.2.1] - 2026-07-25
- Nové minimalistické ikony lekcí na hlavní obrazovce — nahradily staré nekonzistentní obrázky za jednotný, čistý styl.

## [2.2.0] - 2026-07-25
- **Milník: dokončen redesign plán na 162/165 kroků (~98 %).** Fáze 1–13 kompletní kromě kroku 165 (finální manuální QA a release checklist na fyzickém zařízení — crash-free test na API 24–35, LeakCanary, plynulost animací naživo, back navigace, edge-to-edge vzhled). Verze zvednuta na 2.2.0 jako milník ukončení implementační části redesignu; drobné opravy a doladění mohou pokračovat i po tomto milníku.

## [2.1.6] - 2026-07-25
- Nová chytrá revize chybných otázek — appka si teď pamatuje, kdy jsi na otázku chybovala/chyboval, a nabízí ti ji znovu k opakování v narůstajících intervalech (nejdřív brzy, pak za pár dní, pak za týden), dokud si ji nezapamatuješ.
- Na hlavní obrazovce se objeví kartička "Čas na revizi!", pokud máš dnes nějaké otázky k opakování — jedním klikem se otevře revize.

## [2.1.5] - 2026-07-25
- Interní úklid — odstraněno posledních 6 starých obrazovek z doby před redesignem (výsledky, streak, procvičování, test). Appka vypadá a funguje stejně, jen bez starého kódu pod kapotou.

## [2.1.4] - 2026-07-25
- Vylepšený kontrast textu a ikon (žlutá/zlatá a modrotyrkysová barva) ve světlém režimu na několika obrazovkách — text je teď lépe čitelný na bílém pozadí.

## [2.1.3] - 2026-07-25
- Interní úklid — odstranění starých nepoužívaných stylů, barev a rozměrů z dob před redesignem. Bez viditelných změn v appce.

## [2.1.2] - 2026-07-25
- Plynulejší animace a rychlejší načítání obrázků — jemné "shimmer" efekty a obrázky lekcí se teď chytřeji přizpůsobují výkonu telefonu a šetřicímu režimu baterie.
- Lepší kontrast barev v některých tlačítkách a odznacích pro snazší čtení textu.
- Základní podpora pro tablety a otočení na šířku — cesta lekcí na Home je širší a otázky v kvízu s obrázkem/videem se na širší obrazovce zobrazí vedle sebe místo pod sebou.
- Drobné vylepšení dotykových ploch u menších tlačítek v sekci Procvičování.

## [2.1.1] - 2026-07-25
- Opravena vážná chyba, kvůli které appka po dokončení lekce spadla a nešla znovu spustit (týkalo se jen instalace z release verze, ne z Android Studia). Prosíme, kdo měl 2.1.0 a appka mu přestala jít spustit, ať si nainstaluje tuto verzi znovu.

## [2.1.0] - 2026-07-25
- Stabilní verze pro testování — shrnuje poslední dávku vylepšení: opravu světlého/tmavého režimu, odznaky za dokončené sekce, plynulý přechod z lekce do kvízu, sdílení výsledků jako obrázek, lepší podporu pro čtečky obrazovky a šetřicí režim baterie, menší velikost aplikace a opravu drobné chyby při přehrávání videí v kvízu.

## [2.0.71] - 2026-07-24
- Drobná technická oprava přehrávání videí v kvízu — odstraňuje neškodné, ale rušivé chybové hlášky v systémovém logu při přechodu mezi otázkami s videem.

## [2.0.70] - 2026-07-24
- Vylepšená přístupnost pro čtečky obrazovky (TalkBack) — spodní menu, horní lišta (séria, mince, životy), odpovědi v kvízu a kolečka lekcí na Home teď mají srozumitelnější popisky.
- Podpora systémového "Odstranit animace" a spořiče baterie — dekorativní animace (pulzující záře, pozadí, konfety) se v těchto režimech zjednoduší nebo vypnou, aplikace je tak plynulejší na starších/šetřených zařízeních.
- Menší optimalizace velikosti aplikace a interní údržba (ProGuard/R8 pravidla).

## [2.0.69] - 2026-07-23
- Oprava Světlého a Tmavého režimu: zmizel bílý "glow" kruh, který zůstával viditelný po přepnutí na Světlý režim.
- Opraveny desítky míst s nečitelným textem (černý na černém / bílý na bílém) na Home, Alexovi, v Testu, Procvičování, Nastavení, kvízu, achievementech, statistikách a dalších obrazovkách — text i karty se nyní vždy správně přebarví podle zvoleného režimu.

## [2.0.68] - 2026-07-23
- Plynulý přechod z cesty lekcí do kvízu (krok 41): kolečko lekce na Home obrazovce teď při kliknutí vizuálně "doputuje" do hlavičky Quiz obrazovky místo obyčejného přeřezu/slide přechodu (shared element transition).

## [2.0.67] - 2026-07-23
- Nové odznaky/milníky na cestě lekcí za dokončené sekce (trofej s malou animací při odemčení).
- Sdílení týdenního souhrnu je nyní jako vizuální obrázek (streak + statistiky), ne jen text.

## [2.0.66] - 2026-07-23
- Legacy cleanup: odstraněna nepoužívaná `LoadingActivity` (nahradila ji `ComposeMainActivity` jako launcher) a mrtvá view třída `CurvyPathView`.
- Smazáno 11 osiřelých XML layoutů z doby před přechodem na Compose (staré Alex fragmenty, achievementy, changelog atd.), na které už nic neodkazovalo.
- Náhodná událost (`EventOverlay`) v Compose teď používá stejnou konfeta komponentu jako ostatní obrazovky (level-up, výsledky) místo starého View widgetu.
- `ResultsActivity`, `StreakActivity`, `PracticeActivity`, `TestAttemptActivity` a související obrazovky zůstávají — pořád je používá `HomeActivity`/`MainActivity` jako fallback pro staré odkazy.

## [2.0.65] - 2026-07-23
- Zvýšen `targetSdk` na 36 (Android 16) — požadavek Google Play na cílení nejnovější API úrovně.
- Zkontrolován modul `:mediaassets` i verze build nástrojů (AGP, Gradle) — bez nutnosti dalších úprav.

## [2.0.64] - 2026-07-23
- Debug buildy nyní používají oficiální Google test AdMob ID (produkční ID zůstávají v release buildu).
- Sjednocení rewarded ad unit ID do jedné konstanty (`BuildConfig.ADMOB_REWARDED_ID`).
- Interstitial ID stejně tak čte z `BuildConfig.ADMOB_INTERSTITIAL_ID`.
- Po startu se v logcatu (tag `InterstitialAd`) objeví `[Ads] using TEST/PROD ad unit IDs`.

## [2.0.63] - 2026-07-23
- Interstitial reklamy integrovány do nového Compose flow (Results, po dokončení lekce).
- Reklama se zobrazuje po každé 3. dokončené lekci — prvních 3 lekce jsou bez reklam.
- Sjednoceno mezi novým Compose flow a legacy Activity vrstvou, aby pravidlo platilo všude.
- Když reklama není včas nachystaná, navigace není blokovaná — přeskočíme a nabijeme další.

## [2.0.62] - 2026-07-23
- Fáze 12: zvuky napříč aplikací (kvíz, spodní navigace, sheety, Alex, achievementy, level-up, kolo štěstí, mystery box, milníky streaku).
- Sjednocena haptická odezva přes centrální `HapticFeedback` — jednotné chování v UI i ViewModelech.
- Přepínače „Zvuky" a „Vibrace" v Nastavení se respektují všude.
- Opraven zavádějící podtitulek u „Revize chyb" — text už neslibuje spaced repetition.

## [2.0.61] - 2026-05-23
- Fáze 11 krok 140: týdenní souhrn — rozšířený `WeeklyXpScreen` o kartu „Souhrn týdne“ (lekce dokončeno, aktivních dní X/7, aktuální streak, osobní rekord) a tlačítko „Sdílet týdenní výsledek“ přes systémový share. Přidán periodický `WeeklySummaryWorker` (WorkManager, neděle 18:00, idempotentní `KEEP`), nový notifikační kanál „Týdenní souhrn“ a deep-link `OPEN_TAB_WEEKLY_XP` z notifikace přímo na obrazovku. `LessonProgress` nově sleduje počet dokončených lekcí za den (`lessons_per_day`, forward-only).

## [2.0.60] - 2026-04-16
- Fáze 11 krok 138: centrální obchod — tři motivy aplikace (Klasický zdarma, Neon mřížka, Západ slunce) s vlastními barvami Material 3, úpravou typografie a tvarů karet; nákup a přepnutí motivu za mince v obchodě. Alex: nové sloty čepice, šála a párty pozadí (emoji placeholdery, zapnutí/vypnutí po koupi), brýle přesunuty do stejného obchodu; list Alex „Obchod“ odkazuje na hlavní obchod. `PrimaryGradientButton` respektuje aktivní barevné schéma.

## [2.0.59] - 2026-04-16
- Oslavy milníků streaku: overlay po splnění milníku, po level-up na výsledku lekce, při znovuotevření app pokud pending zůstal. Bez kolize s kvízem a výsledkovou obrazovkou.

## [2.0.58] - 2026-04-15
- Nastavení (jen debug build): sekce „Debug — obchod“ — otevření bonusového kola a mystery boxu, skok do obchodu, reset denních limitů kola/boxu pro testování.

## [2.0.57] - 2026-04-15
- Obchod a bonusy: animované bonusové kolo a mystery box (výsledek až po animaci, zvuk a haptika), přehled zbývajících pokusů na obrazovce obchodu; `LessonProgress` rozšířen o počty zbývajících točení/otevření a jednotný bonus XP u krabičky.

## [2.0.56] - 2026-04-15
- XP, úrovně a odměna při postupu; denní výzvy; zmrzlý streak; streak milníky a koruna u Alexe; nápovědy v lekci za mince; týdenní XP; další úspěchy; 2× XP z reklamy; kolo a bedna s limity; přihlášení; sezónní hláška; revize chyb; obchod; sdílení; widget na ploše.

## [2.0.55] - 2026-04-15
- Fáze 10: Nastavení v Compose (profil, téma Systém/Světlý/Tmavý, zvuky, vibrace, biometrický zámek, denní cíl, úspěchy, changelog, smazání postupu), obrazovky Úspěchy a Historie změn; splash s Lottie a lepší stav stahování DFM; nový celoobrazovkový efekt při odemčení úspěchu (Lottie + konfety). Odstraněny legacy Settings/Achievements/Changelog Activity.

## [2.0.54] - 2026-04-15
- Jeden dynamický modul `mediaassets` místo `imageassets` a `videoassets1`–`5` (obrázky + videa; velikost pod limitem 200 MB na modul u Google Play).

## [2.0.53] - 2026-04-15
- Aktualizace závislostí: Kotlin 2.2.21, KSP 2.2.21-2.0.5, AndroidX (core-ktx 1.18, lifecycle 2.10, activity-compose 1.13, WorkManager 2.11.2, Room 2.8.4, testy junit/espresso), Material 1.13, Gson, Commons CSV, Play Services Ads a UMP přes `libs.versions.toml`; WorkManager sjednocen z katalogu místo natvrdo 2.8.1.

## [2.0.52] - 2026-04-15
- Nová záložka Procvičování v Compose: kategorie v mřížce, filtry, podkategorie, tvoje chyby, hledání otázek a list výsledků s detailem v listu (sheet) a tlačítkem Procvičit.
- Procvičovací kvíz bez životů, body za každých pět nově zobrazených otázek jako dřív; po dokončení přehled výsledků a návrat na Procvičování.

## [2.0.51] - 2026-04-15
- Zkouška má stejné rozložení otázek a bodů jako u skutečné zkoušky; nahoře pod ukazatelem postupu je napsaná aktuální skupina.
- V průběhu testu hned vidíš označenou odpověď; test můžeš kdykoli dokončit i bez vyplnění všeho.

## [2.0.50] - 2026-04-15
- Oprava sestavení aplikace (Room / novější kompilátor): databázové dotazy na pokusy zkoušky jsou blokující uvnitř IO vlákna, aby projekt zase šel přeložit.

## [2.0.49] - 2026-04-15
- Zkouška v Compose: stránka testu, statistiky, průběh zkoušky (25 otázek, 30 minut, odpočet před startem, posun mezi otázkami prstem), podrobné výsledky posledního pokusu.
- Uložení pokusů do databáze v telefonu; starší graf ze starého nastavení se při prvním spuštění přenesl.

## [2.0.48] - 2026-04-15
- Fáze 7 (Alex): celá stránka Alexe v Compose (krmení, obchod, přejmenování), `AlexDeathScreen`, odstranění starých Activity; odkaz z legacy menu na Compose s Alex záložkou.
- Notifikace hladu: úrovně 50 / 20 / 5 %, velký obrázek, tlačítko Nakrmit.
- Volitelné nové výrazy lva: `AlexSadC.png`, `AlexFamine.png` a CAlex varianty (aplikace má fallback).

## [2.0.47] - 2026-04-15
- Oprava: kvíz už nepadá na vibraci — v manifestu je `VIBRATE`, ViewModel před voláním kontroluje oprávnění.

## [2.0.46] - 2026-04-15
- Oprava: tlačítko odpovědi v kvízu — animace částic neblokuje klepnutí a neměla by shazovat aplikaci.

## [2.0.45] - 2026-04-15
- Fáze 6 (Quiz Experience): kompletnější Compose kvíz (top bar, média včetně videa, výsledkový pás, životy + reklama, odměny, zvuk odpočtu testu), výsledková obrazovka a fullscreen série po první lekci dne.
- Body za lekci stejně jako dříve v klasických výsledcích; nové assety: zajímavosti o provozu + Lottie.

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
- Added: Type-safe navigační routes — `Route` sealed class s 12 statickými a 4 parametrickými routami, argument konstanty a `mainTabs` helper (`ui/navigation/Routes.kt`).
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
- Novinka: Compose ColorScheme & MaterialTheme — `darkColorScheme()` a `lightColorScheme()` napojeny na `AutokolkTheme`, preference tmavého/světlého režimu uložená v SharedPreferences přes `ThemeMode` enum, `LocalIsDarkTheme` dostupný ve všech composables.

## [2.0.28] - 2026-04-14
- Novinka: Barevná paleta pro light mode (`Color.kt`) — světlá pozadí, glass-effect tokeny pro světlý režim, upravené akcentové barvy pro kontrast na světlém povrchu a tmavé textové varianty. Připraveno pro přepínání dark/light tématu.

## [2.0.27] - 2026-04-14
- Novinka: Barevná paleta pro dark mode (`Color.kt`) — pozadí, glass-effect tokeny, akcentové barvy (cyan/teal/blue gradient), sémantické barvy (success/error/warning/info) a textové varianty s průhledností pro glassmorphism design systém.

## [2.0.26] - 2026-04-14
- Novinka: Edge-to-edge zobrazení — průhledný status bar a navigation bar se správnými ikonami (tmavé/světlé), obsah správně odsazen pomocí `systemBarsPadding()`. Odstraněn dočasný workaround pro Android 15.

## [2.0.25] - 2026-04-14
- Novinka: Přidán vstupní bod pro nové Compose UI (`ComposeMainActivity`) — zatím neaktivní, připraven pro budoucí moderní rozhraní.

## [2.0.24] - 2026-04-14
- Novinka: Nová package struktura pro Compose UI (`ui/theme`, `ui/components/*`, `ui/screens/*`, `ui/navigation`, `data`, `util`) — připraveno pro migraci na Compose.

## [2.0.23] - 2026-04-14
- Novinka: Zvukový engine (`SoundManager`) s nízkolatenčním přehráváním krátkých efektů (správná/špatná odpověď, streak, mince, tap, level up, odpočet). Zvukové soubory budou doplněny později.

## [2.0.22] - 2026-04-14
- Novinka: Centralizovaný systém haptické odezvy (vibrace při tapnutí, správné/špatné odpovědi, streak aj.) — připraven pro nový Compose design.

## [2.0.21] - 2026-04-14
- Novinka: Přidána knihovna Haze 1.7.2 (glassmorphism blur efekty pro nový design).
- Změna: compileSdk zvýšen na 36 (vyžadováno novými knihovnami).

## [2.0.20] - 2026-04-14
- Novinka: Přidány knihovny Accompanist Permissions 0.37.3, Drawable Painter 0.37.3, Coil Compose 3.4.0, Compose Foundation a Animation (příprava pro Pager, async obrázky, animace).

## [2.0.19] - 2026-04-14
- Novinka: Přidán font Quicksand (5 řezů: Light, Regular, Medium, SemiBold, Bold) pro nový design aplikace.

## [2.0.18] - 2026-04-14
- Novinka: Přidána knihovna Lottie Compose 6.7.1 (příprava na animace v aplikaci — splash, odpovědi, streak, konfety aj.).

## [2.0.17] - 2026-04-14
- Novinka: Přidána knihovna Navigation Compose 2.9.7 (příprava na novou navigaci v aplikaci).

## [2.0.16] - 2026-04-14
- Novinka: Přidána podpora Jetpack Compose (BOM 2026.04.00, Material 3); příprava na moderní redesign UI.

## [2.0.15] - 2026-04-14
- Bezpečnost: vypnutý volný HTTP provoz; výjimky jen pro vývoj (localhost) v nastavení sítě.
- Soukromí: formulář souhlasu s reklamami (UMP) při startu; odkaz na zásady soukromí v Nastavení.
- Výkon: videa z balíčků se cachují na disk (méně opakovaného zápisu).
- UX: u lekce vidíš, když se stahuje video balíček; při málo místě na disku upozornění; načítání — nápověda k síti a k dialogu Play.
- Změna: obrázky z Play se stahují jen z načítací obrazovky (bez duplicity z aplikace).
- Změna: sekce pro vývojáře jen v ladící verzi; zmenšení release APK (R8); notifikace hladu a procvičování česky.
- Oprava: titulek stránky nastavení v layoutu (Lint).

## [2.0.14] - 2026-04-14
- Oprava: Načítací obrazovka — tlačítko Zpět je pořád zablokované, ale přes novější API (`OnBackPressedDispatcher`), aby prošel Lint (`MissingSuperCall`).

## [2.0.13] - 2026-04-14
- Oprava: Notifikace (životy plné / hlad Alexe) — na Androidu 13+ se před zobrazením ověří oprávnění k notifikacím; když ho uživatel nemá, neuloží se stav „už odesláno“, takže to zkusí znovu po povolení. Oprava Lint chyby a sestavení s kontrolou Lint.

## [2.0.12] - 2026-04-14
- Novinka: Revizní audit aplikace v souboru `docs/REVIDECNI_AUDIT.md` (moduly, úložiště, stabilita, architektura, UX, použitelnost, design, výkon, notifikace, compliance, testy, release).
- Změna: Úprava `.gitignore` — ignorovat složky `build` ve všech modulech (např. video balíčky), aby se do gitu nedostávaly meziprodukty Gradle.

## [2.0.11] - 2026-03-31
- Změna: Reklama po lekci se začne načítat už během čtecího úvodu a během kvízu; výsledek je často bez dlouhého čekání. Obrazovka „načítání“ má nový vzhled (karta + kolečko).
- Pozn.: Když lekci dokončíš velmi rychle, načítání se může stejně na chvíli ukázat.

## [2.0.10] - 2026-03-31
- Oprava: Úvod k tématu (čtecí lekce) — obrázky se znovu načítají ze složky `images/` v assets (stejně jako u otázek).

## [2.0.9] - 2026-03-30
- Změna: Vývojářské možnosti fungují i v ostré verzi aplikace; heslo je v kódu (`SettingsActivity`, konstanta `DEVELOPER_OPTIONS_PASSWORD`).

## [2.0.8] - 2026-03-30
- Oprava: Smrt Alexe — obrazovka už se neotevře dvakrát; po probuzení se načte hlavní obsah Alexovy stránky.
- Změna: Podržení otisku 3 s; mrtvý Alex z `images/alex/AlexDead.png` (modul imageassets).
- Novinka: Soubor `AlexDead.png` v imageassets.

## [2.0.7] - 2026-03-30
- Změna: Procvičování — nadpis „Tvoje chyby“ je červený pro lepší viditelnost.

## [2.0.6] - 2026-03-30
- Novinka: Procvičování — „Tvoje chyby“: ukládá špatné odpovědi z lekcí, kategorií, náhodného kvízu i testu; řazení podle po sobě jdoucích chyb; žetony ✅/❌ jako u kategorií; oprava tady platí, dokud znovu neuděláš chybu kdekoli v aplikaci.

## [2.0.5] - 2026-03-30
- Změna: Heslo k vývojářským možnostem je v `local.properties` (`developerOptionsPassword`), ne v kódu; v ostré verzi aplikace sekce Debugging není.

## [2.0.4] - 2026-03-26
- Změna: Obrazovka streaku po lekci — stejné tlačítko (teal gradient) a typografie jako na obrazovce výsledků; černé pozadí, velký plamen a číslo zůstávají.

## [2.0.3] - 2026-03-26
- Novinka: Po získání hvězdičky u úspěchu se zobrazí stejný overlay jako u náhodné události (Alex s brýlemi, konfety, animace, OK) s názvem úspěchu a počtem hvězdiček v dané kategorii.

## [2.0.2] - 2026-03-26
- Oprava: Náhodná událost znovu zobrazuje Alexe s brýlemi (`images/AlexCool.png`); dříve spadlo na ikonu tlapky kvůli špatné cestě k souboru.
- Změna: Overlay události — menší nadpis „Událost!“, větší popis, změna mincí/životů/hladu na samostatném řádku.

## [2.0.1] - 2026-03-26
- Oprava: Na Androidu 15+ už obsah nezasahuje pod systémové lišty (stavba a navigace); portrét zůstává vynucený.

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
