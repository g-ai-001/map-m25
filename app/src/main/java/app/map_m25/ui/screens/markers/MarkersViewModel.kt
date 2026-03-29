package app.map_m25.ui.screens.markers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.domain.model.MapMarker
import app.map_m25.domain.model.MarkerCategory
import app.map_m25.domain.repository.MarkerCategoryRepository
import app.map_m25.domain.repository.MarkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarkersUiState(
    val markers: List<MapMarker> = emptyList(),
    val categories: List<MarkerCategory> = emptyList(),
    val selectedCategoryId: Long? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class MarkersViewModel @Inject constructor(
    private val markerRepository: MarkerRepository,
    private val markerCategoryRepository: MarkerCategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarkersUiState())
    val uiState: StateFlow<MarkersUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            combine(
                markerRepository.getAllMarkers(),
                markerCategoryRepository.getAllCategories()
            ) { markers, categories ->
                Pair(markers, categories)
            }.collect { (markers, categories) ->
                _uiState.value = _uiState.value.copy(
                    markers = markers,
                    categories = categories,
                    isLoading = false
                )
            }
        }
    }

    fun selectCategory(categoryId: Long?) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
        viewModelScope.launch {
            if (categoryId == null) {
                markerRepository.getAllMarkers().collect { markers ->
                    _uiState.value = _uiState.value.copy(markers = markers)
                }
            } else {
                markerRepository.getMarkersByCategory(categoryId).collect { markers ->
                    _uiState.value = _uiState.value.copy(markers = markers)
                }
            }
        }
    }

    fun addCategory(name: String, color: Int) {
        viewModelScope.launch {
            markerCategoryRepository.saveCategory(MarkerCategory(name = name, color = color))
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            markerCategoryRepository.deleteCategory(categoryId)
            if (_uiState.value.selectedCategoryId == categoryId) {
                selectCategory(null)
            }
        }
    }

    fun updateMarkerCategory(markerId: Long, categoryId: Long?) {
        viewModelScope.launch {
            val marker = markerRepository.getMarkerById(markerId)
            marker?.let {
                markerRepository.updateMarker(it.copy(categoryId = categoryId))
            }
        }
    }

    fun deleteMarker(markerId: Long) {
        viewModelScope.launch {
            markerRepository.deleteMarker(markerId)
        }
    }
}
