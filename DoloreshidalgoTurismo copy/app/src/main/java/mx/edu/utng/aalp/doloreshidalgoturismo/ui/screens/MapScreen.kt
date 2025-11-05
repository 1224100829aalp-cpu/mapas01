package mx.edu.utng.aalp.doloreshidalgoturismo.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import mx.edu.utng.aalp.doloreshidalgoturismo.ui.componets.PlaceDialog
import mx.edu.utng.aalp.doloreshidalgoturismo.ui.componets.PlacesList
import mx.edu.utng.aalp.doloreshidalgoturismo.ui.componets.StatisticsPanel
import mx.edu.utng.aalp.doloreshidalgoturismo.ui.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel) {

    // Estados del ViewModel
    val places by viewModel.places.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val selectedPlace by viewModel.selectedPlace.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val statistics by viewModel.placeStatistics.collectAsState() // Estadísticas

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Estado de búsqueda
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    // Filtrar lugares según búsqueda
    val filteredPlaces = remember(places, searchQuery) {
        if (searchQuery.isBlank()) places
        else places.filter { place ->
            place.name.contains(searchQuery, ignoreCase = true) ||
                    place.description.contains(searchQuery, ignoreCase = true) ||
                    place.category.contains(searchQuery, ignoreCase = true)
        }
    }

    // Estado del drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estado del mapa
    val doloreshidalgoCenter = LatLng(21.1560, -100.9318)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(doloreshidalgoCenter, 14f)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Contenido del drawer: estadísticas y acciones
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Menú",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    StatisticsPanel(statistics = statistics)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { /* TODO: Implementar exportación */ }, modifier = Modifier.fillMaxWidth()) {
                        Text("Exportar datos")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* TODO: Implementar importación */ }, modifier = Modifier.fillMaxWidth()) {
                        Text("Importar datos")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { scope.launch { drawerState.close() } }, modifier = Modifier.fillMaxWidth()) {
                        Text("Cerrar menú")
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = { Text("Turismo Dolores Hidalgo") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menú")
                            }
                        },
                        actions = {
                            IconButton(onClick = { showSearchBar = !showSearchBar }) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    if (showSearchBar) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = { },
                            active = false,
                            onActiveChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            placeholder = { Text("Buscar lugares...") }
                        ) { }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(onClick = { viewModel.showAddDialog(cameraPositionState.position.target) }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar lugar")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Google Map
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = false),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = false),
                    onMapLongClick = { latLng -> viewModel.showAddDialog(latLng) }
                ) {
                    filteredPlaces.forEach { place ->
                        val position = LatLng(place.latitude, place.longitude)
                        Marker(
                            state = MarkerState(position = position),
                            title = place.name,
                            snippet = place.description,
                            onInfoWindowClick = { viewModel.showEditDialog(place) }
                        )
                    }
                }

                // Lista de lugares
                PlacesList(
                    places = filteredPlaces,
                    onPlaceClick = { place ->
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(place.latitude, place.longitude), 16f
                        )
                    },
                    onEditClick = { place -> viewModel.showEditDialog(place) },
                    onDeleteClick = { place -> viewModel.deletePlace(place) },
                    onFavoriteClick = { place -> viewModel.toggleFavorite(place) }
                )
            }

            // Diálogo para agregar/editar lugar
            if (showDialog) {
                PlaceDialog(
                    place = selectedPlace,
                    onDismiss = { viewModel.dismissDialog() },
                    onSave = { name, description, latLng, category, color ->
                        if (selectedPlace != null) {
                            viewModel.updatePlace(
                                selectedPlace!!.copy(
                                    name = name,
                                    description = description,
                                    latitude = latLng.latitude,
                                    longitude = latLng.longitude,
                                    category = category,
                                    markerColor = color
                                )
                            )
                        } else {
                            viewModel.addPlace(name, description, latLng, category, color)
                        }
                    }
                )
            }
        }
    }
}
