package app.map_m25.ui.screens.offline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.map_m25.domain.model.OfflineRegion
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineRegionsScreen(
    onNavigateBack: () -> Unit,
    viewModel: OfflineRegionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("离线区域管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(OfflineRegionsEvent.ShowAddDialog) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加区域")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.regions.isEmpty()) {
                Text(
                    text = "暂无离线区域\n点击右下角按钮添加",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.regions, key = { it.id }) { region ->
                        OfflineRegionCard(
                            region = region,
                            onEdit = { viewModel.onEvent(OfflineRegionsEvent.EditRegion(region)) },
                            onDelete = { viewModel.onEvent(OfflineRegionsEvent.DeleteRegion(region.id)) }
                        )
                    }
                }
            }
        }

        if (uiState.showAddDialog) {
            OfflineRegionDialog(
                region = uiState.editingRegion,
                onDismiss = { viewModel.onEvent(OfflineRegionsEvent.HideDialog) },
                onSave = { id, name, minLat, maxLat, minLng, maxLng, zoom ->
                    viewModel.onEvent(OfflineRegionsEvent.SaveRegion(id, name, minLat, maxLat, minLng, maxLng, zoom))
                }
            )
        }
    }
}

@Composable
private fun OfflineRegionCard(
    region: OfflineRegion,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth()
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
                    text = region.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "删除")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "经度: ${String.format("%.6f", region.minLongitude)} ~ ${String.format("%.6f", region.maxLongitude)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "纬度: ${String.format("%.6f", region.minLatitude)} ~ ${String.format("%.6f", region.maxLatitude)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "缩放级别: ${region.zoomLevel}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "更新时间: ${dateFormat.format(Date(region.updatedAt))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OfflineRegionDialog(
    region: OfflineRegion?,
    onDismiss: () -> Unit,
    onSave: (Long, String, Double, Double, Double, Double, Int) -> Unit
) {
    var name by remember { mutableStateOf(region?.name ?: "") }
    var minLat by remember { mutableStateOf(region?.minLatitude?.toString() ?: "39.8") }
    var maxLat by remember { mutableStateOf(region?.maxLatitude?.toString() ?: "40.0") }
    var minLng by remember { mutableStateOf(region?.minLongitude?.toString() ?: "116.2") }
    var maxLng by remember { mutableStateOf(region?.maxLongitude?.toString() ?: "116.6") }
    var zoom by remember { mutableStateOf(region?.zoomLevel?.toString() ?: "15") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (region == null) "添加离线区域" else "编辑离线区域") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("区域名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minLat,
                        onValueChange = { minLat = it },
                        label = { Text("最小纬度") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxLat,
                        onValueChange = { maxLat = it },
                        label = { Text("最大纬度") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minLng,
                        onValueChange = { minLng = it },
                        label = { Text("最小经度") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxLng,
                        onValueChange = { maxLng = it },
                        label = { Text("最大经度") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = zoom,
                    onValueChange = { zoom = it },
                    label = { Text("缩放级别 (1-20)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val minLatVal = minLat.toDoubleOrNull() ?: return@TextButton
                    val maxLatVal = maxLat.toDoubleOrNull() ?: return@TextButton
                    val minLngVal = minLng.toDoubleOrNull() ?: return@TextButton
                    val maxLngVal = maxLng.toDoubleOrNull() ?: return@TextButton
                    val zoomVal = zoom.toIntOrNull() ?: return@TextButton
                    if (name.isNotBlank()) {
                        onSave(region?.id ?: 0L, name, minLatVal, maxLatVal, minLngVal, maxLngVal, zoomVal)
                    }
                }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
