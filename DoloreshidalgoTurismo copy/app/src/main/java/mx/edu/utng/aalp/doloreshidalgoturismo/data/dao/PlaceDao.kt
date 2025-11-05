package mx.edu.utng.aalp.doloreshidalgoturismo.data.dao

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mx.edu.utng.aalp.doloreshidalgoturismo.data.model.PlaceEntity

/**
 * Define todas las operaciones que podemos hacer con los lugares.
 * Flow nos permite observar cambios en tiempo real (como un notificador automático).
 */
@Dao
interface PlaceDao {

    /**
     * Obtener todos los lugares.
     * Flow actualiza automáticamente la UI cuando hay cambios.
     */
    @Query("SELECT * FROM places ORDER BY createdAt DESC")
    fun getAllPlaces(): Flow<List<PlaceEntity>>

    /**
     * Obtener lugares por categoría.
     * Ejemplo: Solo mostrar iglesias o solo museos.
     */
    @Query("SELECT * FROM places WHERE category = :category")
    fun getPlacesByCategory(category: String): Flow<List<PlaceEntity>>

    /**
     * Buscar un lugar específico por ID.
     */
    @Query("SELECT * FROM places WHERE id = :placeId")
    suspend fun getPlaceById(placeId: Int): PlaceEntity?

    /**
     * Insertar un nuevo lugar.
     * Returns: el ID del lugar insertado.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity): Long

    /**
     * Actualizar un lugar existente.
     * Útil para editar nombre, descripción, etc.
     */
    @Update
    suspend fun updatePlace(place: PlaceEntity)

    /**
     * Eliminar un lugar específico.
     */
    @Delete
    suspend fun deletePlace(place: PlaceEntity)

    /**
     * Eliminar todos los lugares.
     * Útil para reiniciar la aplicación.
     */
    @Query("DELETE FROM places")
    suspend fun deleteAllPlaces()

    /**
     * Marcar o desmarcar como favorito.
     */
    @Query("UPDATE places SET isFavorite = :isFavorite WHERE id = :placeId")
    suspend fun updateFavoriteStatus(placeId: Int, isFavorite: Boolean)

    /**
     * Obtener lugares con paginación
     * Útil si la lista crece mucho
     */
    @Query("SELECT * FROM places ORDER BY createdAt DESC")
    fun getPlacesPaged(): PagingSource<Int, PlaceEntity>
}