package app.map_m25.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.domain.model.MapLocation
import app.map_m25.domain.repository.LocationRepository
import app.map_m25.domain.repository.SearchHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<MapLocation> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isSearching: Boolean = false,
    val showRecentSearches: Boolean = true
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadRecentSearches()
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            searchHistoryRepository.getRecentSearches().collect { searches ->
                _uiState.value = _uiState.value.copy(recentSearches = searches)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            showRecentSearches = query.isEmpty()
        )
        if (query.isNotEmpty()) {
            search(query)
        } else {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            locationRepository.searchLocations(query).collect { results ->
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isSearching = false
                )
            }
        }
    }

    fun onSearchSubmit(query: String) {
        if (query.isNotBlank()) {
            viewModelScope.launch {
                searchHistoryRepository.addSearch(query)
            }
        }
    }

    fun onRecentSearchClick(keyword: String) {
        onSearchQueryChange(keyword)
        search(keyword)
    }

    fun deleteRecentSearch(keyword: String) {
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
