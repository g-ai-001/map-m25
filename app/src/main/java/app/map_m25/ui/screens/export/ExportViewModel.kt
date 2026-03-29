package app.map_m25.ui.screens.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.data.export.GpxExporter
import app.map_m25.data.export.KmlExporter
import app.map_m25.domain.model.MapMarker
import app.map_m25.domain.model.Track
import app.map_m25.domain.repository.MarkerRepository
import app.map_m25.domain.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ExportUiState(
    val markers: List<MapMarker> = emptyList(),
    val tracks: List<Track> = emptyList(),
    val selectedMarkerIds: Set<Long> = emptySet(),
    val selectedTrackIds: Set<Long> = emptySet(),
    val exportFormat: ExportFormat = ExportFormat.GPX,
    val isExporting: Boolean = false,
    val exportSuccess: Boolean = false,
    val errorMessage: String? = null
)

enum class ExportFormat {
    GPX, KML
}

@HiltViewModel
class ExportViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val markerRepository: MarkerRepository,
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            markerRepository.getAllMarkers().collect { markers ->
                _uiState.value = _uiState.value.copy(markers = markers)
            }
        }
        viewModelScope.launch {
            trackRepository.getAllTracks().collect { tracks ->
                _uiState.value = _uiState.value.copy(tracks = tracks)
            }
        }
    }

    fun toggleMarkerSelection(markerId: Long) {
        val currentSelection = _uiState.value.selectedMarkerIds
        _uiState.value = _uiState.value.copy(
            selectedMarkerIds = if (currentSelection.contains(markerId)) {
                currentSelection - markerId
            } else {
                currentSelection + markerId
            }
        )
    }

    fun toggleTrackSelection(trackId: Long) {
        val currentSelection = _uiState.value.selectedTrackIds
        _uiState.value = _uiState.value.copy(
            selectedTrackIds = if (currentSelection.contains(trackId)) {
                currentSelection - trackId
            } else {
                currentSelection + trackId
            }
        )
    }

    fun setExportFormat(format: ExportFormat) {
        _uiState.value = _uiState.value.copy(exportFormat = format)
    }

    fun selectAllMarkers() {
        _uiState.value = _uiState.value.copy(
            selectedMarkerIds = _uiState.value.markers.map { it.id }.toSet()
        )
    }

    fun selectAllTracks() {
        _uiState.value = _uiState.value.copy(
            selectedTrackIds = _uiState.value.tracks.map { it.id }.toSet()
        )
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            selectedMarkerIds = emptySet(),
            selectedTrackIds = emptySet()
        )
    }

    fun exportData(): Intent? {
        _uiState.value = _uiState.value.copy(isExporting = true, exportSuccess = false, errorMessage = null)

        val state = _uiState.value
        if (state.selectedMarkerIds.isEmpty() && state.selectedTrackIds.isEmpty()) {
            _uiState.value = state.copy(isExporting = false, errorMessage = "请选择要导出的数据")
            return null
        }

        try {
            val exportDir = File(context.getExternalFilesDir(null), "exports")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            val timestamp = System.currentTimeMillis()
            val extension = if (state.exportFormat == ExportFormat.GPX) "gpx" else "kml"
            val fileName = "map_export_$timestamp.$extension"
            val exportFile = File(exportDir, fileName)

            val content = buildExportContent()
            exportFile.writeText(content)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                exportFile
            )

            _uiState.value = state.copy(isExporting = false, exportSuccess = true)

            return Intent(Intent.ACTION_SEND).apply {
                type = if (state.exportFormat == ExportFormat.GPX) "application/gpx+xml" else "application/vnd.google-earth.kml+xml"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } catch (e: Exception) {
            _uiState.value = state.copy(isExporting = false, errorMessage = "导出失败: ${e.message}")
            return null
        }
    }

    private suspend fun buildExportContent(): String {
        val state = _uiState.value
        val sb = StringBuilder()

        val selectedMarkers = state.markers.filter { it.id in state.selectedMarkerIds }
        val selectedTracks = state.tracks.filter { it.id in state.selectedTrackIds }

        when (state.exportFormat) {
            ExportFormat.GPX -> {
                if (selectedMarkers.isNotEmpty()) {
                    sb.append(GpxExporter.exportMarkers(selectedMarkers))
                    if (selectedTracks.isNotEmpty()) {
                        sb.append("\n")
                    }
                }
                selectedTracks.forEach { track ->
                    val fullTrack = trackRepository.getTrackWithPoints(track.id)
                    fullTrack?.let {
                        sb.append(GpxExporter.exportTrack(it))
                        if (selectedTracks.last() != track) {
                            sb.append("\n")
                        }
                    }
                }
            }
            ExportFormat.KML -> {
                if (selectedMarkers.isNotEmpty()) {
                    sb.append(KmlExporter.exportMarkers(selectedMarkers))
                    if (selectedTracks.isNotEmpty()) {
                        sb.append("\n")
                    }
                }
                selectedTracks.forEach { track ->
                    val fullTrack = trackRepository.getTrackWithPoints(track.id)
                    fullTrack?.let {
                        sb.append(KmlExporter.exportTrack(it))
                        if (selectedTracks.last() != track) {
                            sb.append("\n")
                        }
                    }
                }
            }
        }

        return sb.toString()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}