package app.map_m25.ui.screens.offline

import app.map_m25.domain.model.OfflineRegion

data class OfflineRegionsUiState(
    val regions: List<OfflineRegion> = emptyList(),
    val isLoading: Boolean = false,
    val showAddDialog: Boolean = false,
    val editingRegion: OfflineRegion? = null,
    val error: String? = null
)

sealed class OfflineRegionsEvent {
    data object LoadRegions : OfflineRegionsEvent()
    data object ShowAddDialog : OfflineRegionsEvent()
    data object HideDialog : OfflineRegionsEvent()
    data class EditRegion(val region: OfflineRegion) : OfflineRegionsEvent()
    data class DeleteRegion(val id: Long) : OfflineRegionsEvent()
    data class SaveRegion(
        val id: Long = 0,
        val name: String,
        val minLatitude: Double,
        val maxLatitude: Double,
        val minLongitude: Double,
        val maxLongitude: Double,
        val zoomLevel: Int
    ) : OfflineRegionsEvent()
}
