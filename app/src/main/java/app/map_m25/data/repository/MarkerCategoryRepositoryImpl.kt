package app.map_m25.data.repository

import app.map_m25.data.local.dao.MarkerCategoryDao
import app.map_m25.data.local.entity.MarkerCategoryEntity
import app.map_m25.domain.model.MarkerCategory
import app.map_m25.domain.repository.MarkerCategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarkerCategoryRepositoryImpl @Inject constructor(
    private val markerCategoryDao: MarkerCategoryDao
) : MarkerCategoryRepository {

    override fun getAllCategories(): Flow<List<MarkerCategory>> {
        return markerCategoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCategoryById(id: Long): MarkerCategory? {
        return markerCategoryDao.getCategoryById(id)?.toDomain()
    }

    override suspend fun saveCategory(category: MarkerCategory): Long {
        return markerCategoryDao.insertCategory(category.toEntity())
    }

    override suspend fun updateCategory(category: MarkerCategory) {
        markerCategoryDao.updateCategory(category.toEntity())
    }

    override suspend fun deleteCategory(id: Long) {
        markerCategoryDao.deleteCategoryById(id)
    }

    private fun MarkerCategoryEntity.toDomain() = MarkerCategory(
        id = id,
        name = name,
        color = color,
        iconName = iconName,
        createdAt = createdAt
    )

    private fun MarkerCategory.toEntity() = MarkerCategoryEntity(
        id = id,
        name = name,
        color = color,
        iconName = iconName,
        createdAt = createdAt
    )
}