package mx.edu.utng.aalp.doloreshidalgoturismo.ui.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.maps.model.LatLng
import mx.edu.utng.aalp.doloreshidalgoturismo.data.model.PlaceEntity

/**
 * Diálogo para agregar o editar un lugar turístico
 * Es como un formulario de registro
 *
 * @param place: Si no es null, estamos editando; si es null, estamos agregando
 * @param onDismiss: Qué hacer cuando se cierra el diálogo
 * @param onSave: Qué hacer cuando se guardan los datos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDialog(
    place: PlaceEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, LatLng, String, String) -> Unit
) {
    // Estados para los campos del formulario
    var name by remember { mutableStateOf(place?.name ?: "") }
    var description by remember { mutableStateOf(place?.description ?: "") }
    var latitude by remember { mutableStateOf(place?.latitude?.toString() ?: "21.1560") }
    var longitude by remember { mutableStateOf(place?.longitude?.toString() ?: "-100.9318") }
    var selectedCategory by remember { mutableStateOf(place?.category ?: "Plaza") }
    var selectedColor by remember { mutableStateOf(place?.markerColor ?: "#FF6B35") }

    // Validación de errores
    var showError by remember { mutableStateOf(false) }

    // Categorías disponibles
    val categories = listOf("Iglesia", "Museo", "Restaurante", "Plaza", "Monumento", "Parque", "Tienda")

    // Colores disponibles para los marcadores
    val colors = listOf(
        "#FF6B35" to "Naranja",
        "#4ECDC4" to "Turquesa",
        "#95E1D3" to "Verde Menta",
        "#F38181" to "Rosa",
        "#AA96DA" to "Púrpura",
        "#FCBAD3" to "Rosa Claro",
        "#A8D8EA" to "Azul Cielo",
        "#FFD93D" to "Amarillo"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // Título del diálogo
                Text(
                    text = if (place == null) "Agregar Lugar Turístico" else "Editar Lugar",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo: Nombre del lugar
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del lugar") },
                    placeholder = { Text("Ej: Parroquia de Dolores") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && name.isBlank(),
                    supportingText = {
                        if (showError && name.isBlank()) {
                            Text("El nombre es obligatorio")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo: Descripción
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    placeholder = { Text("Describe el lugar turístico") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campos de coordenadas (en fila)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Latitud
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Latitud") },
                        placeholder = { Text("21.1560") },
                        modifier = Modifier.weight(1f),
                        isError = showError && latitude.toDoubleOrNull() == null
                    )

                    // Longitud
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Longitud") },
                        placeholder = { Text("-100.9318") },
                        modifier = Modifier.weight(1f),
                        isError = showError && longitude.toDoubleOrNull() == null
                    )
                }

                if (showError && (latitude.toDoubleOrNull() == null || longitude.toDoubleOrNull() == null)) {
                    Text(
                        text = "Coordenadas inválidas",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de categoría
                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de color del marcador
                Text(
                    text = "Color del marcador",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(colors) { (hex, name) ->
                        ColorOption(
                            color = Color(android.graphics.Color.parseColor(hex)),
                            isSelected = selectedColor == hex,
                            onClick = { selectedColor = hex },
                            colorName = name
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (name.isBlank() ||
                                latitude.toDoubleOrNull() == null ||
                                longitude.toDoubleOrNull() == null) {
                                showError = true
                                return@Button
                            }
                            val latLng = LatLng(
                                latitude.toDouble(),
                                longitude.toDouble()
                            )
                            onSave(name, description, latLng, selectedCategory, selectedColor)
                        }
                    ) {
                        Text(if (place == null) "Agregar" else "Actualizar")
                    }
                }
            }
        }
    }
}

/**
 * Componente para mostrar una opción de color
 * Es como un botón circular de color
 */
@Composable
fun ColorOption(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    colorName: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
        )

        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = colorName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
