package cz.autokolk.ui.screens.home

fun capitalizeFirst(text: String): String {
    if (text.isEmpty()) return text
    return text.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

fun mapCategoryDisplayName(code: String): String {
    val name = when (code.lowercase()) {
        "prav" -> "pravidla provozu a dopravní předpisy"
        "bez" -> "bezpečnost jízdy"
        "def" -> "základní definice"
        "znak" -> "značky"
        "res" -> "řešení dopravních situací"
        "voz" -> "podmínky provozu vozidla"
        "souv" -> "související předpisy"
        "med" -> "zdravotnická příprava"
        "cdt" -> "Předpisy pro jiné skupiny"
        else -> code
    }
    return capitalizeFirst(name)
}

fun mapSubcategoryDisplayName(code: String): String {
    val name = when (code.lowercase()) {
        "neh" -> "nehody"
        "pol" -> "policie"
        "sme" -> "změny směru a přednosti"
        "sta" -> "stání a zastavení"
        "mhd" -> "kontakt s mhd"
        "pra" -> "pruhy, zóny a rychlosti"
        "riz" -> "řízení a bezpečnosti vozidla"
        "ost" -> "vztah k ostatním účastníkům a vozidlům"
        "rid" -> "chování při řízení"
        "pre" -> "přechody a chodci"
        "sil" -> "vztah k veškerému okolnímu vybavení"
        "uca" -> "účastníci"
        "aut" -> "vozidla"
        "vec" -> "místa"
        "cho" -> "chování"
        "poj" -> "obecné pojmy"
        "sem" -> "semafory"
        "pok" -> "pokyny policie"
        "slo" -> "dopravní sloupky"
        "vod" -> "vodorovné značky"
        "vys" -> "výstražné značky"
        "zak" -> "zákazové"
        "pri" -> "příkazové značky"
        "inf" -> "informativní značky"
        "upr" -> "značky upravující přednost"
        "kri" -> "křižovatky"
        "neb" -> "nebezpečí na silnici"
        "sou" -> "systémy a bezpečnost"
        "sve" -> "součásti auta"
        "nak" -> "náklady auta"
        "spo" -> "cestující"
        "stk" -> "kontroly"
        "pru" -> "řidičáky, ohlašování změn, úřady"
        "l" -> "režim l17"
        "mir" -> "míry vozidla, hmotnosti a kontroly"
        "pro" -> "provozovatel a pojištění"
        else -> code
    }
    return capitalizeFirst(name)
}
