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
    val isLoading: Boolean = true
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            locationRepository.getFavoriteLocations().collect { favorites ->
                _uiState.value = FavoritesUiState(
                    favorites = favorites,
                    isLoading = false
                )
            }
        }
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
