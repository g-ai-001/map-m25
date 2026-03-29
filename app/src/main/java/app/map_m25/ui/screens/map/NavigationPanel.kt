package app.map_m25.ui.screens.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.map_m25.domain.model.RouteType
import app.map_m25.ui.theme.MapGreen
import app.map_m25.ui.theme.RouteOrange
import app.map_m25.ui.theme.WarningRed
import app.map_m25.ui.theme.WarningRedDark

@Composable
fun NavigationPanel(
    navigationState: NavigationState,
    onClose: () -> Unit,
    onSimulationToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = navigationState.isNavigating,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (navigationState.isDeviated) {
                    WarningRedDark
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (navigationState.isDeviated) {
                                Icons.Default.Warning
                            } else {
                                Icons.Default.Directions
                            },
                            contentDescription = null,
                            tint = if (navigationState.isDeviated) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (navigationState.isDeviated) "偏航提醒" else "导航中",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (navigationState.isDeviated) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            if (navigationState.isSimulation) {
                                Text(
                                    text = "模拟导航",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (navigationState.isDeviated) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Row {
                        IconButton(
                            onClick = onSimulationToggle,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (navigationState.isSimulation) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                        ) {
                            Icon(
                                imageVector = if (navigationState.isSimulation) Icons.Default.Close else Icons.Default.PlayArrow,
                                contentDescription = if (navigationState.isSimulation) "停止模拟" else "开始模拟",
                                tint = if (navigationState.isSimulation) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "退出导航",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Turn instruction
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (navigationState.isDeviated) {
                                Color.White.copy(alpha = 0.2f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TurnDirectionIcon(
                        turnDirection = navigationState.nextTurnDirection,
                        isDeviated = navigationState.isDeviated
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = navigationState.currentInstruction,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (navigationState.isDeviated) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = navigationState.nextRoadName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (navigationState.isDeviated) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NavigationStatItem(
                        icon = Icons.Default.Speed,
                        value = navigationState.remainingDistance,
                        label = "剩余距离",
                        isDeviated = navigationState.isDeviated
                    )
                    NavigationStatItem(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        value = navigationState.remainingTime,
                        label = "剩余时间",
                        isDeviated = navigationState.isDeviated
                    )
                    NavigationStatItem(
                        icon = Icons.Default.Directions,
                        value = "${navigationState.routeType.displayName}",
                        label = "路线类型",
                        isDeviated = navigationState.isDeviated
                    )
                }

                // Deviation alert
                if (navigationState.isDeviated) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "您已偏离路线，正在重新规划",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TurnDirectionIcon(
    turnDirection: TurnDirection,
    isDeviated: Boolean
) {
    val icon = when (turnDirection) {
        TurnDirection.STRAIGHT -> Icons.Default.Straight
        TurnDirection.LEFT -> Icons.AutoMirrored.Filled.KeyboardArrowLeft
        TurnDirection.RIGHT -> Icons.AutoMirrored.Filled.KeyboardArrowRight
        TurnDirection.SLIGHT_LEFT -> Icons.AutoMirrored.Filled.KeyboardArrowLeft
        TurnDirection.SLIGHT_RIGHT -> Icons.AutoMirrored.Filled.KeyboardArrowRight
        TurnDirection.U_TURN -> Icons.AutoMirrored.Filled.ArrowBack
    }

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(
                if (isDeviated) Color.White.copy(alpha = 0.3f)
                else MapGreen
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = turnDirection.displayName,
            tint = if (isDeviated) Color.White else Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun NavigationStatItem(
    icon: ImageVector,
    value: String,
    label: String,
    isDeviated: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDeviated) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isDeviated) Color.White else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isDeviated) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class NavigationState(
    val isNavigating: Boolean = false,
    val isSimulation: Boolean = false,
    val isDeviated: Boolean = false,
    val currentInstruction: String = "前方直行",
    val nextTurnDirection: TurnDirection = TurnDirection.STRAIGHT,
    val nextRoadName: String = "",
    val remainingDistance: String = "0公里",
    val remainingTime: String = "0分钟",
    val routeType: RouteType = RouteType.DRIVING,
    val currentSpeed: Float = 0f
)

enum class TurnDirection(val displayName: String) {
    STRAIGHT("直行"),
    LEFT("左转"),
    RIGHT("右转"),
    SLIGHT_LEFT("向左前方"),
    SLIGHT_RIGHT("向右前方"),
    U_TURN("掉头")
}

fun RouteType.toDisplayName(): String = when (this) {
    RouteType.DRIVING -> "驾车"
    RouteType.WALKING -> "步行"
    RouteType.CYCLING -> "骑行"
}

val RouteType.displayName: String
    get() = this.toDisplayName()