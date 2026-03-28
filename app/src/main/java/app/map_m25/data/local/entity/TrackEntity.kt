package app.map_m25.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val totalDistance: Float = 0f,
    val createdAt: Long = System.currentTimeMillis()
)
