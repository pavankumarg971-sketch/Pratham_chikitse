package com.example.health.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.health.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCategoryClick: (String) -> Unit,
    onSOSClick: () -> Unit,
    onAssistantClick: () -> Unit,
    onHospitalsClick: () -> Unit,
    onViewAllCategories: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    if (uiState.isLoading) { ShimmerEffect(); return }
    if (uiState.error != null) { ErrorState(message = uiState.error ?: stringResource(R.string.unknown_error)); return }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Header
        item {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.home_subtitle), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.search(it) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Filled.Search, stringResource(R.string.search)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) IconButton({ viewModel.search("") }) { Icon(Icons.Filled.Clear, stringResource(R.string.clear)) }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(16.dp))
        }

        // Emergency Banner
        item {
            EmergencyBanner(onClick = onSOSClick, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(16.dp))
        }

        // Quick Actions
        item {
            SectionHeader(title = stringResource(R.string.quick_actions))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(Icons.Filled.Phone, stringResource(R.string.call_108), onSOSClick, Modifier.weight(1f))
                QuickActionCard(Icons.Filled.SmartToy, stringResource(R.string.ai_assist), onAssistantClick, Modifier.weight(1f))
                QuickActionCard(Icons.Filled.LocalHospital, stringResource(R.string.hospitals), onHospitalsClick, Modifier.weight(1f))
            }
            Spacer(Modifier.height(20.dp))
        }

        // Search results or normal content
        if (searchQuery.isNotEmpty()) {
            val filtered = viewModel.getFilteredCategories()
            item { SectionHeader(title = stringResource(R.string.search_results) + " (${filtered.size})") }
            items(filtered, key = { it.id }) { cat ->
                EmergencyCard(
                    category = cat,
                    isBookmarked = uiState.bookmarkedIds.contains(cat.id),
                    onClick = { onCategoryClick(cat.id) },
                    onBookmark = { viewModel.toggleBookmark(cat.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).animateContentSize()
                )
            }
        } else {
            // Recent
            if (uiState.recentCategories.isNotEmpty()) {
                item {
                    SectionHeader(title = stringResource(R.string.recently_viewed))
                    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.recentCategories, key = { "recent_${it.id}" }) { cat ->
                            Card(
                                onClick = { onCategoryClick(cat.id) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow),
                                modifier = Modifier.width(160.dp)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(cat.icon, fontSize = 28.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text(cat.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }

            // Top / All Categories
            item { SectionHeader(title = stringResource(R.string.all_emergencies), action = stringResource(R.string.view_all), onAction = onViewAllCategories) }
            items(uiState.categories, key = { it.id }) { cat ->
                EmergencyCard(
                    category = cat,
                    isBookmarked = uiState.bookmarkedIds.contains(cat.id),
                    onClick = { onCategoryClick(cat.id) },
                    onBookmark = { viewModel.toggleBookmark(cat.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).animateContentSize()
                )
            }
        }
    }
}
