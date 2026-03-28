package app.map_m25.data.repository

import app.map_m25.data.local.dao.SearchHistoryDao
import app.map_m25.data.local.entity.SearchHistoryEntity
import app.map_m25.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryRepository {

    override fun getRecentSearches(): Flow<List<String>> {
        return searchHistoryDao.getRecentSearches().map { entities ->
            entities.map { it.keyword }
        }
    }

    override suspend fun addSearch(keyword: String) {
        if (keyword.isNotBlank()) {
            searchHistoryDao.insertSearch(SearchHistoryEntity(keyword = keyword.trim()))
        }
    }

    override suspend fun deleteSearch(keyword: String) {
        searchHistoryDao.deleteSearch(keyword)
    }

    override suspend fun clearAllHistory() {
        searchHistoryDao.clearAllHistory()
    }
}
