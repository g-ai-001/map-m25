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
        val MAP_STYLE = stringPreferencesKey("map_style")
        val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        val SHOW_POI_LABELS = booleanPreferencesKey("show_poi_labels")
        val SHOW_ROAD_NAMES = booleanPreferencesKey("show_road_names")
        val SHOW_TRAFFIC_SIGNS = booleanPreferencesKey("show_traffic_signs")
        val SHOW_BUILDING_LABELS = booleanPreferencesKey("show_building_labels")
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

    val mapStyle: Flow<MapStyle> = context.dataStore.data.map { prefs ->
        val styleName = prefs[Keys.MAP_STYLE] ?: MapStyle.STANDARD.name
        try {
            MapStyle.valueOf(styleName)
        } catch (e: IllegalArgumentException) {
            MapStyle.STANDARD
        }
    }

    val voiceEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.VOICE_ENABLED] ?: true
    }

    val showPoiLabels: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SHOW_POI_LABELS] ?: true
    }

    val showRoadNames: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SHOW_ROAD_NAMES] ?: true
    }

    val showTrafficSigns: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SHOW_TRAFFIC_SIGNS] ?: true
    }

    val showBuildingLabels: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SHOW_BUILDING_LABELS] ?: true
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

    suspend fun setMapStyle(style: MapStyle) {
        context.dataStore.edit { prefs ->
            prefs[Keys.MAP_STYLE] = style.name
        }
    }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.VOICE_ENABLED] = enabled
        }
    }

    suspend fun setShowPoiLabels(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_POI_LABELS] = show
        }
    }

    suspend fun setShowRoadNames(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_ROAD_NAMES] = show
        }
    }

    suspend fun setShowTrafficSigns(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_TRAFFIC_SIGNS] = show
        }
    }

    suspend fun setShowBuildingLabels(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_BUILDING_LABELS] = show
        }
    }
}
