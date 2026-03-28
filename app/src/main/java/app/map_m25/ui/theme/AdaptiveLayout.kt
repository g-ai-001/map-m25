package app.map_m25.ui.theme

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

enum class DeviceType {
    PHONE,
    TABLET,
    FOLDABLE
}

data class AdaptiveLayoutInfo(
    val deviceType: DeviceType,
    val isLandscape: Boolean,
    val isWideScreen: Boolean
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberAdaptiveLayoutInfo(): AdaptiveLayoutInfo {
    val context = LocalContext.current
    val activity = context as? Activity

    return remember(activity) {
        val windowSizeClass = activity?.let { calculateWindowSizeClass(it) }
        val widthSizeClass = windowSizeClass?.widthSizeClass ?: WindowWidthSizeClass.Compact

        val deviceType = when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> DeviceType.PHONE
            WindowWidthSizeClass.Medium -> DeviceType.FOLDABLE
            WindowWidthSizeClass.Expanded -> DeviceType.TABLET
            else -> DeviceType.PHONE
        }

        val isLandscape = activity?.let {
            val display = it.windowManager.defaultDisplay
            val size = android.graphics.Point()
            display.getSize(size)
            size.x > size.y
        } ?: false

        AdaptiveLayoutInfo(
            deviceType = deviceType,
            isLandscape = isLandscape,
            isWideScreen = deviceType == DeviceType.TABLET || deviceType == DeviceType.FOLDABLE
        )
    }
}

object AdaptiveLayout {
    @Composable
    fun shouldShowTwoPane(): Boolean {
        val layoutInfo = rememberAdaptiveLayoutInfo()
        return layoutInfo.deviceType == DeviceType.TABLET ||
               (layoutInfo.deviceType == DeviceType.FOLDABLE && layoutInfo.isLandscape)
    }

    @Composable
    fun getContentPadding(): Int {
        val layoutInfo = rememberAdaptiveLayoutInfo()
        return when (layoutInfo.deviceType) {
            DeviceType.PHONE -> 0
            DeviceType.FOLDABLE -> if (layoutInfo.isLandscape) 16 else 8
            DeviceType.TABLET -> 24
        }
    }

    @Composable
    fun getGridColumns(): Int {
        val layoutInfo = rememberAdaptiveLayoutInfo()
        return when (layoutInfo.deviceType) {
            DeviceType.PHONE -> 1
            DeviceType.FOLDABLE -> 2
            DeviceType.TABLET -> if (layoutInfo.isLandscape) 3 else 2
        }
    }
}