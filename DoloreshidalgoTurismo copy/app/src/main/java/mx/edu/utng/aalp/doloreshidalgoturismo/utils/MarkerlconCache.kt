package mx.edu.utng.aalp.doloreshidalgoturismo.utils
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/**
 * Caché para iconos de marcadores
 * Evita recrear bitmaps innecesariamente
 */
object MarkerIconCache {
    private val cache = mutableMapOf<String, BitmapDescriptor>()

    /**
     * Obtener o crear un icono de marcador con color personalizado
     */
    fun getColoredMarker(context: Context, colorHex: String): BitmapDescriptor {
        return cache.getOrPut(colorHex) {
            createColoredMarkerBitmap(colorHex)
        }
    }

    /**
     * Crear un bitmap de marcador con color personalizado
     */
    private fun createColoredMarkerBitmap(colorHex: String): BitmapDescriptor {
        val color = android.graphics.Color.parseColor(colorHex)
        // Crear un bitmap personalizado
        val size = 100
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        // Dibujar círculo coloreado
        val paint = Paint().apply {
            this.color = color
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        canvas.drawCircle(
            size / 2f,
            size / 2f,
            size / 2.5f,
            paint
        )
        // Borde blanco
        paint.apply {
            this.color = android.graphics.Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }
        canvas.drawCircle(
            size / 2f,
            size / 2f,
            size / 2.5f,
            paint
        )
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    /**
     * Limpiar el caché
     */
    fun clearCache() {
        cache.clear()
    }
}