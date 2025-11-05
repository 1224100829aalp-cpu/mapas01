package mx.edu.utng.aalp.doloreshidalgoturismo.ui.componets

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Animación de pulso para marcadores en el mapa.
 * Crea un efecto visual llamativo cuando se agrega un nuevo lugar.
 */
@Composable
fun PulsingMarkerIndicator(
    color: Color,
    modifier: Modifier = Modifier
) {
    // Transición infinita para animar escala y transparencia
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier.size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        // Círculo exterior pulsante
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .background(
                    color = color.copy(alpha = alpha),
                    shape = CircleShape
                )
        )

        // Círculo interior fijo
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )
    }
}
