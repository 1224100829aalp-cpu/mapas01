package mx.edu.utng.aalp.doloreshidalgoturismo.ui.componets

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mx.edu.utng.aalp.doloreshidalgoturismo.ui.viewmodel.PlaceStatistics


/**
 * Panel que muestra estadísticas de los lugares guardados
 * Es como un resumen ejecutivo de tu colección
 */
@Composable
fun StatisticsPanel(
    statistics: PlaceStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Estadísticas de Lugares",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Total de lugares
            StatisticRow(
                icon = Icons.Default.Place,
                label = "Total de lugares",
                value = statistics.totalPlaces.toString()
            )

            // Lugares favoritos
            StatisticRow(
                icon = Icons.Default.Favorite,
                label = "Favoritos",
                value = statistics.favoriteCount.toString(),
                iconTint = Color.Red
            )

            // Categoría más popular
            if (statistics.categoryCounts.isNotEmpty()) {
                val mostPopular = statistics.categoryCounts.maxByOrNull { it.value }
                mostPopular?.let { (category, count) ->
                    StatisticRow(
                        icon = Icons.Default.Star,
                        label = "Categoría popular",
                        value = "$category ($count)",
                        iconTint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            // Último lugar agregado
            statistics.mostRecentPlace?.let { place ->
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Último agregado:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun StatisticRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}