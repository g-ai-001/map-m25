package app.map_m25.ui.screens.hotspots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.domain.model.HotSpotLocation
import app.map_m25.domain.model.LocationCategory
import app.map_m25.domain.repository.LocationRepository
import app.map_m25.domain.repository.SearchHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotSpotsViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotSpotsUiState())
    val uiState: StateFlow<HotSpotsUiState> = _uiState.asStateFlow()

    init {
        loadHotSpots()
    }

    fun onEvent(event: HotSpotsEvent) {
        when (event) {
            is HotSpotsEvent.LoadHotSpots -> loadHotSpots()
        }
    }

    private fun loadHotSpots() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val favorites = locationRepository.getAllLocations().first()
            val searchHistory = searchHistoryRepository.getAllHistory().first()

            val categoryCount = mutableMapOf<LocationCategory, Int>()
            val categoryLocations = mutableMapOf<LocationCategory, MutableList<HotSpotLocation>>()

            favorites.forEach { location ->
                val category = location.category
                categoryCount[category] = (categoryCount[category] ?: 0) + 1
                if (!categoryLocations.containsKey(category)) {
                    categoryLocations[category] = mutableListOf()
                }
                categoryLocations[category]?.add(
                    HotSpotLocation(
                        id = location.id,
                        name = location.name,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        address = location.address,
                        visitCount = categoryCount[category] ?: 1,
                        category = category
                    )
                )
            }

            searchHistory.take(20).forEach { history ->
                val category = LocationCategory.OTHER
                categoryCount[category] = (categoryCount[category] ?: 0) + 1
                if (!categoryLocations.containsKey(category)) {
                    categoryLocations[category] = mutableListOf()
                }
                categoryLocations[category]?.add(
                    HotSpotLocation(
                        id = history.id,
                        name = history.name,
                        latitude = history.latitude,
                        longitude = history.longitude,
                        address = history.address,
                        visitCount = categoryCount[category] ?: 1,
                        category = category
                    )
                )
            }

            val hotSpots = categoryLocations.values
                .flatten()
                .sortedByDescending { it.visitCount }
                .take(10)

            _uiState.update { it.copy(hotSpots = hotSpots, isLoading = false) }
        }
    }
}
