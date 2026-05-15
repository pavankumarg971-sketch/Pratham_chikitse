package com.example.health.ui.emergency

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.health.R
import com.example.health.ui.components.SeverityChip
import com.example.health.ui.theme.HealthThemeExtras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyDetailScreen(
    categoryId: String,
    onBack: () -> Unit,
    onSOS: () -> Unit,
    viewModel: EmergencyViewModel = hiltViewModel()
) {
    LaunchedEffect(categoryId) { viewModel.selectCategory(categoryId) }
    val category by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    
    // Get the current language from SettingsViewModel or LocaleHelper
    val lang = java.util.Locale.getDefault().language
    
    val cat = category ?: return

    val severityColor = when (cat.severity) {
        "critical" -> HealthThemeExtras.colors.severityCritical
        "high" -> HealthThemeExtras.colors.severityHigh
        else -> HealthThemeExtras.colors.severityMedium
    }
    val isBookmarked = bookmarks.contains(cat.id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cat.name) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } },
                actions = {
                    IconButton({ viewModel.toggleBookmark(cat.id) }) {
                        Icon(if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder, stringResource(R.string.bookmark),
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton({ viewModel.speakSteps(cat.immediateSteps, lang) }) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, stringResource(R.string.read_aloud))
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onSOS,
                containerColor = HealthThemeExtras.colors.emergency,
                contentColor = HealthThemeExtras.colors.onEmergency
            ) {
                Icon(Icons.Filled.Phone, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.call_108))
            }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 96.dp)) {
            // Header
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(cat.icon, fontSize = 48.sp)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(cat.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        SeverityChip(cat.severityLevel, severityColor)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(cat.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))

                // Disclaimer mini
                Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(HealthThemeExtras.colors.warningContainer)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, null, tint = HealthThemeExtras.colors.warning, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.first_aid_guidance_only), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(Modifier.height(16.dp))

                // When to call
                if (cat.whenToCallEmergency.isNotBlank()) {
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(HealthThemeExtras.colors.emergencyContainer)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Filled.Warning, null, tint = HealthThemeExtras.colors.emergency)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(stringResource(R.string.when_to_call), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = HealthThemeExtras.colors.emergency)
                                Spacer(Modifier.height(4.dp))
                                Text(cat.whenToCallEmergency, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Immediate Steps header
                Text(stringResource(R.string.immediate_steps), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
            }

            // Steps
            itemsIndexed(cat.immediateSteps) { index, step ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Box(Modifier.size(28.dp).background(MaterialTheme.colorScheme.primary, CircleShape), Alignment.Center) {
                            Text("${index + 1}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(step, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    }
                }
            }

            // Do's
            if (cat.doList.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Text("✅ ${stringResource(R.string.do_list)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = HealthThemeExtras.colors.safe)
                    Spacer(Modifier.height(8.dp))
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(HealthThemeExtras.colors.safeContainer)) {
                        Column(Modifier.padding(16.dp)) {
                            cat.doList.forEach { item ->
                                Row(Modifier.padding(vertical = 4.dp)) {
                                    Text("•", color = HealthThemeExtras.colors.safe, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(8.dp))
                                    Text(item, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }

            // Don'ts
            if (cat.dontList.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Text("❌ ${stringResource(R.string.dont_list)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = HealthThemeExtras.colors.emergency)
                    Spacer(Modifier.height(8.dp))
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(HealthThemeExtras.colors.emergencyContainer)) {
                        Column(Modifier.padding(16.dp)) {
                            cat.dontList.forEach { item ->
                                Row(Modifier.padding(vertical = 4.dp)) {
                                    Text("•", color = HealthThemeExtras.colors.emergency, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(8.dp))
                                    Text(item, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }

            // Prevention
            if (cat.preventionTips.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Text("🛡️ ${stringResource(R.string.prevention)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)) {
                        Column(Modifier.padding(16.dp)) {
                            cat.preventionTips.forEach { tip ->
                                Row(Modifier.padding(vertical = 4.dp)) {
                                    Text("•", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(8.dp))
                                    Text(tip, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
