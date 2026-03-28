package app.map_m25.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.domain.repository.LocationRepository
import app.map_m25.domain.repository.SearchHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryItem(
    val keyword: String,
    val timestamp: Long,
    val isLocationResult: Boolean = false,
    val locationName: String? = null
)

data class HistoryUiState(
    val historyItems: List<HistoryItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            searchHistoryRepository.getRecentSearches().collect { searches ->
                val historyItems = searches.map { keyword ->
                    HistoryItem(
                        keyword = keyword,
                        timestamp = System.currentTimeMillis()
                    )
                }
                _uiState.value = HistoryUiState(
                    historyItems = historyItems,
                    isLoading = false
                )
            }
        }
    }

    fun deleteHistoryItem(keyword: String) {
        viewModelScope.launch {
            searchHistoryRepository.deleteSearch(keyword)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clearAllHistory()
        }
    }
}