package app.map_m25.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HotTub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.map_m25.BuildConfig
import app.map_m25.domain.model.MapLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToExport: () -> Unit,
    onNavigateToOfflineRegions: () -> Unit = {},
    onNavigateToHotSpots: () -> Unit = {},
    onNavigateToSnapshots: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var coordInput by remember { mutableStateOf("") }
    var coordResult by remember { mutableStateOf("") }
    var isDmsToDecimal by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                    }
                ,
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
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSection(title = "坐标转换") {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "经纬度格式转换",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.selectableGroup(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = isDmsToDecimal,
                                    onClick = { isDmsToDecimal = true },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = isDmsToDecimal, onClick = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("度分秒→十进制")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = !isDmsToDecimal,
                                    onClick = { isDmsToDecimal = false },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = !isDmsToDecimal, onClick = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("十进制→度分秒")
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = coordInput,
                        onValueChange = { coordInput = it },
                        label = { Text(if (isDmsToDecimal) "输入度分秒如: 116°24′35.2″" else "输入十进制如: 116.4098") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        TextButton(
                            onClick = {
                                coordResult = if (isDmsToDecimal) {
                                    convertDmsToDecimal(coordInput)
                                } else {
                                    convertDecimalToDms(coordInput)
                                }
                            }
                        ) {
                            Text("转换")
                        }
                        if (coordResult.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    coordInput = coordResult
                                    coordResult = ""
                                    isDmsToDecimal = !isDmsToDecimal
                                }
                            ) {
                                Text("交换")
                            }
                        }
                    }
                    if (coordResult.isNotEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "转换结果",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = coordResult,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(title = "数据管理") {
                ClickableSettingItem(
                    icon = Icons.Default.History,
                    title = "搜索历史",
                    description = "查看和管理搜索记录",
                    onClick = onNavigateToHistory
                )
                HorizontalDivider()
                ClickableSettingItem(
                    icon = Icons.Default.Share,
                    title = "数据导出",
                    description = "导出标记和轨迹数据",
                    onClick = onNavigateToExport
                )
                HorizontalDivider()
                ClickableSettingItem(
                    icon = Icons.Default.Download,
                    title = "离线区域",
                    description = "管理离线地图区域",
                    onClick = onNavigateToOfflineRegions
                )
                HorizontalDivider()
                ClickableSettingItem(
                    icon = Icons.Default.HotTub,
                    title = "热点推荐",
                    description = "查看热门地点推荐",
                    onClick = onNavigateToHotSpots
                )
                HorizontalDivider()
                ClickableSettingItem(
                    icon = Icons.Default.PhotoCamera,
                    title = "地图快照",
                    description = "查看已保存的地图截图",
                    onClick = onNavigateToSnapshots
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(title = "显示设置") {
                SwitchSettingItem(
                    icon = Icons.Default.DarkMode,
                    title = "深色模式",
                    description = "启用深色主题",
                    checked = uiState.darkMode,
                    onCheckedChange = viewModel::setDarkMode
                )
                HorizontalDivider()
                SwitchSettingItem(
                    icon = Icons.Default.Speed,
                    title = "高刷新率模式",
                    description = "启用120Hz流畅体验",
                    checked = uiState.highRefreshRate,
                    onCheckedChange = viewModel::setHighRefreshRate
                )
                HorizontalDivider()
                SliderSettingItem(
                    icon = Icons.Default.ZoomIn,
                    title = "默认缩放级别",
                    description = "地图默认缩放级别: ${uiState.mapZoom.toInt()}",
                    value = uiState.mapZoom,
                    valueRange = 5f..20f,
                    onValueChange = viewModel::setMapZoom
                )
                HorizontalDivider()
                MapLayerSettingItem(
                    icon = Icons.Default.Layers,
                    title = "地图图层",
                    currentLayer = uiState.mapLayer,
                    onLayerSelected = viewModel::setMapLayer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(title = "电池设置") {
                SwitchSettingItem(
                    icon = Icons.Default.ScreenLockPortrait,
                    title = "保持屏幕常亮",
                    description = "导航时防止屏幕关闭",
                    checked = uiState.keepScreenOn,
                    onCheckedChange = viewModel::setKeepScreenOn
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(title = "关于") {
                InfoSettingItem(
                    icon = Icons.Default.Info,
                    title = "版本信息",
                    value = BuildConfig.VERSION_NAME
                )
            }
        }
    }
}

private fun convertDmsToDecimal(input: String): String {
    try {
        val pattern = Regex("(\\d+)°(\\d+)′(\\d+\\.?\\d*)″")
        val match = pattern.find(input) ?: return "格式错误"
        val degrees = match.groupValues[1].toDouble()
        val minutes = match.groupValues[2].toDouble()
        val seconds = match.groupValues[3].toDouble()
        val decimal = degrees + minutes / 60 + seconds / 3600
        return String.format("%.6f", decimal)
    } catch (e: Exception) {
        return "格式错误"
    }
}

private fun convertDecimalToDms(input: String): String {
    try {
        val decimal = input.toDouble()
        val degrees = decimal.toInt()
        val minutesDouble = (decimal - degrees) * 60
        val minutes = minutesDouble.toInt()
        val seconds = (minutesDouble - minutes) * 60
        return String.format("%d°%d′%.2f″", degrees, minutes, seconds)
    } catch (e: Exception) {
        return "格式错误"
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SwitchSettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SliderSettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun ClickableSettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InfoSettingItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MapLayerSettingItem(
    icon: ImageVector,
    title: String,
    currentLayer: MapLayer,
    onLayerSelected: (MapLayer) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 40.dp, top = 8.dp)
                .selectableGroup()
        ) {
            MapLayer.entries.forEach { layer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = currentLayer == layer,
                            onClick = { onLayerSelected(layer) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentLayer == layer,
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (layer) {
                            MapLayer.NORMAL -> "普通地图"
                            MapLayer.SATELLITE -> "卫星地图"
                            MapLayer.TERRAIN -> "地形地图"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}