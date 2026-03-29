package app.map_m25.ui.screens.map

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.map_m25.domain.model.MapLayer
import app.map_m25.domain.model.MapLocation
import app.map_m25.domain.model.MapMarker
import app.map_m25.ui.theme.MapGreen
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MapScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToRoute: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToMarkers: () -> Unit,
    onNavigateToTracks: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var mapOffsetX by remember { mutableFloatStateOf(0f) }
    var mapOffsetY by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var showLayerMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = rotation
                    compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, rotationChange ->
                        scale = (scale * zoom).coerceIn(0.5f, 3f)
                        mapOffsetX += pan.x
                        mapOffsetY += pan.y
                        rotation += rotationChange
                        viewModel.onRotationChange(rotation)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val angleRad = Math.toRadians(rotation.toDouble())
                        val cosAngle = cos(angleRad).toFloat()
                        val sinAngle = sin(angleRad).toFloat()

                        val rotatedX = cosAngle * (offset.x - centerX) - sinAngle * (offset.y - centerY) + centerX
                        val rotatedY = sinAngle * (offset.x - centerX) + cosAngle * (offset.y - centerY) + centerY

                        val lat = uiState.currentLocation.latitude +
                            (rotatedY - size.height / 2) / (100 * scale)
                        val lng = uiState.currentLocation.longitude +
                            (rotatedX - size.width / 2) / (100 * scale)
                        viewModel.onMapClick(lat, lng)
                    }
                }
        ) {
            val centerX = size.width / 2 + mapOffsetX
            val centerY = size.height / 2 + mapOffsetY
            val gridSize = 50f * scale

            val bgColor = when (uiState.mapLayer) {
                MapLayer.NORMAL -> Color(0xFFF5F5F5)
                MapLayer.SATELLITE -> Color(0xFF2D4A2D)
                MapLayer.TERRAIN -> Color(0xFFE8DCC8)
            }
            drawRect(bgColor)

            for (x in 0..(size.width / gridSize).toInt() + 1) {
                for (y in 0..(size.height / gridSize).toInt() + 1) {
                    val gridX = x * gridSize - (mapOffsetX % gridSize)
                    val gridY = y * gridSize - (mapOffsetY % gridSize)
                    val dotColor = when (uiState.mapLayer) {
                        MapLayer.NORMAL -> Color.LightGray.copy(alpha = 0.3f)
                        MapLayer.SATELLITE -> Color.Green.copy(alpha = 0.2f)
                        MapLayer.TERRAIN -> Color(0xFF8B7355).copy(alpha = 0.3f)
                    }
                    drawCircle(
                        color = dotColor,
                        radius = 2f,
                        center = Offset(gridX, gridY)
                    )
                }
            }

            val roadColor = when (uiState.mapLayer) {
                MapLayer.NORMAL -> Color.Gray.copy(alpha = 0.6f)
                MapLayer.SATELLITE -> Color(0xFF4A5D4A).copy(alpha = 0.8f)
                MapLayer.TERRAIN -> Color(0xFF6B5B4F).copy(alpha = 0.6f)
            }
            val roadWidth = 4f * scale

            val horizontalRoads = listOf(0.3f, 0.5f, 0.7f)
            horizontalRoads.forEach { yRatio ->
                val y = yRatio * size.height
                drawLine(
                    color = roadColor,
                    start = Offset(0f, y + mapOffsetY % (size.height * 0.3f)),
                    end = Offset(size.width, y + mapOffsetY % (size.height * 0.3f)),
                    strokeWidth = roadWidth
                )
            }

            val verticalRoads = listOf(0.25f, 0.45f, 0.65f, 0.85f)
            verticalRoads.forEach { xRatio ->
                val x = xRatio * size.width
                drawLine(
                    color = roadColor,
                    start = Offset(x + mapOffsetX % (size.width * 0.3f), 0f),
                    end = Offset(x + mapOffsetX % (size.width * 0.3f), size.height),
                    strokeWidth = roadWidth
                )
            }

            if (uiState.isMeasuring && uiState.measurePoints.isNotEmpty()) {
                val measureColor = Color.Red.copy(alpha = 0.8f)
                val measureStrokeWidth = 3f * scale

                for (i in 0 until uiState.measurePoints.size - 1) {
                    val p1 = uiState.measurePoints[i]
                    val p2 = uiState.measurePoints[i + 1]
                    val x1 = centerX + (p1.longitude - uiState.currentLocation.longitude) * 100 * scale
                    val y1 = centerY + (p1.latitude - uiState.currentLocation.latitude) * 100 * scale
                    val x2 = centerX + (p2.longitude - uiState.currentLocation.longitude) * 100 * scale
                    val y2 = centerY + (p2.latitude - uiState.currentLocation.latitude) * 100 * scale
                    drawLine(measureColor, Offset(x1.toFloat(), y1.toFloat()), Offset(x2.toFloat(), y2.toFloat()), measureStrokeWidth)
                }

                uiState.measurePoints.forEach { point ->
                    val x = centerX + (point.longitude - uiState.currentLocation.longitude) * 100 * scale
                    val y = centerY + (point.latitude - uiState.currentLocation.latitude) * 100 * scale
                    drawCircle(Color.Red, 8f * scale, Offset(x.toFloat(), y.toFloat()))
                }
            }

            uiState.markers.forEach { marker ->
                val markerX = centerX + (marker.longitude - uiState.currentLocation.longitude) * 100 * scale
                val markerY = centerY + (marker.latitude - uiState.currentLocation.latitude) * 100 * scale
                drawCircle(
                    color = Color(marker.color),
                    radius = 12f * scale,
                    center = Offset(markerX.toFloat(), markerY.toFloat())
                )
                drawCircle(
                    color = Color.White,
                    radius = 6f * scale,
                    center = Offset(markerX.toFloat(), markerY.toFloat())
                )
            }

            val markerX = centerX
            val markerY = centerY

            drawCircle(
                color = Color.Blue.copy(alpha = 0.3f),
                radius = 30f * scale,
                center = Offset(markerX, markerY)
            )
            drawCircle(
                color = Color.Blue,
                radius = 15f * scale,
                center = Offset(markerX, markerY)
            )
            drawCircle(
                color = Color.White,
                radius = 8f * scale,
                center = Offset(markerX, markerY)
            )

            val pointerPath = Path().apply {
                moveTo(markerX, markerY - 40f * scale)
                lineTo(markerX - 15f * scale, markerY - 20f * scale)
                lineTo(markerX + 15f * scale, markerY - 20f * scale)
                close()
            }
            drawPath(
                path = pointerPath,
                color = Color.Blue,
                style = Stroke(width = 2f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar(
                onSearchClick = onNavigateToSearch,
                onSettingsClick = onNavigateToSettings,
                onLayersClick = { showLayerMenu = true },
                layerMenuExpanded = showLayerMenu,
                onLayerMenuDismiss = { showLayerMenu = false },
                currentLayer = uiState.mapLayer,
                onLayerSelected = {
                    viewModel.setMapLayer(it)
                    showLayerMenu = false
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.showCompass && rotation != 0f) {
                CompassOverlay(
                    rotation = rotation,
                    onClick = { viewModel.resetRotation() }
                )
            }

            if (uiState.showScaleBar) {
                ScaleBarOverlay(
                    scale = scale,
                    zoom = uiState.zoom
                )
            }

            ZoomControls(
                onZoomIn = { viewModel.onZoomChange((uiState.zoom + 1).coerceAtMost(20f)) },
                onZoomOut = { viewModel.onZoomChange((uiState.zoom - 1).coerceAtLeast(5f)) },
                rotation = rotation,
                onResetRotation = { viewModel.resetRotation() }
            )

            ActionButtons(
                onLocateClick = { viewModel.startLocation() },
                onFavoritesClick = onNavigateToFavorites,
                onRouteClick = onNavigateToRoute,
                onMarkerClick = onNavigateToMarkers,
                onTrackClick = onNavigateToTracks,
                isLocating = uiState.isLocating,
                onMeasureClick = {
                    if (uiState.isMeasuring) {
                        viewModel.stopMeasuring()
                    } else {
                        viewModel.startMeasuring()
                    }
                },
                isMeasuring = uiState.isMeasuring,
                onAddMarkerClick = {
                    if (uiState.isAddingMarker) {
                        viewModel.cancelAddingMarker()
                    } else {
                        viewModel.startAddingMarker()
                    }
                },
                isAddingMarker = uiState.isAddingMarker
            )

            if (uiState.isMeasuring) {
                MeasuringToolbar(
                    totalDistance = uiState.totalDistance,
                    onClear = { viewModel.clearMeasurePoints() },
                    onStop = { viewModel.stopMeasuring() }
                )
            }
        }

        AnimatedVisibility(
            visible = uiState.showLocationInfo && uiState.selectedLocation != null,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            uiState.selectedLocation?.let { location ->
                LocationInfoCard(
                    location = location,
                    onClose = { viewModel.hideLocationInfo() },
                    onAddFavorite = { viewModel.addToFavorites(location) },
                    onShare = {
                        val shareText = "位置: ${location.name}\n地址: ${location.address}\n经度: ${location.longitude}\n纬度: ${location.latitude}"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "分享位置")
                        context.startActivity(shareIntent)
                    }
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLayersClick: () -> Unit,
    layerMenuExpanded: Boolean,
    onLayerMenuDismiss: () -> Unit,
    currentLayer: MapLayer,
    onLayerSelected: (MapLayer) -> Unit
) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "地图",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索",
                    tint = Color.White
                )
            }
            IconButton(onClick = onLayersClick) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "图层",
                    tint = Color.White
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "设置",
                    tint = Color.White
                )
            }
        }

        DropdownMenu(
            expanded = layerMenuExpanded,
            onDismissRequest = onLayerMenuDismiss
        ) {
            MapLayer.entries.forEach { layer ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = when (layer) {
                                MapLayer.NORMAL -> "普通地图"
                                MapLayer.SATELLITE -> "卫星地图"
                                MapLayer.TERRAIN -> "地形地图"
                            }
                        )
                    },
                    onClick = { onLayerSelected(layer) },
                    leadingIcon = if (layer == currentLayer) {
                        { Icon(Icons.Default.Star, contentDescription = null) }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun CompassOverlay(
    rotation: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, top = 60.dp)
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Navigation,
            contentDescription = "指南针",
            tint = Color.Red,
            modifier = Modifier.graphicsLayer { rotationZ = -rotation }
        )
    }
}

@Composable
private fun ScaleBarOverlay(
    scale: Float,
    zoom: Float
) {
    val metersPerPixel = 156543.33 * cos(39.9042 * Math.PI / 180.0) / (1 shl zoom.toInt())
    val scaleBarWidth = 100.dp
    val realWidth = (scaleBarWidth.value * metersPerPixel / scale).toInt()
    val displayText = when {
        realWidth >= 1000 -> "${realWidth / 1000}公里"
        realWidth >= 100 -> "${realWidth / 100}百米"
        else -> "${realWidth}米"
    }

    Box(
        modifier = Modifier
            .padding(start = 16.dp, top = 120.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = displayText,
                style = MaterialTheme.typography.labelSmall
            )
            Box(
                modifier = Modifier
                    .width(scaleBarWidth)
                    .height(4.dp)
                    .background(Color.Black)
            )
        }
    }
}

@Composable
private fun ZoomControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    rotation: Float = 0f,
    onResetRotation: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton(
            onClick = onZoomIn,
            modifier = Modifier.size(40.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Icon(Icons.Default.Add, contentDescription = "放大")
        }
        Spacer(modifier = Modifier.height(8.dp))
        FloatingActionButton(
            onClick = onZoomOut,
            modifier = Modifier.size(40.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Icon(Icons.Default.Remove, contentDescription = "缩小")
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (rotation != 0f) {
            FloatingActionButton(
                onClick = onResetRotation,
                modifier = Modifier.size(40.dp),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "重置旋转",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.graphicsLayer { rotationZ = -rotation }
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    onLocateClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onRouteClick: () -> Unit,
    onMarkerClick: () -> Unit,
    onTrackClick: () -> Unit,
    isLocating: Boolean,
    onMeasureClick: () -> Unit,
    isMeasuring: Boolean,
    onAddMarkerClick: () -> Unit,
    isAddingMarker: Boolean
) {
    Column {
        if (isAddingMarker) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "点击地图添加标记",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    IconButton(onClick = onAddMarkerClick) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "取消",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = Icons.Default.MyLocation,
                label = "定位",
                onClick = onLocateClick,
                isActive = isLocating
            )
            ActionButton(
                icon = Icons.Default.Star,
                label = "标记",
                onClick = onMarkerClick
            )
            ActionButton(
                icon = Icons.Default.Route,
                label = "轨迹",
                onClick = onTrackClick
            )
            ActionButton(
                icon = Icons.Filled.Straighten,
                label = "测量",
                onClick = onMeasureClick,
                isActive = isMeasuring
            )
            ActionButton(
                icon = if (isAddingMarker) Icons.Default.Clear else Icons.Default.Add,
                label = "添标记",
                onClick = onAddMarkerClick,
                isActive = isAddingMarker
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun MeasuringToolbar(
    totalDistance: Float,
    onClear: () -> Unit,
    onStop: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "距离测量",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (totalDistance >= 1) String.format("%.2f 公里", totalDistance)
                           else String.format("%.0f 米", totalDistance * 1000),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "清除"
                    )
                }
                IconButton(onClick = onStop) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "完成"
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationInfoCard(
    location: MapLocation,
    onClose: () -> Unit,
    onAddFavorite: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "分享",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onAddFavorite) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "收藏",
                            tint = MapGreen
                        )
                    }
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "关闭"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = location.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "经度: ${String.format("%.6f", location.longitude)} " +
                    "纬度: ${String.format("%.6f", location.latitude)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}