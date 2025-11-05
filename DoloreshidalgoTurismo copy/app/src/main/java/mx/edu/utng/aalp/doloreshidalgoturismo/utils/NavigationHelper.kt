package mx.edu.utng.aalp.doloreshidalgoturismo.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import mx.edu.utng.aalp.doloreshidalgoturismo.data.model.PlaceEntity

/**
 * Utilidad para abrir navegación en Google Maps
 */
object NavigationHelper {

    /**
     * Abrir Google Maps para navegar a un lugar
     * @param context: Contexto de la aplicación
     * @param place: Lugar de destino
     */
    fun openGoogleMapsNavigation(context: Context, place: PlaceEntity) {
        val uri = Uri.parse(
            "google.navigation:q=${place.latitude},${place.longitude}&mode=d"
        )

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            val browserUri = Uri.parse(
                "https://www.google.com/maps/dir/?api=1&destination=${place.latitude},${place.longitude}"
            )
            val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
            context.startActivity(browserIntent)
        }
    }

    /**
     * Abrir Google Maps solo para ver el lugar (sin navegación)
     */
    fun openGoogleMapsView(context: Context, place: PlaceEntity) {
        val uri = Uri.parse(
            "geo:${place.latitude},${place.longitude}?q=${place.latitude},${place.longitude}(${place.name})"
        )

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            val browserUri = Uri.parse(
                "https://www.google.com/maps/search/?api=1&query=${place.latitude},${place.longitude}"
            )
            val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
            context.startActivity(browserIntent)
        }
    }

    /**
     * Compartir ubicación de un lugar
     */
    fun sharePlaceLocation(context: Context, place: PlaceEntity) {
        val shareText = """
            📍 ${place.name}
            
            ${place.description}
            
            🗺️ Ver en Google Maps:
            https://www.google.com/maps/search/?api=1&query=${place.latitude},${place.longitude}
            
            📱 Compartido desde Turismo Dolores Hidalgo
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Lugar turístico: ${place.name}")
        }

        context.startActivity(Intent.createChooser(intent, "Compartir lugar"))
    }
}
