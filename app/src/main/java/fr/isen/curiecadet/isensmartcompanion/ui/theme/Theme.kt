package fr.isen.curiecadet.isensmartcompanion.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Définition des couleurs pour le thème sombre et clair
private val Purple80 = Color(0xFF9B4D96)          // Couleur pour le thème sombre (80%)
private val PurpleGrey80 = Color(0xFF7C6A8E)      // Couleur secondaire pour le thème sombre
private val Pink80 = Color(0xFFD16D7B)            // Couleur tertiaire pour le thème sombre

private val Purple40 = Color(0xFF5E2A8C)          // Couleur pour le thème clair (40%)
private val PurpleGrey40 = Color(0xFF6D5D7E)      // Couleur secondaire pour le thème clair
private val Pink40 = Color(0xFFD24D6B)            // Couleur tertiaire pour le thème clair

// Définition des color schemes pour le thème sombre et clair
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    // Autres couleurs par défaut que tu peux ajouter si besoin
    // background = Color(0xFFFFFBFE),
    // surface = Color(0xFFFFFBFE),
    // onPrimary = Color.White,
    // onSecondary = Color.White,
    // onTertiary = Color.White,
    // onBackground = Color(0xFF1C1B1F),
    // onSurface = Color(0xFF1C1B1F),
)

@Composable
fun ISENSmartCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Couleurs dynamiques disponibles sur Android 12 (API 31) et versions ultérieures
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            // Si la version est compatible avec les couleurs dynamiques (Android 12+)
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Applique le thème personnalisé à l'interface
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
