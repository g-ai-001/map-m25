package app.map_m25.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.domain.model.MapLocation
import app.map_m25.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val favorites: List<MapLocation> = emptyList(),
    val isLoading: Boolean = true,
    val sortOrder: SortOrder = SortOrder.TIME_DESC
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private var allFavorites: List<MapLocation> = emptyList()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            locationRepository.getFavoriteLocations().collect { favorites ->
                allFavorites = favorites
                _uiState.value = _uiState.value.copy(
                    favorites = sortFavorites(favorites, _uiState.value.sortOrder),
                    isLoading = false
                )
            }
        }
    }

    private fun sortFavorites(favorites: List<MapLocation>, sortOrder: SortOrder): List<MapLocation> {
        return when (sortOrder) {
            SortOrder.NAME_ASC -> favorites.sortedBy { it.name.lowercase() }
            SortOrder.NAME_DESC -> favorites.sortedByDescending { it.name.lowercase() }
            SortOrder.TIME_ASC -> favorites.sortedBy { it.id }
            SortOrder.TIME_DESC -> favorites.sortedByDescending { it.id }
        }
    }

    fun setSortOrder(sortOrder: SortOrder) {
        _uiState.value = _uiState.value.copy(
            sortOrder = sortOrder,
            favorites = sortFavorites(allFavorites, sortOrder)
        )
    }

    fun removeFavorite(location: MapLocation) {
        viewModelScope.launch {
            locationRepository.toggleFavorite(location.id, false)
        }
    }

    fun deleteLocation(location: MapLocation) {
        viewModelScope.launch {
            locationRepository.deleteLocation(location)
        }
    }
}
