package cz.autokolk.reading

import cz.autokolk.ReadingLesson

object ReadingLessonsCatalog {
            fun readingLessonsForSubcategory(category: String): List<ReadingLesson> = when (category) {
            "pru" -> listOf(
                ReadingLesson(
                    category = "pru",
                    text = "Úřady\n\nVždy pamatujte, že za většinu věcí je zodpovědný Úřad obce s rozšířenou působností.\nAž na výjimky všechny změny jména nebo místa pobytu oznamujte právě tomuto úřadu.",
                    imagePath = "images/0907.png",
                    isLastSlide = true
                )
            )
            "neb" -> listOf(
                ReadingLesson(
                    category = "neb",
                    text = "Nebezpečí na vozovce\n\nVždy je důležité na silnici dbát opatrnosti, aby nikomu nehrozilo žádné nebezpečí.",
                    imagePath = "images/0984.png",
                    isLastSlide = true
                )
            )
            "kri" -> listOf(
                ReadingLesson(
                    category = "kri",
                    text = "Křižovatky\n\nU křižovatek je 5 důležitých pravidel\n\n1.pravidlo: řidič odbočující vlevo dává přednost všem protijedoucím i tramvajím v obou směrech\n2.pravidlo: odbočující tramvaj má přednost před vozidly, která jedou vedle ní, byť vozidlo pokračují rovně a tramvaj odbočuje",
                    imagePath = "images/0605.png"
                ),
                ReadingLesson(
                    category = "kri",
                    text = "Křižovatky\n\n3.pravidlo: řidič dává přednost chodcům na silnici, na kterou odbočuje, platí i bez přechodu\n4.pravidlo: bez značek přednosti nebo na silnicích stejného řádu platí přednost zprava\n5.pravidlo: policista>semafor>značky upravující přednost>přednost zprava",
                    imagePath = "images/0598.png",
                    isLastSlide = true
                )
            )
            "upr" -> listOf(
                ReadingLesson(
                    category = "upr",
                    text = "Značky upravující přednost\n\nZnačky různě upravující vaši přednost.\nVětšina má atypický tvar, jsou rozpoznatelné i zezadu, takže v případě nevšimnutí si značky ve svém směru se můžete podívat k ostatním směrům.\nTyto značky znamenají, že máte přednost v jízdě.\nJedna je v obci, druhá mimo obec.",
                    imagePath = "images/0960.png"
                ),
                ReadingLesson(
                    category = "upr",
                    text = "Značky \"Dej přednost\"\n\nTyto značky vám přikazují neomezit vozidla na hlavní komunikaci, dát jim přednost v jízdě.\nPři jedné stačí dát přednost, při druhé se musí před křižovatkou zastavit vozidlo v místě, odkud máte dostatečný rozhled do křižovatky.",
                    imagePath = "images/0938.png"
                ),
                ReadingLesson(
                    category = "upr",
                    text = "Značky v místech zúžení vozovky\n\nTyto značky platí v místech, kde je vozovky z jedné strany zúžena.\nPři první máte přednost před protijedoucími, druhá naopak značí, že přednost musíte dát vy.",
                    imagePath = "images/0988.png",
                    isLastSlide = true
                )
            )
            "inf" -> listOf(
                ReadingLesson(
                    category = "inf",
                    text = "Informativní dopravní značky\n\nVětšinou čtverec nebo obdélník informující o nějaké skutečnosti.\nPatří sem zónové značky, značky doporučené rychlosti nebo místa parkoviště.",
                    imagePath = "images/1129.png",
                    isLastSlide = true
                )
            )
            "pri" -> listOf(
                ReadingLesson(
                    category = "pri",
                    text = "Příkazové dopravní značky\n\nModré kolečko s určitým nařízením.\nZnamená \"Musíš...\" (odbočit, mít nasazené sněžné řetězy, jet nejméně určitou rychlostí)",
                    imagePath = "images/0929.png",
                    isLastSlide = true
                )
            )
            "zak" -> listOf(
                ReadingLesson(
                    category = "zak",
                    text = "Zákazové dopravní značky\n\nVětšinou červené kolečko se zákazem vevnitř.\nZnamená \"Nesmíš...\" (odbočit, jet rychleji, než je stanoveno, vjet)",
                    imagePath = "images/1084.png",
                    isLastSlide = true
                )
            )
            "vys" -> listOf(
                ReadingLesson(
                    category = "vys",
                    text = "Výstražné dopravní značky\n\nVětšinou červený trojúhelník upozorňující na nějaké nebezpečí.\nZnamenají \"Pozor na...\" (zatáčky, přechod, práce na silnici)",
                    imagePath = "images/1045.png",
                    isLastSlide = true
                )
            )
            "vod" -> listOf(
                ReadingLesson(
                    category = "vod",
                    text = "Vodorovné značky\n\nVodorovné dopravní značky jsou ty, které jsou nakresleny na zemi. Jedná se tedy o čáry mez pruhy nebo místa k stání",
                    imagePath = "images/0991.png",
                    isLastSlide = true
                )
            )
            "slo" -> listOf(
                ReadingLesson(
                    category = "slo",
                    text = "Sloupky\n\nBílý=okraj vozovky\nČervený=vyústění účelové komunikace na jinou komunikaci, ústí polňačky na normální silnici\nModrý=úsek s nebezpečím námrazy",
                    imagePath = "images/0955.png",
                    isLastSlide = true
                )
            )
            "pok" -> listOf(
                ReadingLesson(
                    category = "pok",
                    text = "Policisté na křižovatce\n\nPolicista čelem/zády=stůj\nPolicista bokem=jeď\nZvednutá ruka policisty=pozor, přijde změna\nPři odbočování vlevo se jezdí před policistou",
                    imagePath = "images/0919.png",
                    isLastSlide = true
                )
            )
            "cho" -> listOf(
                ReadingLesson(
                    category = "cho",
                    text = "Stání\n\nStát=\"uvést vozidlo do klidu na povolenou dobu\", česky \"Zamknout vozidlo u Kauflandu a odejít nakupovat\"",
                    imagePath = "images/0020.png"
                ),
                ReadingLesson(
                    category = "cho",
                    text = "Zastavení\n\nZastavit=\"uvést vozidlo do klidu na dobu nezbytně nutnou k nastoupení nebo vystoupení osob\", česky \"Vyložit babičku u Kauflandu nebo skládat cihly u domu\", od vozidla se nevzdálíte",
                    imagePath = "images/1114.png"
                ),
                ReadingLesson(
                    category = "cho",
                    text = "Zastavení vozidla\n\nZastavit vozidlo=\"přerušit jízdy z důvodu nezávislém na řidiči\", česky \"Puštění chodce na přechodu nebo zastavení na červenou\", z vozidla nevystoupíte a hned pokračujete v jízdě",
                    imagePath = "images/0026.png"
                ),
                ReadingLesson(
                    category = "cho",
                    text = "Přednost\n\nDát přednost=neomezit řidiče s předností",
                    imagePath = "images/0596.png",
                    isLastSlide = true
                )
            )
            "uca" -> listOf(
                ReadingLesson(
                    category = "uca",
                    text = "Účastníci provozu\n\nŘidič=osoba ovládající vozidlo\nChodec=osoba pohybující se pěšky, na skateboardu, kolečkových bruslích nebo invalidním vozíku\nŘidič nemotorového vozidla=osoba řídící kolo, ruční vozík, povoz\nOsoba na osobním přepravníku=uživatel malého přepravníku, např. Segway, Elektrokoloběžka, Jednokolka\nOsoba vedoucí zvíře=průvodce zvířat, např. krav, NEpatří sem pejskaři",
                    imagePath = "images/0254.png",
                    isLastSlide = true
                )
            )
            "aut" -> listOf(
                ReadingLesson(
                    category = "aut",
                    text = "Typy vozidel\n\nVozidlo=motorová i nemotorová vozidla, tramvaje\nMotorové vozidlo=nekolejové vozidlo s vlastním pohonem, trolejbus\nNemotorové vozidlo=přípojná vozidla, vozidla poháněná zvířecí nebo lidskou silou\nVozidla MHD=autobusy, trolejbusy a tramvaje\nVozidlo s právem přednosti k jízdě=modrá houkačka nahoře - hasiči, policie, sanitka",
                    imagePath = "images/0344.png",
                    isLastSlide = true
                )
            )
            "pra" -> listOf(
                ReadingLesson(
                    category = "pra",
                    text = "Pruhy\n\nPrůběžný=normální pruh, který jede rovně\nOdbočovací=pruh, který se postupně odpojuje\nPřipojovací=pruh, který se postupně připojuje\nOdbočovací nebo připojovací se musí vždy využít v plné délce.\nVyhrazený=pruh určený pouze pro určitá vozidla (např. vozidla MHD nebo cyklisté)",
                    imagePath = "images/0993.png"
                ),
                ReadingLesson(
                    category = "pra",
                    text = "Zóny\n\nObytná=většinou v obydlených oblastech před baráky, vjezd povolen, děti si mohou hrát na silnici a chodci mohou využívat vozovku, ale musí autům uhnout\nPěší=většinou v centrech měst s vysokou hustotou lidí, nelze vjet bez povolení, chodci mohou využívat vozovku, ale musí dát přednost jedoucím vozidlům",
                    imagePath = "images/0916.png"
                ),
                ReadingLesson(
                    category = "pra",
                    text = "Rychlost\n\nRychlost je důležitým faktorem bezpečnosti silničního provozu. Vždy je potřeba dodržovat stanovené rychlostní limity a přizpůsobit rychlost aktuálním podmínkám.",
                    imagePath = "images/0993.png",
                    isLastSlide = true
                )
            )
            "mhd" -> listOf(
                ReadingLesson(
                    category = "mhd",
                    text = "MHD\n\nZa vozidly MHD se v zastávce zastavovat nemusí, pokud nemají označení o přepravě dětí nebo pokud je na zastávce nástupní ostrůvek.\nVšem vozidlům MHD však musíte umožnit vyjetí ze zastávky, a to i snížením rychlosti nebo zastavením vozidla.\nPřes tramvajový se může přejíždět, jestli to dovolují pravidla a neomezíte v jízdě tramvaj.",
                    imagePath = "images/0654.png",
                    isLastSlide = true
                )
            )
            "sta" -> listOf(
                ReadingLesson(
                    category = "sta",
                    text = "Stání\n\nStát=\"uvést vozidlo do klidu na povolenou dobu\", česky \"Zamknout vozidlo u Kauflandu a odejít nakupovat\"",
                    imagePath = "images/0020.png"
                ),
                ReadingLesson(
                    category = "sta",
                    text = "Zastavení\n\nZastavit=\"uvést vozidlo do klidu na dobu nezbytně nutnou k nastoupení nebo vystoupení osob\", česky \"Vyložit babičku u Kauflandu nebo skládat cihly u domu\", od vozidla se nevzdálíte",
                    imagePath = "images/1114.png"
                ),
                ReadingLesson(
                    category = "sta",
                    text = "Zastavení vozidla\n\nZastavit vozidlo=\"přerušit jízdy z důvodu nezávislém na řidiči\", česky \"Puštění chodce na přechodu nebo zastavení na červenou\", z vozidla nevystoupíte a hned pokračujete v jízdě",
                    imagePath = "images/0026.png"
                ),
                ReadingLesson(
                    category = "sta",
                    text = "Parkování\n\nPokud se parkuje mimo místa vyznačená \"Parkoviště\", musí zůstat alespoň jeden průjezdný pruh.\nZároveň musíte použít blinkry směrem k okraji vozovky.\nNa dálnicích a silnicích pro motorová vozidla je pak parkování možné pouze na místech označených \"Parkoviště\".",
                    imagePath = "images/0886.png",
                    isLastSlide = true
                )
            )
            "sme" -> listOf(
                ReadingLesson(
                    category = "sme",
                    text = "Změny směru\n\nNeomezit=nepřekážet ostatním účastníkům provozu (někdo musí lehce brzdit, lehce pohnout volantem)\nNeohrozit=nezpůsobit nebezpečí ostatním, nesmíte ohrozit nikdy nikoho (nutnost dupnout na brzdu, prudké změny směru)\nPři vašich změnách směru nesmíte nikoho ohrozit, omezit můžete, pokud je to nutné.\nDále je při každé změně směru potřeba dávat blinkr, i v případě, kdy jedete po hlavní komunikaci, která mění směr.",
                    imagePath = "images/0982.png"
                ),
                ReadingLesson(
                    category = "sme",
                    text = "Předjíždění\n\nPři předjíždění nebo objíždění je vždy potřeba dbát zvýšené opatrnosti a použít znamení o změně směru jízdy.\nVždy se ujistěte, že máte dostatečnou rychlost a přehled o situaci.\nZároveň při těchto manévrech nesmíte nikoho ohrozit ani omezit.",
                    imagePath = "images/0501.png",
                    isLastSlide = true
                )
            )
            "pol" -> listOf(
                ReadingLesson(
                    category = "pol",
                    text = "Policie\n\nVždy je povinnost řídit se pokyny Policie!\nPolicie se musí vždy respektovat - v případech určených zákonem má možnost zastavovat vozidla nebo odebírat řidičáky.\nDo složek policie se počítá se i příslušník Celní správy ve stejnokroji, který může zastavovat vozidla, ale nemůže měřit rychlost.",
                    imagePath = "images/0920.png",
                    isLastSlide = true
                )
            )
            "neh" -> listOf(
                ReadingLesson(
                    category = "neh",
                    text = "Nehody\n\nU nehod jde vždy hlavně o bezpečnost - vždy používejte hlavně zdravý rozum.\nVždy je potřeba zastavit a zkontrolovat vozidlo, okolí i ostatní účastníky provozu, že jsou v pořádku.\nPovinnost oznámit nehodu je pouze, pokud způsobená škoda byla větší než 100 000 Kč.",
                    imagePath = "images/1065.png",
                    isLastSlide = true
                )
            )
            else -> emptyList()
        }
}
