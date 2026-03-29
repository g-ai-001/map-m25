package app.map_m25.data.export

import app.map_m25.domain.model.MapMarker
import app.map_m25.domain.model.Track
import app.map_m25.domain.model.TrackPoint
import java.text.SimpleDateFormat
import java.util.Locale

data class ImportedMarker(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String = ""
)

data class ImportedTrack(
    val name: String,
    val points: List<ImportedTrackPoint>,
    val description: String = ""
)

data class ImportedTrackPoint(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double? = null,
    val timestamp: Long? = null
)

sealed class ImportResult {
    data class Success(val markers: List<ImportedMarker>, val tracks: List<ImportedTrack>) : ImportResult()
    data class Error(val message: String) : ImportResult()
}

object GpxImporter {
    private val dateFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    )

    fun importData(xmlContent: String): ImportResult {
        return try {
            val markers = mutableListOf<ImportedMarker>()
            val tracks = mutableListOf<ImportedTrack>()

            val wptRegex = """<wpt\s+lat="([^"]+)"\s+lon="([^"]+)"[^>]*>(.*?)</wpt>""".toRegex(RegexOption.DOT_MATCHES_ALL)
            wptRegex.findAll(xmlContent).forEach { match ->
                val lat = match.groupValues[1].toDoubleOrNull() ?: return@forEach
                val lon = match.groupValues[2].toDoubleOrNull() ?: return@forEach
                val content = match.groupValues[3]

                val name = extractTag(content, "name") ?: "未命名标记"
                val desc = extractTag(content, "desc") ?: ""

                markers.add(ImportedMarker(name, lat, lon, desc))
            }

            var trackName = "未命名轨迹"
            val trackPoints = mutableListOf<ImportedTrackPoint>()

            val trkRegex = """<trk>(.*?)</trk>""".toRegex(RegexOption.DOT_MATCHES_ALL)
            trkRegex.findAll(xmlContent).forEach { match ->
                val trkContent = match.groupValues[1]
                trackName = extractTag(trkContent, "name") ?: trackName

                val trkptRegex = """<trkpt\s+lat="([^"]+)"\s+lon="([^"]+)"[^>]*>(.*?)</trkpt>""".toRegex(RegexOption.DOT_MATCHES_ALL)
                trkptRegex.findAll(trkContent).forEach { ptMatch ->
                    val lat = ptMatch.groupValues[1].toDoubleOrNull() ?: return@forEach
                    val lon = ptMatch.groupValues[2].toDoubleOrNull() ?: return@forEach
                    val ptContent = ptMatch.groupValues[3]

                    val eleStr = extractTag(ptContent, "ele")
                    val ele = eleStr?.toDoubleOrNull()

                    val timeStr = extractTag(ptContent, "time")
                    val time = timeStr?.let { parseTimestamp(it) }

                    trackPoints.add(ImportedTrackPoint(lat, lon, ele, time))
                }

                if (trackPoints.isNotEmpty()) {
                    tracks.add(ImportedTrack(trackName, trackPoints.toList()))
                    trackPoints.clear()
                }
            }

            ImportResult.Success(markers, tracks)
        } catch (e: Exception) {
            ImportResult.Error("GPX解析失败: ${e.message}")
        }
    }

    private fun extractTag(content: String, tag: String): String? {
        val regex = """<$tag>([^<]*)</$tag>""".toRegex()
        return regex.find(content)?.groupValues?.get(1)?.trim()
    }

    private fun parseTimestamp(str: String): Long? {
        for (format in dateFormats) {
            try {
                return format.parse(str)?.time
            } catch (_: Exception) {}
        }
        return null
    }
}

object KmlImporter {
    fun importData(xmlContent: String): ImportResult {
        return try {
            val markers = mutableListOf<ImportedMarker>()
            val tracks = mutableListOf<ImportedTrack>()

            val placemarkRegex = """<Placemark[^>]*>(.*?)</Placemark>""".toRegex(RegexOption.DOT_MATCHES_ALL)
            placemarkRegex.findAll(xmlContent).forEach { match ->
                val content = match.groupValues[1]

                val name = extractTag(content, "name") ?: "未命名"
                val desc = extractTag(content, "description") ?: ""

                val pointRegex = """<Point[^>]*>(.*?)</Point>""".toRegex(RegexOption.DOT_MATCHES_ALL)
                val pointMatch = pointRegex.find(content)
                if (pointMatch != null) {
                    val pointContent = pointMatch.groupValues[1]
                    val coordsRegex = """<coordinates>\s*([^<]+)\s*</coordinates>""".toRegex()
                    val coordsMatch = coordsRegex.find(pointContent)
                    if (coordsMatch != null) {
                        val coords = coordsMatch.groupValues[1].trim().split("[,\\s]+".toRegex())
                        if (coords.size >= 2) {
                            val lon = coords[0].toDoubleOrNull() ?: return@forEach
                            val lat = coords[1].toDoubleOrNull() ?: return@forEach
                            markers.add(ImportedMarker(name, lat, lon, desc))
                        }
                    }
                }

                val lineStringRegex = """<LineString[^>]*>(.*?)</LineString>""".toRegex(RegexOption.DOT_MATCHES_ALL)
                val lineStringMatch = lineStringRegex.find(content)
                if (lineStringMatch != null) {
                    val lineContent = lineStringMatch.groupValues[1]
                    val coordsRegex = """<coordinates>\s*([^<]+)\s*</coordinates>""".toRegex()
                    val coordsMatch = coordsRegex.find(lineContent)
                    if (coordsMatch != null) {
                        val coordsStr = coordsMatch.groupValues[1].trim()
                        val points = mutableListOf<ImportedTrackPoint>()
                        coordsStr.split("\\s+".toRegex()).forEach { coordStr ->
                            val parts = coordStr.split(",")
                            if (parts.size >= 2) {
                                val lon = parts[0].toDoubleOrNull() ?: return@forEach
                                val lat = parts[1].toDoubleOrNull() ?: return@forEach
                                val ele = parts.getOrNull(2)?.toDoubleOrNull()
                                points.add(ImportedTrackPoint(lat, lon, ele))
                            }
                        }
                        if (points.isNotEmpty()) {
                            tracks.add(ImportedTrack(name, points, desc))
                        }
                    }
                }
            }

            ImportResult.Success(markers, tracks)
        } catch (e: Exception) {
            ImportResult.Error("KML解析失败: ${e.message}")
        }
    }

    private fun extractTag(content: String, tag: String): String? {
        val regex = """<$tag>([^<]*)</$tag>""".toRegex()
        return regex.find(content)?.groupValues?.get(1)?.trim()
    }
}

object ImportExportManager {
    fun importFromContent(content: String, fileName: String): ImportResult {
        val lowerName = fileName.lowercase()
        return when {
            lowerName.endsWith(".gpx") -> GpxImporter.importData(content)
            lowerName.endsWith(".kml") -> KmlImporter.importData(content)
            content.contains("<gpx", ignoreCase = true) -> GpxImporter.importData(content)
            content.contains("<kml", ignoreCase = true) -> KmlImporter.importData(content)
            else -> ImportResult.Error("不支持的文件格式")
        }
    }

    fun convertToMarkers(imported: ImportedMarker, color: Int = 0xFFFF5722.toInt()): MapMarker {
        return MapMarker(
            name = imported.name,
            latitude = imported.latitude,
            longitude = imported.longitude,
            color = color,
            description = imported.description
        )
    }

    fun convertToTrackPoints(imported: ImportedTrack): List<TrackPoint> {
        return imported.points.mapIndexed { index, pt ->
            TrackPoint(
                latitude = pt.latitude,
                longitude = pt.longitude,
                timestamp = pt.timestamp ?: System.currentTimeMillis(),
                sequence = index
            )
        }
    }
}