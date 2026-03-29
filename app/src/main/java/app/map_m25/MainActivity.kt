package app.map_m25

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.map_m25.data.local.datastore.SettingsDataStore
import app.map_m25.ui.navigation.MapNavHost
import app.map_m25.ui.theme.MapAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MapApplication.log("MainActivity", "onCreate")

        // Apply high refresh rate setting
        applyRefreshRateSettings()

        val initialDarkMode = runBlocking { settingsDataStore.darkMode.first() }

        setContent {
            val darkMode by settingsDataStore.darkMode.collectAsState(initial = initialDarkMode)
            MapAppTheme(darkTheme = darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MapNavHost()
                }
            }
        }
    }

    private fun applyRefreshRateSettings() {
        val highRefreshRate = runBlocking {
            settingsDataStore.highRefreshRate.first()
        }

        if (highRefreshRate) {
            // Request highest available refresh rate
            window.attributes = window.attributes.apply {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                }
            }

            // Set preferred frame rate to highest available
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                display?.let { display ->
                    val modes = display.supportedModes
                    val highestRefreshMode = modes.maxByOrNull { it.refreshRate }
                    highestRefreshMode?.let { mode ->
                        window.attributes = window.attributes.apply {
                            preferredDisplayModeId = mode.modeId
                        }
                    }
                }
            }
        }
    }
}
