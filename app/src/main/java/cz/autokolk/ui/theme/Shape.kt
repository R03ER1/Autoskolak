package cz.autokolk.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AutokolkShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

/** Plně zaoblené pilulky (velký poloměr rohů). */
val PillShape = RoundedCornerShape(50.dp)

val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

val LessonNodeShape = CircleShape
