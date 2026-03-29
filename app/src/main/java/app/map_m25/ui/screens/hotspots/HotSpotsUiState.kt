package app.map_m25.ui.screens.hotspots

import app.map_m25.domain.model.HotSpotLocation

data class HotSpotsUiState(
    val hotSpots: List<HotSpotLocation> = emptyList(),
    val isLoading: Boolean = false
)

sealed class HotSpotsEvent {
    data object LoadHotSpots : HotSpotsEvent()
}
