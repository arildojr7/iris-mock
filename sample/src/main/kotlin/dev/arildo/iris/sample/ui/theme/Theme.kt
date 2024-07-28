package dev.arildo.iris.sample.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val primary = Color(0xFFFFC66D)
val secondary = Color(0xFF9E79B1)
val tertiary = Color(0xFF7F7F7F)
val background = Color(0xFF1E2022)
val onBackground = Color(0xFF2B2D30)
val stringTextColor = Color(0xFF6A8759)

private val darkScheme = darkColorScheme(
    primary = primary,
    secondary = secondary,
    tertiary = tertiary,
    background = background,
    onBackground = onBackground,
)

val AppTypography = Typography(
    labelMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.Monospace,
        fontSize = 16.sp,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.Monospace,
        fontSize = 16.sp,
    )
)


@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkScheme,
        typography = AppTypography,
        content = content
    )
}
