package com.example.health.ui.assistant

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.health.R
import com.example.health.data.model.Hospital
import com.example.health.ui.theme.HealthThemeExtras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    onBack: () -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToHospitals: () -> Unit,
    viewModel: AssistantViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val isTyping by viewModel.isTyping.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.assistant_title), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.local_gemini_ai), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding()) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    if (msg.isUser) {
                        // ── User message bubble ──
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Card(
                                shape = RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp),
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)
                            ) {
                                Text(msg.text, Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    } else {
                        // ── Bot message ──
                        Column {
                            // Header: avatar + label + optional Gemini badge
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(28.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), Alignment.Center) {
                                    Icon(Icons.Filled.SmartToy, null, Modifier.size(16.dp), MaterialTheme.colorScheme.primary)
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.assistant), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                if (msg.isGeminiResponse) {
                                    Spacer(Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                    ) {
                                        Row(Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.AutoAwesome, null, Modifier.size(10.dp))
                                            Spacer(Modifier.width(3.dp))
                                            Text(stringResource(R.string.gemini_ai), style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(4.dp))

                            // ── Hospital cards (rich UI) ──
                            if (msg.hospitals.isNotEmpty()) {
                                Card(
                                    shape = RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp),
                                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(msg.text, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(Modifier.height(8.dp))

                                        msg.hospitals.forEach { hospital ->
                                            ChatHospitalCard(
                                                hospital = hospital,
                                                userLat = msg.userLat,
                                                userLng = msg.userLng,
                                                onCall = {
                                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${hospital.phone}")))
                                                },
                                                onMap = {
                                                    val uri = Uri.parse("geo:${hospital.latitude},${hospital.longitude}?q=${Uri.encode(hospital.name)}")
                                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                                }
                                            )
                                            Spacer(Modifier.height(8.dp))
                                        }

                                        // "View All" button
                                        FilledTonalButton(
                                            onClick = onNavigateToHospitals,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Filled.LocalHospital, null, Modifier.size(16.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text(stringResource(R.string.view_all_hospitals))
                                        }
                                    }
                                }
                            }
                            // ── Triage result card ──
                            else if (msg.triageResult != null || msg.text.isNotBlank()) {
                                Card(
                                    shape = RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp),
                                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(msg.text, style = MaterialTheme.typography.bodyMedium)

                                        msg.triageResult?.let { result ->
                                            if (result.matchedCategory != null) {
                                                Spacer(Modifier.height(12.dp))
                                                val urgencyColor = when {
                                                    result.urgencyLevel >= 4 -> HealthThemeExtras.colors.emergency
                                                    result.urgencyLevel >= 3 -> HealthThemeExtras.colors.warning
                                                    else -> HealthThemeExtras.colors.safe
                                                }
                                                Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(urgencyColor.copy(0.1f))) {
                                                    Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Text("${result.matchedCategory.icon} ", style = MaterialTheme.typography.titleMedium)
                                                        Text("${result.matchedCategory.name} • Urgency: ${result.urgencyLevel}/5", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = urgencyColor)
                                                    }
                                                }

                                                if (result.immediateActions.isNotEmpty()) {
                                                    Spacer(Modifier.height(8.dp))
                                                    Text(stringResource(R.string.immediate_steps), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                                    result.immediateActions.forEachIndexed { i, step ->
                                                        Text("${i + 1}. $step", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 2.dp))
                                                    }
                                                }

                                                Spacer(Modifier.height(12.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    FilledTonalButton(onClick = { onNavigateToCategory(result.matchedCategory.id) }, modifier = Modifier.weight(1f)) {
                                                        Icon(Icons.AutoMirrored.Filled.MenuBook, null, Modifier.size(16.dp))
                                                        Spacer(Modifier.width(4.dp))
                                                        Text(stringResource(R.string.full_guide), style = MaterialTheme.typography.labelSmall)
                                                    }
                                                    if (result.callEmergency) {
                                                        Button(
                                                            onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))) },
                                                            modifier = Modifier.weight(1f),
                                                            colors = ButtonDefaults.buttonColors(HealthThemeExtras.colors.emergency)
                                                        ) {
                                                            Icon(Icons.Filled.Phone, null, Modifier.size(16.dp))
                                                            Spacer(Modifier.width(4.dp))
                                                            Text(stringResource(R.string.call_108), style = MaterialTheme.typography.labelSmall)
                                                        }
                                                    }
                                                }
                                                FilledTonalButton(onClick = onNavigateToHospitals, modifier = Modifier.fillMaxWidth()) {
                                                    Icon(Icons.Filled.LocalHospital, null, Modifier.size(16.dp))
                                                    Spacer(Modifier.width(4.dp))
                                                    Text(stringResource(R.string.find_hospitals))
                                                }
                                            } else if (result.callEmergency) {
                                                Spacer(Modifier.height(8.dp))
                                                Button(
                                                    onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))) },
                                                    colors = ButtonDefaults.buttonColors(HealthThemeExtras.colors.emergency),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Icon(Icons.Filled.Phone, null); Spacer(Modifier.width(4.dp)); Text(stringResource(R.string.call_108_now))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Typing indicator
                if (isTyping) {
                    item { TypingIndicator() }
                }
            }

            // Input bar
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.updateInput(it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.type_message)) },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = { viewModel.sendMessage() },
                        enabled = inputText.isNotBlank() && !isTyping
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, stringResource(R.string.send))
                    }
                }
            }
        }
    }
}

/**
 * Rich hospital card rendered inline in the chat — mirrors the HospitalScreen card design.
 */
@Composable
private fun ChatHospitalCard(
    hospital: Hospital,
    userLat: Double,
    userLng: Double,
    onCall: () -> Unit,
    onMap: () -> Unit
) {
    val distance = if (userLat != 0.0 || userLng != 0.0) {
        val km = hospital.distanceTo(userLat, userLng)
        if (km < 1.0) "%.0f m".format(km * 1000) else "%.1f km".format(km)
    } else ""

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(hospital.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        hospital.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (distance.isNotBlank()) {
                    AssistChip(
                        onClick = {},
                        label = { Text(distance, style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = { Icon(Icons.Filled.NearMe, null, Modifier.size(14.dp)) }
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (hospital.emergencyAvailable) {
                    AssistChip(
                        onClick = {},
                        label = { Text(stringResource(R.string.emergency), style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = { Icon(Icons.Filled.LocalHospital, null, Modifier.size(14.dp), tint = HealthThemeExtras.colors.emergency) }
                    )
                }
                if (hospital.open24x7) {
                    AssistChip(
                        onClick = {},
                        label = { Text(stringResource(R.string.open_24x7), style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = { Icon(Icons.Filled.Schedule, null, Modifier.size(14.dp)) }
                    )
                }
                Text(
                    hospital.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onCall, Modifier.weight(1f)) {
                    Icon(Icons.Filled.Phone, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.call), style = MaterialTheme.typography.labelSmall)
                }
                Button(onMap, Modifier.weight(1f)) {
                    Icon(Icons.Filled.Map, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.directions), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

/**
 * Animated three-dot typing indicator shown while waiting for a response.
 */
@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(28.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), Alignment.Center) {
            Icon(Icons.Filled.SmartToy, null, Modifier.size(16.dp), MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.width(8.dp))
        Card(
            shape = RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { index ->
                    val delay = index * 200
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = delay),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot$index"
                    )
                    Box(
                        Modifier
                            .size(8.dp)
                            .background(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}
