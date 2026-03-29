package app.map_m25.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marker_categories")
data class MarkerCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: Int,
    val iconName: String = "LocationOn",
    val createdAt: Long = System.currentTimeMillis()
)