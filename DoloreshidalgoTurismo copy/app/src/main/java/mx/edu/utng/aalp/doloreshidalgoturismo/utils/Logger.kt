package mx.edu.utng.aalp.doloreshidalgoturismo.utils
import android.util.Log

/**
 * Utilidad para logging consistente en toda la aplicación
 */
object Logger {
    private const val TAG = "DoloreshidalgoTurismo"

    fun d(message: String) {
        Log.d(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }

    fun i(message: String) {
        Log.i(TAG, message)
    }

    fun w(message: String) {
        Log.w(TAG, message)
    }
}