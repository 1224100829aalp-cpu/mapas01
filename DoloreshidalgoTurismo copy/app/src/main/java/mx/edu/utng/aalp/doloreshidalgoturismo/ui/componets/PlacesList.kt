package mx.edu.utng.aalp.doloreshidalgoturismo.ui.componets
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mx.edu.utng.aalp.doloreshidalgoturismo.data.model.PlaceEntity
import mx.edu.utng.aalp.doloreshidalgoturismo.ui.components.ConfirmationDialog
import mx.edu.utng.aalp.doloreshidalgoturismo.utils.NavigationHelper

/**
 * Lista horizontal de tarjetas de lugares turísticos.
 * Se muestra en la parte inferior del mapa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesList(
    places: List<PlaceEntity>,
    onPlaceClick: (PlaceEntity) -> Unit,
    onEditClick: (PlaceEntity) -> Unit,
    onDeleteClick: (PlaceEntity) -> Unit,
    onFavoriteClick: (PlaceEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(places) { place ->
            PlaceCard(
                place = place,
                onClick = { onPlaceClick(place) },
                onEditClick = { onEditClick(place) },
                onDeleteClick = { onDeleteClick(place) },
                onFavoriteClick = { onFavoriteClick(place) }
            )
        }
    }
}

/**
 * Tarjeta individual de un lugar turístico.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceCard(
    place: PlaceEntity,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Estado para el diálogo de confirmación
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .width(280.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Encabezado: nombre y botón de favorito
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (place.isFavorite) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (place.isFavorite) Color.Red else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Categoría del lugar
            AssistChip(
                onClick = { },
                label = { Text(place.category) },
                leadingIcon = {
                    Icon(
                        imageVector = getCategoryIcon(place.category),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción del lugar
            Text(
                text = place.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botones de acción secundarios + editar/eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Botones de acción secundarios: Maps, Navegar, Compartir
                Row {
                    IconButton(onClick = { NavigationHelper.openGoogleMapsView(context, place) }) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Ver en Maps",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { NavigationHelper.openGoogleMapsNavigation(context, place) }) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Navegar",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    IconButton(onClick = { NavigationHelper.sharePlaceLocation(context, place) }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Compartir"
                        )
                    }
                }

                // Botones de editar y eliminar
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }

                    // Botón de eliminar muestra el diálogo de confirmación
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteConfirmation) {
        ConfirmationDialog (
            title = "Eliminar lugar",
            message = "¿Estás seguro de que deseas eliminar '${place.name}'? Esta acción no se puede deshacer.",
            confirmButtonText = "Eliminar",
            dismissButtonText = "Cancelar",
            onConfirm = {
                onDeleteClick()
                showDeleteConfirmation = false
            },
            onDismiss = {
                showDeleteConfirmation = false
            }
        )
    }
}

/**
 * Obtiene el icono correspondiente según la categoría del lugar.
 */
fun getCategoryIcon(category: String) = when (category.lowercase()) {
    "iglesia" -> Icons.Filled.Place
    "museo" -> Icons.Filled.Home
    "restaurante" -> Icons.Filled.ShoppingCart
    "plaza" -> Icons.Filled.Star
    else -> Icons.Filled.LocationOn
}