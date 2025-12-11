package cz.autokolk

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class ReadingLessonActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_LESSON_NUMBER = "extra_lesson_number"
        const val EXTRA_IS_REVIEW = "extra_is_review"
        const val EXTRA_DISPLAY_LESSON_NUMBER = "extra_display_lesson_number"
        const val EXTRA_RETURN_TO_CALLER = "extra_return_to_caller"
    }

    private lateinit var lessonText: TextView
    private lateinit var lessonImage: ImageView
    private lateinit var nextButton: MaterialButton
    private lateinit var okButton: MaterialButton
    private lateinit var closeButton: ImageButton
    private lateinit var lessonProgressBar: ProgressBar

    private var currentSlide = 0
    private var category = ""
    private var lessonNumber = 1
    private var isReviewMode = false
    private var returnToCaller = false
    private lateinit var readingLessons: List<ReadingLesson>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_lesson)

        // Set the status bar color to black
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        category = intent.getStringExtra(EXTRA_CATEGORY) ?: ""
        lessonNumber = intent.getIntExtra(EXTRA_LESSON_NUMBER, 1)
        isReviewMode = intent.getBooleanExtra(EXTRA_IS_REVIEW, false)
        returnToCaller = intent.getBooleanExtra(EXTRA_RETURN_TO_CALLER, false)

        initializeViews()
        loadReadingLessons()
        showCurrentSlide()
    }

    private fun initializeViews() {
        lessonText = findViewById(R.id.lessonText)
        lessonImage = findViewById(R.id.lessonImage)
        nextButton = findViewById(R.id.nextButton)
        okButton = findViewById(R.id.okButton)
        closeButton = findViewById(R.id.closeButton)
        lessonProgressBar = findViewById(R.id.lessonProgressBar)

        nextButton.setOnClickListener {
            currentSlide++
            showCurrentSlide()
        }

        okButton.setOnClickListener {
            finishReadingLesson()
        }

        closeButton.setOnClickListener {
            finishReadingLesson()
        }
    }

    private fun loadReadingLessons() {
        readingLessons = when (category) {
            "pru" -> listOf(
                ReadingLesson(
                    category = "pru",
                    text = "Úřady\n\nVždy pamatujte, že za většinu věcí je zodpovědný Úřad obce s rozšířenou působností.\nAž na výjimky všechny změny jména nebo místa pobytu oznamujte právě tomuto úřadu.",
                    imagePath = "0907.png",
                    isLastSlide = true
                )
            )
            "neb" -> listOf(
                ReadingLesson(
                    category = "neb",
                    text = "Nebezpečí na vozovce\n\nVždy je důležité na silnici dbát opatrnosti, aby nikomu nehrozilo žádné nebezpečí.",
                    imagePath = "0984.png",
                    isLastSlide = true
                )
            )
            "kri" -> listOf(
                ReadingLesson(
                    category = "kri",
                    text = "Křižovatky\n\nU křižovatek je 5 důležitých pravidel\n\n1.pravidlo: řidič odbočující vlevo dává přednost všem protijedoucím i tramvajím v obou směrech\n2.pravidlo: odbočující tramvaj má přednost před vozidly, která jedou vedle ní, byť vozidlo pokračují rovně a tramvaj odbočuje",
                    imagePath = "0605.png"
                ),
                ReadingLesson(
                    category = "kri",
                    text = "Křižovatky\n\n3.pravidlo: řidič dává přednost chodcům na silnici, na kterou odbočuje, platí i bez přechodu\n4.pravidlo: bez značek přednosti nebo na silnicích stejného řádu platí přednost zprava\n5.pravidlo: policista>semafor>značky upravující přednost>přednost zprava",
                    imagePath = "0598.png",
                    isLastSlide = true
                )
            )
            "upr" -> listOf(
                ReadingLesson(
                    category = "upr",
                    text = "Značky upravující přednost\n\nZnačky různě upravující vaši přednost.\nVětšina má atypický tvar, jsou rozpoznatelné i zezadu, takže v případě nevšimnutí si značky ve svém směru se můžete podívat k ostatním směrům.\nTyto značky znamenají, že máte přednost v jízdě.\nJedna je v obci, druhá mimo obec.",
                    imagePath = "0960.png"
                ),
                ReadingLesson(
                    category = "upr",
                    text = "Značky \"Dej přednost\"\n\nTyto značky vám přikazují neomezit vozidla na hlavní komunikaci, dát jim přednost v jízdě.\nPři jedné stačí dát přednost, při druhé se musí před křižovatkou zastavit vozidlo v místě, odkud máte dostatečný rozhled do křižovatky.",
                    imagePath = "0938.png"
                ),
                ReadingLesson(
                    category = "upr",
                    text = "Značky v místech zúžení vozovky\n\nTyto značky platí v místech, kde je vozovky z jedné strany zúžena.\nPři první máte přednost před protijedoucími, druhá naopak značí, že přednost musíte dát vy.",
                    imagePath = "0988.png",
                    isLastSlide = true
                )
            )
            "inf" -> listOf(
                ReadingLesson(
                    category = "inf",
                    text = "Informativní dopravní značky\n\nVětšinou čtverec nebo obdélník informující o nějaké skutečnosti.\nPatří sem zónové značky, značky doporučené rychlosti nebo místa parkoviště.",
                    imagePath = "1129.png",
                    isLastSlide = true
                )
            )
            "pri" -> listOf(
                ReadingLesson(
                    category = "pri",
                    text = "Příkazové dopravní značky\n\nModré kolečko s určitým nařízením.\nZnamená \"Musíš...\" (odbočit, mít nasazené sněžné řetězy, jet nejméně určitou rychlostí)",
                    imagePath = "0929.png",
                    isLastSlide = true
                )
            )
            "zak" -> listOf(
                ReadingLesson(
                    category = "zak",
                    text = "Zákazové dopravní značky\n\nVětšinou červené kolečko se zákazem vevnitř.\nZnamená \"Nesmíš...\" (odbočit, jet rychleji, než je stanoveno, vjet)",
                    imagePath = "1084.png",
                    isLastSlide = true
                )
            )
            "vys" -> listOf(
                ReadingLesson(
                    category = "vys",
                    text = "Výstražné dopravní značky\n\nVětšinou červený trojúhelník upozorňující na nějaké nebezpečí.\nZnamenají \"Pozor na...\" (zatáčky, přechod, práce na silnici)",
                    imagePath = "1045.png",
                    isLastSlide = true
                )
            )
            "vod" -> listOf(
                ReadingLesson(
                    category = "vod",
                    text = "Vodorovné značky\n\nVodorovné dopravní značky jsou ty, které jsou nakresleny na zemi. Jedná se tedy o čáry mez pruhy nebo místa k stání",
                    imagePath = "0991.png",
                    isLastSlide = true
                )
            )
            "slo" -> listOf(
                ReadingLesson(
                    category = "slo",
                    text = "Sloupky\n\nBílý=okraj vozovky\nČervený=vyústění účelové komunikace na jinou komunikaci, ústí polňačky na normální silnici\nModrý=úsek s nebezpečím námrazy",
                    imagePath = "0955.png",
                    isLastSlide = true
                )
            )
            "pok" -> listOf(
                ReadingLesson(
                    category = "pok",
                    text = "Policisté na křižovatce\n\nPolicista čelem/zády=stůj\nPolicista bokem=jeď\nZvednutá ruka policisty=pozor, přijde změna\nPři odbočování vlevo se jezdí před policistou",
                    imagePath = "0919.png",
                    isLastSlide = true
                )
            )
            "cho" -> listOf(
                ReadingLesson(
                    category = "cho",
                    text = "Stání\n\nStát=\"uvést vozidlo do klidu na povolenou dobu\", česky \"Zamknout vozidlo u Kauflandu a odejít nakupovat\"",
                    imagePath = "0020.png"
                ),
                ReadingLesson(
                    category = "cho",
                    text = "Zastavení\n\nZastavit=\"uvést vozidlo do klidu na dobu nezbytně nutnou k nastoupení nebo vystoupení osob\", česky \"Vyložit babičku u Kauflandu nebo skládat cihly u domu\", od vozidla se nevzdálíte",
                    imagePath = "1114.png"
                ),
                ReadingLesson(
                    category = "cho",
                    text = "Zastavení vozidla\n\nZastavit vozidlo=\"přerušit jízdy z důvodu nezávislém na řidiči\", česky \"Puštění chodce na přechodu nebo zastavení na červenou\", z vozidla nevystoupíte a hned pokračujete v jízdě",
                    imagePath = "0026.png"
                ),
                ReadingLesson(
                    category = "cho",
                    text = "Přednost\n\nDát přednost=neomezit řidiče s předností",
                    imagePath = "0596.png",
                    isLastSlide = true
                )
            )
            "uca" -> listOf(
                ReadingLesson(
                    category = "uca",
                    text = "Účastníci provozu\n\nŘidič=osoba ovládající vozidlo\nChodec=osoba pohybující se pěšky, na skateboardu, kolečkových bruslích nebo invalidním vozíku\nŘidič nemotorového vozidla=osoba řídící kolo, ruční vozík, povoz\nOsoba na osobním přepravníku=uživatel malého přepravníku, např. Segway, Elektrokoloběžka, Jednokolka\nOsoba vedoucí zvíře=průvodce zvířat, např. krav, NEpatří sem pejskaři",
                    imagePath = "0254.png",
                    isLastSlide = true
                )
            )
            "aut" -> listOf(
                ReadingLesson(
                    category = "aut",
                    text = "Typy vozidel\n\nVozidlo=motorová i nemotorová vozidla, tramvaje\nMotorové vozidlo=nekolejové vozidlo s vlastním pohonem, trolejbus\nNemotorové vozidlo=přípojná vozidla, vozidla poháněná zvířecí nebo lidskou silou\nVozidla MHD=autobusy, trolejbusy a tramvaje\nVozidlo s právem přednosti k jízdě=modrá houkačka nahoře - hasiči, policie, sanitka",
                    imagePath = "0344.png",
                    isLastSlide = true
                )
            )
            "pra" -> listOf(
                ReadingLesson(
                    category = "pra",
                    text = "Pruhy\n\nPrůběžný=normální pruh, který jede rovně\nOdbočovací=pruh, který se postupně odpojuje\nPřipojovací=pruh, který se postupně připojuje\nOdbočovací nebo připojovací se musí vždy využít v plné délce.\nVyhrazený=pruh určený pouze pro určitá vozidla (např. vozidla MHD nebo cyklisté)",
                    imagePath = "0993.png"
                ),
                ReadingLesson(
                    category = "pra",
                    text = "Zóny\n\nObytná=většinou v obydlených oblastech před baráky, vjezd povolen, děti si mohou hrát na silnici a chodci mohou využívat vozovku, ale musí autům uhnout\nPěší=většinou v centrech měst s vysokou hustotou lidí, nelze vjet bez povolení, chodci mohou využívat vozovku, ale musí dát přednost jedoucím vozidlům",
                    imagePath = "0916.png"
                ),
                ReadingLesson(
                    category = "pra",
                    text = "Rychlost\n\nRychlost je důležitým faktorem bezpečnosti silničního provozu. Vždy je potřeba dodržovat stanovené rychlostní limity a přizpůsobit rychlost aktuálním podmínkám.",
                    imagePath = "0993.png",
                    isLastSlide = true
                )
            )
            "mhd" -> listOf(
                ReadingLesson(
                    category = "mhd",
                    text = "MHD\n\nZa vozidly MHD se v zastávce zastavovat nemusí, pokud nemají označení o přepravě dětí nebo pokud je na zastávce nástupní ostrůvek.\nVšem vozidlům MHD však musíte umožnit vyjetí ze zastávky, a to i snížením rychlosti nebo zastavením vozidla.\nPřes tramvajový se může přejíždět, jestli to dovolují pravidla a neomezíte v jízdě tramvaj.",
                    imagePath = "0654.png",
                    isLastSlide = true
                )
            )
            "sta" -> listOf(
                ReadingLesson(
                    category = "sta",
                    text = "Stání\n\nStát=\"uvést vozidlo do klidu na povolenou dobu\", česky \"Zamknout vozidlo u Kauflandu a odejít nakupovat\"",
                    imagePath = "0020.png"
                ),
                ReadingLesson(
                    category = "sta",
                    text = "Zastavení\n\nZastavit=\"uvést vozidlo do klidu na dobu nezbytně nutnou k nastoupení nebo vystoupení osob\", česky \"Vyložit babičku u Kauflandu nebo skládat cihly u domu\", od vozidla se nevzdálíte",
                    imagePath = "1114.png"
                ),
                ReadingLesson(
                    category = "sta",
                    text = "Zastavení vozidla\n\nZastavit vozidlo=\"přerušit jízdy z důvodu nezávislém na řidiči\", česky \"Puštění chodce na přechodu nebo zastavení na červenou\", z vozidla nevystoupíte a hned pokračujete v jízdě",
                    imagePath = "0026.png"
                ),
                ReadingLesson(
                    category = "sta",
                    text = "Parkování\n\nPokud se parkuje mimo místa vyznačená \"Parkoviště\", musí zůstat alespoň jeden průjezdný pruh.\nZároveň musíte použít blinkry směrem k okraji vozovky.\nNa dálnicích a silnicích pro motorová vozidla je pak parkování možné pouze na místech označených \"Parkoviště\".",
                    imagePath = "0886.png",
                    isLastSlide = true
                )
            )
            "sme" -> listOf(
                ReadingLesson(
                    category = "sme",
                    text = "Změny směru\n\nNeomezit=nepřekážet ostatním účastníkům provozu (někdo musí lehce brzdit, lehce pohnout volantem)\nNeohrozit=nezpůsobit nebezpečí ostatním, nesmíte ohrozit nikdy nikoho (nutnost dupnout na brzdu, prudké změny směru)\nPři vašich změnách směru nesmíte nikoho ohrozit, omezit můžete, pokud je to nutné.\nDále je při každé změně směru potřeba dávat blinkr, i v případě, kdy jedete po hlavní komunikaci, která mění směr.",
                    imagePath = "0982.png"
                ),
                ReadingLesson(
                    category = "sme",
                    text = "Předjíždění\n\nPři předjíždění nebo objíždění je vždy potřeba dbát zvýšené opatrnosti a použít znamení o změně směru jízdy.\nVždy se ujistěte, že máte dostatečnou rychlost a přehled o situaci.\nZároveň při těchto manévrech nesmíte nikoho ohrozit ani omezit.",
                    imagePath = "0501.png",
                    isLastSlide = true
                )
            )
            "pol" -> listOf(
                ReadingLesson(
                    category = "pol",
                    text = "Policie\n\nVždy je povinnost řídit se pokyny Policie!\nPolicie se musí vždy respektovat - v případech určených zákonem má možnost zastavovat vozidla nebo odebírat řidičáky.\nDo složek policie se počítá se i příslušník Celní správy ve stejnokroji, který může zastavovat vozidla, ale nemůže měřit rychlost.",
                    imagePath = "0920.png",
                    isLastSlide = true
                )
            )
            "neh" -> listOf(
                ReadingLesson(
                    category = "neh",
                    text = "Nehody\n\nU nehod jde vždy hlavně o bezpečnost - vždy používejte hlavně zdravý rozum.\nVždy je potřeba zastavit a zkontrolovat vozidlo, okolí i ostatní účastníky provozu, že jsou v pořádku.\nPovinnost oznámit nehodu je pouze, pokud způsobená škoda byla větší než 100 000 Kč.",
                    imagePath = "1065.png",
                    isLastSlide = true
                )
            )
            else -> emptyList()
        }
    }

    private fun showCurrentSlide() {
        if (currentSlide >= readingLessons.size) {
            finishReadingLesson()
            return
        }

        val lesson = readingLessons[currentSlide]
        
        // Animate text and image
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        lessonText.startAnimation(fadeIn)
        lessonImage.startAnimation(fadeIn)

        lessonText.text = lesson.text

        // Handle image display
        lesson.imagePath?.let { imagePath ->
            try {
                val inputStream = assets.open(imagePath)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                lessonImage.setImageBitmap(bitmap)
                // Limit max height to ~40% of screen to avoid overly tall images
                val displayMetrics = resources.displayMetrics
                val maxHeightPx = (displayMetrics.heightPixels * 0.4f).toInt()
                if (lessonImage.maxHeight != maxHeightPx) {
                    lessonImage.maxHeight = maxHeightPx
                }
                lessonImage.visibility = View.VISIBLE
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
                lessonImage.visibility = View.GONE
            }
        } ?: run {
            lessonImage.visibility = View.GONE
        }

        // Update navigation buttons
        nextButton.visibility = if (lesson.isLastSlide) View.GONE else View.VISIBLE
        okButton.visibility = if (lesson.isLastSlide) View.VISIBLE else View.GONE

        // Update progress bar; progress is percentage of slides completed
        val totalSlides = if (readingLessons.isNotEmpty()) readingLessons.size else 1
        val progressPercent = ((currentSlide + 1) * 100) / totalSlides
        lessonProgressBar.progress = progressPercent
    }

    private fun finishReadingLesson() {
        // If opened as inline info from an ongoing lesson, just return to caller
        if (returnToCaller) {
            finish()
            return
        }
        // Otherwise transition to MainActivity with the lesson number
        val back = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_LESSON_NUMBER, lessonNumber)
            putExtra(MainActivity.EXTRA_IS_REVIEW, isReviewMode)
            // Do NOT pass category for normal lessons; this would incorrectly trigger practice mode
            putExtra(MainActivity.EXTRA_CATEGORY, "")
            // Forward display lesson number if provided so numbering stays consistent after intro
            val displayNum = intent?.getIntExtra(EXTRA_DISPLAY_LESSON_NUMBER, -1) ?: -1
            if (displayNum > 0) {
                putExtra(MainActivity.EXTRA_DISPLAY_LESSON_NUMBER, displayNum)
            }
        }
        startActivity(back)
        finish()
    }
} 