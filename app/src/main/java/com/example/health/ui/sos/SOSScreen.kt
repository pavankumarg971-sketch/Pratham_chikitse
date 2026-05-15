package com.example.health.ui.sos

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.health.R
import com.example.health.ui.theme.HealthThemeExtras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(onBack: () -> Unit, onHospitals: () -> Unit, onGuides: () -> Unit) {
    val context = LocalContext.current
    val pulse = rememberInfiniteTransition(label = "pulse")
    val scale by pulse.animateFloat(1f, 1.08f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "ps")

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.emergency_sos)) }, navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } }) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // Big 108 button
            Button(
                onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))) },
                modifier = Modifier.size(180.dp).scale(scale),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(HealthThemeExtras.colors.emergency),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Phone, null, Modifier.size(40.dp), Color.White)
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.call_108), style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.ambulance), style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.8f))
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.tap_to_call_ambulance), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)

            Spacer(Modifier.height(24.dp))

            // Other emergency numbers
            Text(stringResource(R.string.other_emergency_numbers), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            listOf(
                "112" to stringResource(R.string.unified_emergency),
                "100" to stringResource(R.string.police),
                "101" to stringResource(R.string.fire_brigade)
            ).forEach { (num, label) ->
                OutlinedButton(
                    onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num"))) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Phone, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp))
                    Text("$num - $label", modifier = Modifier.weight(1f)); Icon(Icons.Filled.ChevronRight, null)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Quick steps
            Text(stringResource(R.string.while_waiting), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)) {
                Column(Modifier.padding(16.dp)) {
                    val steps = listOf(
                        stringResource(R.string.step_1_wait),
                        stringResource(R.string.step_2_wait),
                        stringResource(R.string.step_3_wait),
                        stringResource(R.string.step_4_wait),
                        stringResource(R.string.step_5_wait)
                    )
                    steps.forEachIndexed { i, step ->
                        Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                            Text("${i+1}.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp)); Text(step, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onHospitals, Modifier.weight(1f)) { Icon(Icons.Filled.LocalHospital, null); Spacer(Modifier.width(4.dp)); Text(stringResource(R.string.hospitals)) }
                OutlinedButton(onGuides, Modifier.weight(1f)) { Icon(Icons.AutoMirrored.Filled.MenuBook, null); Spacer(Modifier.width(4.dp)); Text(stringResource(R.string.guides)) }
            }

            Spacer(Modifier.height(16.dp))
            // Disclaimer
            Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(HealthThemeExtras.colors.warningContainer)) {
                Row(Modifier.padding(12.dp)) {
                    Icon(Icons.Filled.Info, null, tint = HealthThemeExtras.colors.warning, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.first_aid_guidance_only), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
