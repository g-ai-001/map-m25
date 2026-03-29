package app.map_m25.ui.screens.offline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.domain.model.OfflineRegion
import app.map_m25.domain.repository.OfflineRegionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfflineRegionsViewModel @Inject constructor(
    private val offlineRegionRepository: OfflineRegionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OfflineRegionsUiState())
    val uiState: StateFlow<OfflineRegionsUiState> = _uiState.asStateFlow()

    init {
        loadRegions()
    }

    fun onEvent(event: OfflineRegionsEvent) {
        when (event) {
            is OfflineRegionsEvent.LoadRegions -> loadRegions()
            is OfflineRegionsEvent.ShowAddDialog -> showAddDialog()
            is OfflineRegionsEvent.HideDialog -> hideDialog()
            is OfflineRegionsEvent.EditRegion -> editRegion(event.region)
            is OfflineRegionsEvent.DeleteRegion -> deleteRegion(event.id)
            is OfflineRegionsEvent.SaveRegion -> saveRegion(event)
        }
    }

    private fun loadRegions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            offlineRegionRepository.getAllRegions().collect { regions ->
                _uiState.update { it.copy(regions = regions, isLoading = false) }
            }
        }
    }

    private fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true, editingRegion = null) }
    }

    private fun hideDialog() {
        _uiState.update { it.copy(showAddDialog = false, editingRegion = null) }
    }

    private fun editRegion(region: OfflineRegion) {
        _uiState.update { it.copy(showAddDialog = true, editingRegion = region) }
    }

    private fun deleteRegion(id: Long) {
        viewModelScope.launch {
            offlineRegionRepository.deleteRegion(id)
        }
    }

    private fun saveRegion(event: OfflineRegionsEvent.SaveRegion) {
        viewModelScope.launch {
            val region = OfflineRegion(
                id = event.id,
                name = event.name,
                minLatitude = event.minLatitude,
                maxLatitude = event.maxLatitude,
                minLongitude = event.minLongitude,
                maxLongitude = event.maxLongitude,
                zoomLevel = event.zoomLevel,
                updatedAt = System.currentTimeMillis()
            )
            if (event.id == 0L) {
                offlineRegionRepository.insertRegion(region)
            } else {
                offlineRegionRepository.updateRegion(region)
            }
            hideDialog()
        }
    }
}
