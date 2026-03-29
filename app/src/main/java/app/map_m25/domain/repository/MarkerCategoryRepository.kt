package app.map_m25.domain.repository

import app.map_m25.domain.model.MarkerCategory
import kotlinx.coroutines.flow.Flow

interface MarkerCategoryRepository {
    fun getAllCategories(): Flow<List<MarkerCategory>>
    suspend fun getCategoryById(id: Long): MarkerCategory?
    suspend fun saveCategory(category: MarkerCategory): Long
    suspend fun updateCategory(category: MarkerCategory)
    suspend fun deleteCategory(id: Long)
}