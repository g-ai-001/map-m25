package app.map_m25.ui.screens.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.map_m25.domain.model.MapLocation
import app.map_m25.ui.theme.MapGreen
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MapScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToRoute: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var mapOffsetX by remember { mutableFloatStateOf(0f) }
    var mapOffsetY by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = rotation
                    // Enable high quality rendering for smooth animations
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
                        // Transform tap coordinates from rotated space to original space
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val angleRad = Math.toRadians(rotation.toDouble())
                        val cosAngle = kotlin.math.cos(angleRad).toFloat()
                        val sinAngle = kotlin.math.sin(angleRad).toFloat()

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

            for (x in 0..(size.width / gridSize).toInt() + 1) {
                for (y in 0..(size.height / gridSize).toInt() + 1) {
                    val gridX = x * gridSize - (mapOffsetX % gridSize)
                    val gridY = y * gridSize - (mapOffsetY % gridSize)
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        radius = 2f,
                        center = Offset(gridX, gridY)
                    )
                }
            }

            val roadColor = Color.Gray.copy(alpha = 0.6f)
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
                onSettingsClick = onNavigateToSettings
            )

            Spacer(modifier = Modifier.weight(1f))

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
                isLocating = uiState.isLocating
            )
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
                    onAddFavorite = { viewModel.addToFavorites(location) }
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
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
        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "设置",
                tint = Color.White
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
        // Rotation indicator
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
    isLocating: Boolean
) {
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
            label = "收藏",
            onClick = onFavoritesClick
        )
        ActionButton(
            icon = Icons.Default.Route,
            label = "路线",
            onClick = onRouteClick
        )
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
private fun LocationInfoCard(
    location: MapLocation,
    onClose: () -> Unit,
    onAddFavorite: () -> Unit
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
