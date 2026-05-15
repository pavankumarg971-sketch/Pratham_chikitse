package com.example.health.ui.hospital

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.health.R
import com.example.health.data.model.Hospital
import com.example.health.ui.components.ErrorState
import com.example.health.ui.components.ShimmerEffect
import com.example.health.ui.theme.HealthThemeExtras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalScreen(onBack: () -> Unit, viewModel: HospitalViewModel = hiltViewModel()) {
    val hospitals by viewModel.hospitals.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var permissionRequested by remember { mutableStateOf(false) }

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true || 
                      permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            try {
                val client = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
                client.lastLocation.addOnSuccessListener { location ->
                    if (location != null) viewModel.updateLocation(location.latitude, location.longitude)
                    else viewModel.updateLocation(null, null)
                }.addOnFailureListener {
                    viewModel.updateLocation(null, null)
                }
            } catch (e: SecurityException) {
                viewModel.updateLocation(null, null)
            }
        } else {
            viewModel.updateLocation(null, null)
        }
    }

    LaunchedEffect(Unit) {
        if (!permissionRequested) {
            permissionRequested = true
            launcher.launch(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hospitals)) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = searchQuery, onValueChange = { viewModel.search(it) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text(stringResource(R.string.search_hospitals)) },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                trailingIcon = { if (searchQuery.isNotEmpty()) IconButton({ viewModel.search("") }) { Icon(Icons.Filled.Clear, null) } },
                singleLine = true, shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(8.dp))

            when {
                isLoading -> ShimmerEffect()
                error != null -> ErrorState(message = error ?: stringResource(R.string.error))
                else -> LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(hospitals, key = { it.id }) { hospital ->
                        HospitalCard(hospital, viewModel.getDistanceText(hospital),
                            onCall = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${hospital.phone}"))) },
                            onMap = {
                                val uri = Uri.parse("geo:${hospital.latitude},${hospital.longitude}?q=${Uri.encode(hospital.name)}")
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HospitalCard(hospital: Hospital, distance: String, onCall: () -> Unit, onMap: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(hospital.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(hospital.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                if (distance.isNotBlank()) {
                    AssistChip(onClick = {}, label = { Text(distance) }, leadingIcon = { Icon(Icons.Filled.NearMe, null, Modifier.size(16.dp)) })
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (hospital.emergencyAvailable) AssistChip({}, { Text(stringResource(R.string.emergency)) }, leadingIcon = { Icon(Icons.Filled.LocalHospital, null, Modifier.size(16.dp), tint = HealthThemeExtras.colors.emergency) })
                if (hospital.open24x7) AssistChip({}, { Text("24×7") }, leadingIcon = { Icon(Icons.Filled.Schedule, null, Modifier.size(16.dp)) })
                Text(hospital.type, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterVertically))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onCall, Modifier.weight(1f)) { Icon(Icons.Filled.Phone, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text(stringResource(R.string.call)) }
                Button(onMap, Modifier.weight(1f)) { Icon(Icons.Filled.Directions, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text(stringResource(R.string.directions)) }
            }
        }
    }
}
