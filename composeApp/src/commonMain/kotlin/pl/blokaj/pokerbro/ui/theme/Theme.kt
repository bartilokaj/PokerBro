package pl.blokaj.pokerbro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB23535),        // key interactive elements (buttons, highlights)
    onPrimary = Color(0xFFFFFFFF),      // text/icons on primary -> white for contrast
    secondary = Color(0xFFEEEADE),      // accents, highlights
    onSecondary = Color(0xFF000000),    // text/icons on secondary -> black
    background = Color(0xFF35654D),     // poker table green
    onBackground = Color(0xFFFFFFFF),   // text/icons on background
    surface = Color(0xFFFFFFF0),        // surfaces like cards, panels
    onSurface = Color(0xFF2B2A28),      // text/icons on surface
    surfaceVariant = Color(0xFF2E4D3B), // subtle variant for cards, raised surfaces
    onSurfaceVariant = Color(0xFFEEEEEE),
    error = Color(0xFFFFFFF0),
    onError = Color(0xFFB00020),          // standard Material error
    errorContainer = Color(0xFFFFFFFF),
    outline = Color(0xFFCCCCCC)         // borders, dividers
)

private val PhoneTypography = Typography()

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val typography = PhoneTypography

    MaterialTheme (
        colorScheme = colorScheme,
        content = content,
        typography = typography
    )
}