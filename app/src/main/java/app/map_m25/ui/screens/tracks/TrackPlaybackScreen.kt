package app.map_m25.ui.screens.tracks

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.map_m25.domain.model.MapLayer
import app.map_m25.domain.model.MapLocation
import app.map_m25.domain.model.MapStyle
import app.map_m25.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackPlaybackScreen(
    trackId: Long,
    onNavigateBack: () -> Unit,
    viewModel: TrackPlaybackViewModel = hiltViewModel()
) {
    val playbackState by viewModel.playbackState.collectAsState()

    LaunchedEffect(trackId) {
        viewModel.loadTrack(trackId)
    }

    val track = playbackState.track
    val points = playbackState.points

    val bgColor = Color(0xFFF5F5F5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(track?.name ?: "轨迹回放") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                if (points.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂无轨迹数据",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(bgColor)

                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val scale = 100f

                        val path = Path()
                        points.forEachIndexed { index, point ->
                            val x = centerX + ((point.longitude - points[0].longitude) * scale).toFloat()
                            val y = centerY + ((point.latitude - points[0].latitude) * scale).toFloat()
                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = Color.Gray.copy(alpha = 0.5f),
                            style = Stroke(width = 4f)
                        )

                        val currentIndex = playbackState.currentPointIndex
                        for (i in 0 until currentIndex.coerceAtMost(points.size - 1)) {
                            val point = points[i]
                            val x = centerX + ((point.longitude - points[0].longitude) * scale).toFloat()
                            val y = centerY + ((point.latitude - points[0].latitude) * scale).toFloat()
                            drawCircle(
                                color = Color.Blue.copy(alpha = 0.5f),
                                radius = 6f,
                                center = Offset(x, y)
                            )
                        }

                        if (currentIndex in points.indices) {
                            val currentPoint = points[currentIndex]
                            val x = centerX + ((currentPoint.longitude - points[0].longitude) * scale).toFloat()
                            val y = centerY + ((currentPoint.latitude - points[0].latitude) * scale).toFloat()
                            drawCircle(
                                color = Color.Red,
                                radius = 12f,
                                center = Offset(x, y)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 6f,
                                center = Offset(x, y)
                            )
                        }

                        val startPoint = points.firstOrNull()
                        startPoint?.let {
                            val x = centerX
                            val y = centerY
                            drawCircle(
                                color = Color.Green,
                                radius = 10f,
                                center = Offset(x, y)
                            )
                        }

                        val endPoint = points.lastOrNull()
                        if (endPoint != null && points.size > 1) {
                            val x = centerX + ((endPoint.longitude - points[0].longitude) * scale).toFloat()
                            val y = centerY + ((endPoint.latitude - points[0].latitude) * scale).toFloat()
                            drawCircle(
                                color = Color.Red.copy(alpha = 0.7f),
                                radius = 10f,
                                center = Offset(x, y)
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "进度",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${FormatUtils.formatDuration(playbackState.elapsedTime)} / ${FormatUtils.formatDuration(playbackState.totalDuration)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = playbackState.progress,
                        onValueChange = { viewModel.seekTo(it) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.seekTo((playbackState.progress - 0.1f).coerceAtLeast(0f)) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastRewind,
                                contentDescription = "后退"
                            )
                        }

                        FloatingActionButton(
                            onClick = {
                                if (playbackState.isPlaying) {
                                    viewModel.pause()
                                } else {
                                    viewModel.play()
                                }
                            },
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playbackState.isPlaying) "暂停" else "播放"
                            )
                        }

                        IconButton(onClick = { viewModel.stop() }) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "停止"
                            )
                        }

                        IconButton(
                            onClick = { viewModel.seekTo((playbackState.progress + 0.1f).coerceAtMost(1f)) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastForward,
                                contentDescription = "前进"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "播放速度",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(0.5f, 1f, 1.5f, 2f).forEach { speed ->
                            SpeedButton(
                                speed = speed,
                                isSelected = playbackState.playbackSpeed == speed,
                                onClick = { viewModel.setSpeed(speed) }
                            )
                        }
                    }

                    if (playbackState.currentPoint != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        val cp = playbackState.currentPoint!!
                        Text(
                            text = "当前位置: ${String.format("%.6f", cp.latitude)}, ${String.format("%.6f", cp.longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeedButton(
    speed: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Text(
                text = "${speed}x",
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Color.White
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}