package app.map_m25.ui.screens.snapshot

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class MapSnapshot(
    val id: String,
    val filePath: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val zoom: Float
)

data class MapSnapshotsUiState(
    val snapshots: List<MapSnapshot> = emptyList(),
    val isLoading: Boolean = false,
    val isCapturing: Boolean = false,
    val lastCapturePath: String? = null,
    val error: String? = null
)

sealed class MapSnapshotsEvent {
    data object LoadSnapshots : MapSnapshotsEvent()
    data class CaptureSnapshot(
        val view: View,
        val latitude: Double,
        val longitude: Double,
        val zoom: Float
    ) : MapSnapshotsEvent()
    data class DeleteSnapshot(val id: String) : MapSnapshotsEvent()
    data object ClearError : MapSnapshotsEvent()
}

@HiltViewModel
class MapSnapshotsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapSnapshotsUiState())
    val uiState: StateFlow<MapSnapshotsUiState> = _uiState.asStateFlow()

    private val snapshotDir: File by lazy {
        File(context.getExternalFilesDir(null), "snapshots").also {
            if (!it.exists()) it.mkdirs()
        }
    }

    init {
        loadSnapshots()
    }

    fun onEvent(event: MapSnapshotsEvent) {
        when (event) {
            is MapSnapshotsEvent.LoadSnapshots -> loadSnapshots()
            is MapSnapshotsEvent.CaptureSnapshot -> captureSnapshot(event.view, event.latitude, event.longitude, event.zoom)
            is MapSnapshotsEvent.DeleteSnapshot -> deleteSnapshot(event.id)
            is MapSnapshotsEvent.ClearError -> clearError()
        }
    }

    private fun loadSnapshots() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val snapshots = withContext(Dispatchers.IO) {
                snapshotDir.listFiles()
                    ?.filter { it.extension == "png" }
                    ?.mapNotNull { file ->
                        val parts = file.nameWithoutExtension.split("_")
                        if (parts.size >= 4) {
                            MapSnapshot(
                                id = file.nameWithoutExtension,
                                filePath = file.absolutePath,
                                timestamp = file.lastModified(),
                                latitude = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0,
                                longitude = parts.getOrNull(2)?.toDoubleOrNull() ?: 0.0,
                                zoom = parts.getOrNull(3)?.toFloatOrNull() ?: 0f
                            )
                        } else null
                    }
                    ?.sortedByDescending { it.timestamp }
                    ?: emptyList()
            }
            _uiState.update { it.copy(snapshots = snapshots, isLoading = false) }
        }
    }

    private fun captureSnapshot(view: View, latitude: Double, longitude: Double, zoom: Float) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCapturing = true) }
            try {
                val bitmap = withContext(Dispatchers.Default) {
                    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    view.draw(canvas)
                    bitmap
                }

                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "snapshot_${latitude}_${longitude}_${zoom}_$timestamp.png"
                val file = File(snapshotDir, fileName)

                withContext(Dispatchers.IO) {
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    bitmap.recycle()
                }

                _uiState.update {
                    it.copy(
                        isCapturing = false,
                        lastCapturePath = file.absolutePath
                    )
                }
                loadSnapshots()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCapturing = false,
                        error = "截图失败: ${e.message}"
                    )
                }
            }
        }
    }

    private fun deleteSnapshot(id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                File(snapshotDir, "$id.png").delete()
            }
            loadSnapshots()
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
