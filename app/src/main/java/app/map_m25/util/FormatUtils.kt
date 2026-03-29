package app.map_m25.util

object FormatUtils {
    fun formatDistanceKm(km: Float): String {
        return if (km >= 1) {
            String.format("%.2f km", km)
        } else {
            String.format("%.0f m", km * 1000)
        }
    }

    fun formatDistanceChinese(km: Float): String {
        return if (km >= 1) {
            String.format("%.2f 公里", km)
        } else {
            String.format("%.0f 米", km * 1000)
        }
    }

    fun formatDistanceMetric(km: Float): String {
        return if (km >= 1) {
            String.format("%.1f公里", km)
        } else {
            String.format("%.0f米", km * 1000)
        }
    }

    fun formatDuration(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes % 60, seconds % 60)
            minutes > 0 -> String.format("%d:%02d", minutes, seconds % 60)
            else -> String.format("0:%02d", seconds)
        }
    }
}