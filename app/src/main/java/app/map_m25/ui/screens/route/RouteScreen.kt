package app.map_m25.ui.screens.route

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.automirrored.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.map_m25.domain.model.MapLocation
import app.map_m25.domain.model.RouteType
import app.map_m25.ui.theme.MapGreen
import app.map_m25.ui.theme.RouteBlue
import app.map_m25.ui.theme.RouteOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteScreen(
    onNavigateBack: () -> Unit,
    viewModel: RouteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("路线规划") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    if (uiState.route != null) {
                        IconButton(onClick = { viewModel.clearRoute() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "清除",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LocationSelector(
                startLocation = uiState.startLocation,
                endLocation = uiState.endLocation,
                onSwap = { viewModel.swapLocations() },
                onStartClick = { showStartPicker = true },
                onEndClick = { showEndPicker = true }
            )

            RouteTypeSelector(
                selectedType = uiState.routeType,
                onTypeSelected = viewModel::setRouteType
            )

            uiState.route?.let { route ->
                RouteInfoCard(route = route)
            }

            if (uiState.route != null && !uiState.isNavigating) {
                Button(
                    onClick = { viewModel.startNavigation() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("开始导航")
                }
            }

            if (showStartPicker) {
                LocationPickerDialog(
                    title = "选择起点",
                    locations = uiState.allLocations,
                    onSelect = {
                        viewModel.setStartLocation(it)
                        showStartPicker = false
                    },
                    onDismiss = { showStartPicker = false }
                )
            }

            if (showEndPicker) {
                LocationPickerDialog(
                    title = "选择终点",
                    locations = uiState.allLocations,
                    onSelect = {
                        viewModel.setEndLocation(it)
                        showEndPicker = false
                    },
                    onDismiss = { showEndPicker = false }
                )
            }
        }
    }
}

@Composable
private fun LocationSelector(
    startLocation: MapLocation?,
    endLocation: MapLocation?,
    onSwap: () -> Unit,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            LocationInput(
                label = "起点",
                location = startLocation,
                icon = Icons.Default.MyLocation,
                iconColor = RouteBlue,
                onClick = onStartClick
            )
            Spacer(modifier = Modifier.height(8.dp))
            LocationInput(
                label = "终点",
                location = endLocation,
                icon = Icons.Default.LocationOn,
                iconColor = RouteOrange,
                onClick = onEndClick
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSwap,
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.SwapVert,
                contentDescription = "交换"
            )
        }
    }
}

@Composable
private fun LocationInput(
    label: String,
    location: MapLocation?,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = location?.name ?: "点击选择",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RouteTypeSelector(
    selectedType: RouteType,
    onTypeSelected: (RouteType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RouteTypeChip(
            type = RouteType.DRIVING,
            icon = Icons.Filled.Directions,
            label = "驾车",
            isSelected = selectedType == RouteType.DRIVING,
            onClick = { onTypeSelected(RouteType.DRIVING) },
            modifier = Modifier.weight(1f)
        )
        RouteTypeChip(
            type = RouteType.WALKING,
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
            label = "步行",
            isSelected = selectedType == RouteType.WALKING,
            onClick = { onTypeSelected(RouteType.WALKING) },
            modifier = Modifier.weight(1f)
        )
        RouteTypeChip(
            type = RouteType.CYCLING,
            icon = Icons.Default.DirectionsBike,
            label = "骑行",
            isSelected = selectedType == RouteType.CYCLING,
            onClick = { onTypeSelected(RouteType.CYCLING) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RouteTypeChip(
    type: RouteType,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null)
        },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = Color.White,
            selectedLeadingIconColor = Color.White
        )
    )
}

@Composable
private fun RouteInfoCard(route: app.map_m25.domain.model.Route) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "距离",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = String.format("%.1f 公里", route.distance),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "预计时间",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "${route.duration} 分钟",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MapGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = route.startLocation.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = RouteOrange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = route.endLocation.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun LocationPickerDialog(
    title: String,
    locations: List<MapLocation>,
    onSelect: (MapLocation) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.Center)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Divider()
                if (locations.isEmpty()) {
                    Text(
                        text = "暂无收藏地点",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.height(300.dp)
                    ) {
                        items(locations) { location ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(location) }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = location.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
