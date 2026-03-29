package app.map_m25.data.export

import app.map_m25.domain.model.MapMarker
import app.map_m25.domain.model.Track
import app.map_m25.domain.model.TrackPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GpxExporter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

    fun exportMarkers(markers: List<MapMarker>): String {
        val sb = StringBuilder()
        sb.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        sb.appendLine("""<gpx version="1.1" creator="Map-M25">""")
        sb.appendLine("  <metadata>")
        sb.appendLine("    <name>地图标记导出</name>")
        sb.appendLine("    <time>${dateFormat.format(Date())}</time>")
        sb.appendLine("  </metadata>")
        markers.forEach { marker ->
            sb.appendLine("  <wpt lat=\"${marker.latitude}\" lon=\"${marker.longitude}\">")
            sb.appendLine("    <name>${escapeXml(marker.name)}</name>")
            sb.appendLine("    <desc>Marker created at ${dateFormat.format(Date(marker.createdAt))}</desc>")
            sb.appendLine("  </wpt>")
        }
        sb.appendLine("</gpx>")
        return sb.toString()
    }

    fun exportTrack(track: Track): String {
        val sb = StringBuilder()
        sb.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        sb.appendLine("""<gpx version="1.1" creator="Map-M25">""")
        sb.appendLine("  <metadata>")
        sb.appendLine("    <name>${escapeXml(track.name)}</name>")
        sb.appendLine("    <time>${dateFormat.format(Date())}</time>")
        sb.appendLine("  </metadata>")
        sb.appendLine("  <trk>")
        sb.appendLine("    <name>${escapeXml(track.name)}</name>")
        sb.appendLine("    <trkseg>")
        track.points.forEach { point ->
            sb.appendLine("      <trkpt lat=\"${point.latitude}\" lon=\"${point.longitude}\">")
            sb.appendLine("        <time>${dateFormat.format(Date(point.timestamp))}</time>")
            sb.appendLine("      </trkpt>")
        }
        sb.appendLine("    </trkseg>")
        sb.appendLine("  </trk>")
        sb.appendLine("</gpx>")
        return sb.toString()
    }

    private fun escapeXml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}

object KmlExporter {
    fun exportMarkers(markers: List<MapMarker>): String {
        val sb = StringBuilder()
        sb.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        sb.appendLine("""<kml xmlns="http://www.opengis.net/kml/2.2">""")
        sb.appendLine("  <Document>")
        sb.appendLine("    <name>地图标记导出</name>")
        markers.forEach { marker ->
            sb.appendLine("    <Placemark>")
            sb.appendLine("      <name>${escapeKml(marker.name)}</name>")
            sb.appendLine("      <description>Created at ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(marker.createdAt))}</description>")
            sb.appendLine("      <Point>")
            sb.appendLine("        <coordinates>${marker.longitude},${marker.latitude},0</coordinates>")
            sb.appendLine("      </Point>")
            sb.appendLine("    </Placemark>")
        }
        sb.appendLine("  </Document>")
        sb.appendLine("</kml>")
        return sb.toString()
    }

    fun exportTrack(track: Track): String {
        val sb = StringBuilder()
        sb.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        sb.appendLine("""<kml xmlns="http://www.opengis.net/kml/2.2">""")
        sb.appendLine("  <Document>")
        sb.appendLine("    <name>${escapeKml(track.name)}</name>")
        sb.appendLine("    <Placemark>")
        sb.appendLine("      <name>${escapeKml(track.name)}</name>")
        sb.appendLine("      <LineString>")
        sb.appendLine("        <tessellate>1</tessellate>")
        sb.appendLine("        <coordinates>")
        track.points.forEach { point ->
            sb.appendLine("          ${point.longitude},${point.latitude},0")
        }
        sb.appendLine("        </coordinates>")
        sb.appendLine("      </LineString>")
        sb.appendLine("    </Placemark>")
        sb.appendLine("  </Document>")
        sb.appendLine("</kml>")
        return sb.toString()
    }

    private fun escapeKml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
    }
}