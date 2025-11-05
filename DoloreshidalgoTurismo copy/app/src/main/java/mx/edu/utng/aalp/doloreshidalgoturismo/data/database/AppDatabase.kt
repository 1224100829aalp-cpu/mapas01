package mx.edu.utng.aalp.doloreshidalgoturismo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mx.edu.utng.aalp.doloreshidalgoturismo.data.dao.PlaceDao
import mx.edu.utng.aalp.doloreshidalgoturismo.data.model.PlaceEntity


/**
 * Base de datos principal de la aplicación.
 * Singleton: solo existe una instancia en toda la app (como un único archivo maestro).
 */
@Database(
    entities = [PlaceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao

    companion object {
        // @Volatile asegura que todos los threads vean la misma instancia
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtener o crear la base de datos.
         * Patrón Singleton: garantiza una única instancia.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dolores_hidalgo_tourism_db"
                )
                    .fallbackToDestructiveMigration() // En producción, usar migraciones adecuadas
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}