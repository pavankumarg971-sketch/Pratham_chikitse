package com.example.health.ui.learning

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.health.R
import com.example.health.data.model.MythFact
import com.example.health.ui.components.ShimmerEffect
import com.example.health.ui.theme.HealthThemeExtras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(
    onModuleClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: LearningViewModel = hiltViewModel()
) {
    val modules by viewModel.modules.collectAsStateWithLifecycle()
    val myths by viewModel.myths.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.learning_center)) }, navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTab) {
                Tab(selectedTab == 0, { viewModel.selectTab(0) }, text = { Text(stringResource(R.string.modules)) }, icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, null) })
                Tab(selectedTab == 1, { viewModel.selectTab(1) }, text = { Text(stringResource(R.string.myths_facts)) }, icon = { Icon(Icons.Filled.Lightbulb, null) })
            }

            if (isLoading) { ShimmerEffect(); return@Scaffold }

            when (selectedTab) {
                0 -> LazyColumn(contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(modules, key = { it.id }) { module ->
                        Card(
                            onClick = { onModuleClick(module.id) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(module.icon, fontSize = 32.sp)
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(module.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Schedule, null, Modifier.size(14.dp), MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.width(4.dp))
                                        Text("${module.estimatedReadTime} min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.width(12.dp))
                                        AssistChip(onClick = {}, label = { Text(module.category, style = MaterialTheme.typography.labelSmall) })
                                    }
                                }
                                Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                1 -> LazyColumn(contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(myths, key = { it.id }) { myth -> MythFactCard(myth) }
                }
            }
        }
    }
}

@Composable
fun MythFactCard(myth: MythFact) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        onClick = { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Text("❌", fontSize = 20.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("MYTH", style = MaterialTheme.typography.labelSmall, color = HealthThemeExtras.colors.emergency, fontWeight = FontWeight.Bold)
                    Text(myth.myth, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                }
            }
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text("✅", fontSize = 20.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("FACT", style = MaterialTheme.typography.labelSmall, color = HealthThemeExtras.colors.safe, fontWeight = FontWeight.Bold)
                        Text(myth.fact, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                Spacer(Modifier.height(4.dp))
                Text("Tap to reveal the fact", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningDetailScreen(
    moduleId: String,
    onBack: () -> Unit,
    viewModel: LearningViewModel = hiltViewModel()
) {
    LaunchedEffect(moduleId) { viewModel.selectModule(moduleId) }
    val module by viewModel.selectedModule.collectAsStateWithLifecycle()
    val m = module ?: return

    Scaffold(
        topBar = { TopAppBar(title = { Text(m.title) }, navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(m.icon, fontSize = 40.sp)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(m.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Schedule, null, Modifier.size(16.dp), MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(4.dp))
                        Text("${m.estimatedReadTime} min read", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(12.dp))
                        AssistChip(onClick = {}, label = { Text(m.difficulty.replaceFirstChar { it.uppercase() }) })
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            // Render content as formatted text
            Text(m.content, style = MaterialTheme.typography.bodyLarge, lineHeight = 26.sp)
        }
    }
}
