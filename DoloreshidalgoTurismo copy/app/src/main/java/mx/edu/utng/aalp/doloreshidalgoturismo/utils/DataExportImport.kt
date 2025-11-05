package mx.edu.utng.aalp.doloreshidalgoturismo.utils

import android.content.Context
import mx.edu.utng.aalp.doloreshidalgoturismo.data.model.PlaceEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Utilidad para exportar e importar lugares en formato JSON
 */
object DataExportImport{

    /**
     * Exportar lugares a archivo JSON
     */
    fun exportPlacesToJson(places: List<PlaceEntity>, context: Context): File {
        val jsonArray = JSONArray()

        places.forEach { place ->
            val jsonObject = JSONObject().apply {
                put("name", place.name)
                put("description", place.description)
                put("latitude", place.latitude)
                put("longitude", place.longitude)
                put("category", place.category)
                put("markerColor", place.markerColor)
                put("isFavorite", place.isFavorite)
            }
            jsonArray.put(jsonObject)
        }

        // Guardar en el almacenamiento externo
        val file = File(context.getExternalFilesDir(null), "lugares_turisticos_${System.currentTimeMillis()}.json")
        file.writeText(jsonArray.toString(2)) // 2 = indentación para legibilidad

        return file
    }

    /**
     * Importar lugares desde archivo JSON
     */
    fun importPlacesFromJson(jsonString: String): List<PlaceEntity> {
        val places = mutableListOf<PlaceEntity>()
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val place = PlaceEntity(
                name = jsonObject.getString("name"),
                description = jsonObject.optString("description", ""),
                latitude = jsonObject.getDouble("latitude"),
                longitude = jsonObject.getDouble("longitude"),
                category = jsonObject.optString("category", "Plaza"),
                markerColor = jsonObject.optString("markerColor", "#FF6B35"),
                isFavorite = jsonObject.optBoolean("isFavorite", false)
            )
            places.add(place)
        }

        return places
    }
}
