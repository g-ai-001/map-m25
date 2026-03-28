package app.map_m25.ui.screens.markers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.domain.model.MapMarker
import app.map_m25.domain.repository.MarkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarkersUiState(
    val markers: List<MapMarker> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class MarkersViewModel @Inject constructor(
    private val markerRepository: MarkerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarkersUiState())
    val uiState: StateFlow<MarkersUiState> = _uiState.asStateFlow()

    init {
        loadMarkers()
    }

    private fun loadMarkers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            markerRepository.getAllMarkers().collect { markers ->
                _uiState.value = _uiState.value.copy(
                    markers = markers,
                    isLoading = false
                )
            }
        }
    }

    fun deleteMarker(markerId: Long) {
        viewModelScope.launch {
            markerRepository.deleteMarker(markerId)
        }
    }
}
