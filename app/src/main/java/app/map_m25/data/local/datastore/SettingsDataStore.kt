package app.map_m25.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.map_m25.domain.model.MapLayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(
    private val context: Context
) {
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val MAP_ZOOM = floatPreferencesKey("map_zoom")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val HIGH_REFRESH_RATE = booleanPreferencesKey("high_refresh_rate")
        val MAP_LAYER = stringPreferencesKey("map_layer")
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.DARK_MODE] ?: false
    }

    val mapZoom: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[Keys.MAP_ZOOM] ?: 15f
    }

    val keepScreenOn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.KEEP_SCREEN_ON] ?: false
    }

    val highRefreshRate: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.HIGH_REFRESH_RATE] ?: true
    }

    val mapLayer: Flow<MapLayer> = context.dataStore.data.map { prefs ->
        val layerName = prefs[Keys.MAP_LAYER] ?: MapLayer.NORMAL.name
        try {
            MapLayer.valueOf(layerName)
        } catch (e: IllegalArgumentException) {
            MapLayer.NORMAL
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = enabled
        }
    }

    suspend fun setMapZoom(zoom: Float) {
        context.dataStore.edit { prefs ->
            prefs[Keys.MAP_ZOOM] = zoom
        }
    }

    suspend fun setKeepScreenOn(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.KEEP_SCREEN_ON] = enabled
        }
    }

    suspend fun setHighRefreshRate(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HIGH_REFRESH_RATE] = enabled
        }
    }

    suspend fun setMapLayer(layer: MapLayer) {
        context.dataStore.edit { prefs ->
            prefs[Keys.MAP_LAYER] = layer.name
        }
    }
}
