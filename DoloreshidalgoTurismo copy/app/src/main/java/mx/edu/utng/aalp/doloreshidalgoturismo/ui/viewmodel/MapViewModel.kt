package mx.edu.utng.aalp.doloreshidalgoturismo.ui.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mx.edu.utng.aalp.doloreshidalgoturismo.data.model.PlaceEntity
import mx.edu.utng.aalp.doloreshidalgoturismo.data.repository.PlaceRepository
import android.util.Log

class MapViewModel(
    private val repository: PlaceRepository
) : ViewModel() {

    // Estado de los lugares (observado por la UI)
    val places: StateFlow<List<PlaceEntity>> = repository.allPlaces
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Lugar seleccionado para editar
    private val _selectedPlace = MutableStateFlow<PlaceEntity?>(null)
    val selectedPlace: StateFlow<PlaceEntity?> = _selectedPlace.asStateFlow()

    // Categoría filtrada actualmente
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Estado del diálogo de agregar/editar
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    // Coordenadas del centro del mapa
    private val _mapCenter = MutableStateFlow(LatLng(21.1560, -100.9318))
    val mapCenter: StateFlow<LatLng> = _mapCenter.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Cargar lugares predeterminados si la base de datos está vacía
        viewModelScope.launch {
            places.first().let { placesList ->
                if (placesList.isEmpty()) {
                    repository.insertDefaultPlaces()
                }
            }
        }
    }

    // Limpiar error
    fun clearError() {
        _errorMessage.value = null
    }

    fun addPlace(
        name: String,
        description: String,
        latLng: LatLng,
        category: String,
        markerColor: String
    ) {
        viewModelScope.launch {
            try {
                Log.d("MapViewModel", "Agregando nuevo lugar: $name")
                val newPlace = PlaceEntity(
                    name = name,
                    description = description,
                    latitude = latLng.latitude,
                    longitude = latLng.longitude,
                    category = category,
                    markerColor = markerColor
                )
                repository.insertPlace(newPlace)
                Log.i("MapViewModel", "Lugar agregado exitosamente con ID: ${newPlace.id}")
                _showDialog.value = false
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error al agregar lugar", e)
                _errorMessage.value = "No se pudo agregar el lugar. Intenta nuevamente."
            }
        }
    }

    fun updatePlace(place: PlaceEntity) {
        viewModelScope.launch {
            try {
                Log.d("MapViewModel", "Actualizando lugar: ${place.name}")
                repository.updatePlace(place)
                Log.i("MapViewModel", "Lugar actualizado exitosamente con ID: ${place.id}")
                _selectedPlace.value = null
                _showDialog.value = false
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error al actualizar lugar", e)
                _errorMessage.value = "No se pudo actualizar el lugar. Intenta nuevamente."
            }
        }
    }

    fun deletePlace(place: PlaceEntity) {
        viewModelScope.launch {
            try {
                Log.d("MapViewModel", "Eliminando lugar: ${place.name}")
                repository.deletePlace(place)
                Log.i("MapViewModel", "Lugar eliminado exitosamente con ID: ${place.id}")
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error al eliminar lugar", e)
                _errorMessage.value = "No se pudo eliminar el lugar. Intenta nuevamente."
            }
        }
    }

    fun toggleFavorite(place: PlaceEntity) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(place.id, place.isFavorite)
                Log.d("MapViewModel", "Se cambió favorito de: ${place.name} a ${!place.isFavorite}")
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error al cambiar favorito", e)
                _errorMessage.value = "No se pudo cambiar el favorito. Intenta nuevamente."
            }
        }
    }

    fun filterByCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun showAddDialog(latLng: LatLng) {
        _mapCenter.value = latLng
        _selectedPlace.value = null
        _showDialog.value = true
    }

    fun showEditDialog(place: PlaceEntity) {
        _selectedPlace.value = place
        _showDialog.value = true
    }

    fun dismissDialog() {
        _showDialog.value = false
        _selectedPlace.value = null
    }

    /**
     * Estadísticas sobre los lugares guardados
     */
    val placeStatistics: StateFlow<PlaceStatistics> = places
        .map { placesList ->
            PlaceStatistics(
                totalPlaces = placesList.size,
                favoriteCount = placesList.count { it.isFavorite },
                categoryCounts = placesList.groupingBy { it.category }.eachCount(),
                mostRecentPlace = placesList.maxByOrNull { it.createdAt }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlaceStatistics()
        )


}