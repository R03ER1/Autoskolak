package cz.autokolk.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CarCrash
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsBusFilled
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsCarFilled
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Fence
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.RoundaboutRight
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Signpost
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.ui.graphics.vector.ImageVector
import cz.autokolk.LessonProgress

/**
 * Minimalistická sada ikon lekcí (Material Icons) namísto starších bitmapových assetů
 * z `mediaassets` — konzistentní s ostatními kulatými odznaky v appce (např.
 * [cz.autokolk.ui.components.badges.SectionMilestoneBadge]), kde se ikona kreslí bílá
 * na barevném gradientovém kruhu.
 */
fun iconForSubcategory(code: String): ImageVector = when (code.lowercase()) {
    LessonProgress.CATEGORY_USER_MISTAKES.lowercase() -> Icons.Filled.History
    "c" -> Icons.Filled.LocalShipping
    "d" -> Icons.Filled.DirectionsBus
    "t" -> Icons.Filled.Agriculture
    "neh" -> Icons.Filled.CarCrash
    "pol" -> Icons.Filled.LocalPolice
    "sme" -> Icons.Filled.TurnRight
    "sta" -> Icons.Filled.LocalParking
    "mhd" -> Icons.Filled.DirectionsBusFilled
    "pra" -> Icons.Filled.Speed
    "riz" -> Icons.Filled.HealthAndSafety
    "ost" -> Icons.Filled.Groups
    "rid" -> Icons.Filled.DirectionsCar
    "pre" -> Icons.Filled.EmojiPeople
    "sil" -> Icons.Filled.Fence
    "uca" -> Icons.Filled.PeopleAlt
    "aut" -> Icons.Filled.DirectionsCarFilled
    "vec" -> Icons.Filled.Route
    "cho" -> Icons.Filled.CheckCircle
    "poj" -> Icons.AutoMirrored.Filled.MenuBook
    "sem" -> Icons.Filled.Traffic
    "pok" -> Icons.Filled.Gavel
    "slo" -> Icons.Filled.Construction
    "vod" -> Icons.Filled.EditRoad
    "vys" -> Icons.Filled.WarningAmber
    "zak" -> Icons.Filled.Block
    "pri" -> Icons.Filled.ArrowCircleUp
    "inf" -> Icons.Filled.Signpost
    "upr" -> Icons.AutoMirrored.Filled.AltRoute
    "kri" -> Icons.Filled.RoundaboutRight
    "neb" -> Icons.Filled.ReportProblem
    "sou" -> Icons.Filled.Engineering
    "sve" -> Icons.Filled.CarRepair
    "nak" -> Icons.Filled.Inventory2
    "spo" -> Icons.Filled.AirlineSeatReclineNormal
    "stk" -> Icons.AutoMirrored.Filled.FactCheck
    "pru" -> Icons.Filled.AssignmentInd
    "l" -> Icons.Filled.School
    "mir" -> Icons.Filled.Scale
    "pro" -> Icons.Filled.Policy
    "med" -> Icons.Filled.MedicalServices
    else -> Icons.Filled.Traffic
}
