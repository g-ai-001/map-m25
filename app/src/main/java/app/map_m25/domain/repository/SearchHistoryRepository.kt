package app.map_m25.domain.repository

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getRecentSearches(): Flow<List<String>>
    suspend fun addSearch(keyword: String)
    suspend fun deleteSearch(keyword: String)
    suspend fun clearAllHistory()
}
