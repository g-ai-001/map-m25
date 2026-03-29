package app.map_m25.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.map_m25.data.local.entity.MarkerCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkerCategoryDao {
    @Query("SELECT * FROM marker_categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<MarkerCategoryEntity>>

    @Query("SELECT * FROM marker_categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): MarkerCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: MarkerCategoryEntity): Long

    @Update
    suspend fun updateCategory(category: MarkerCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: MarkerCategoryEntity)

    @Query("DELETE FROM marker_categories WHERE id = :id")
    suspend fun deleteCategoryById(id: Long)
}