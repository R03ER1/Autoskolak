package cz.autokolk.ui.screens.home

/**
 * Název souboru ikony v `assets/images/lesson_icons/` — kopie logiky z [cz.autokolk.HomeActivity].
 */
fun mapSubcategoryToIconAsset(code: String): String {
    return when (code) {
        "c", "d", "t" -> "tractor-icon.png"
        "neh", "ost" -> "accident-icon.png"
        "pri" -> "arrows-up-icon.png"
        "zak" -> "ban-sign-icon.png"
        "mhd" -> "bus-symbol-icon.png"
        "pro", "neb" -> "car-back-collision-icon.png"
        "pru", "l" -> "car-document-icon.png"
        "spo" -> "car-door-icon.png"
        "vod", "sil" -> "car-driving-on-road-icon.png"
        "stk" -> "car-repair-service-icon.png"
        "mir" -> "car-report-icon.png"
        "aut" -> "car-top-view-icon.png"
        "inf" -> "construction-sign-icon.png"
        "slo" -> "construction-traffic-cone-icon.png"
        "sve" -> "engine-motor-icon.png"
        "vys" -> "exclamation-triangle-icon.png"
        "sta", "upr" -> "hand-line-icon.png"
        "uca" -> "hatchback-car-icon.png"
        "sou" -> "lubricant-oil-icon.png"
        "pol", "pok" -> "officer-icon.png"
        "pra", "vec" -> "road-route-icon.png"
        "nak" -> "roadside-car-assistance-icon.png"
        "sme" -> "route-arrows-up-icon.png"
        "rid", "cho" -> "seatbelt-icon.png"
        "riz" -> "steering-wheel-icon.png"
        "sem", "kri" -> "traffic-light-icon.png"
        "poj" -> "turn-right-arrow-icon.png"
        "pre" -> "zebra-crossing-sign-icon.png"
        "med" -> "first-aid-kit.png"
        else -> "traffic-light-icon.png"
    }
}
