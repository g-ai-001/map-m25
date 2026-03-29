package app.map_m25

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltAndroidApp
class MapApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        initLogger()
    }

    private fun initLogger() {
        val logDir = getExternalFilesDir(null) ?: return
        val logFile = File(logDir, "app.log")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val currentTime = dateFormat.format(Date())
        logFile.appendText("\n--- App Started: $currentTime ---\n")
    }

    companion object {
        private lateinit var instance: MapApplication

        fun log(tag: String, message: String) {
            Log.d(tag, message)
            val logDir = instance.getExternalFilesDir(null) ?: return
            val logFile = File(logDir, "app.log")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentTime = dateFormat.format(Date())
            logFile.appendText("[$currentTime] [$tag] $message\n")
        }

        fun logError(tag: String, message: String, throwable: Throwable? = null) {
            Log.e(tag, message, throwable)
            val logDir = instance.getExternalFilesDir(null) ?: return
            val logFile = File(logDir, "app.log")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentTime = dateFormat.format(Date())
            val errorMsg = if (throwable != null) {
                "$message\n${throwable.stackTraceToString()}"
            } else {
                message
            }
            logFile.appendText("[$currentTime] [$tag] ERROR: $errorMsg\n")
        }
    }
}
