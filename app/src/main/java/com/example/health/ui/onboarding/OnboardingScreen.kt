package com.example.health.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.health.R
import com.example.health.ui.theme.HealthThemeExtras
import com.example.health.ui.theme.MedicalTeal
import com.example.health.ui.theme.MedicalTealLight
import kotlinx.coroutines.delay

@Composable
fun OnboardingLayout(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .systemBarsPadding(),
        content = content
    )
}

@Composable
fun SplashScreen(onNext: () -> Unit) {
    Box(Modifier.fillMaxSize().background(HealthThemeExtras.colors.emergency), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.MedicalServices, null, Modifier.size(80.dp), Color.White)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.displayMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.home_subtitle), style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(0.8f))
        }
    }
    LaunchedEffect(Unit) { delay(2000); onNext() }
}

@Composable
fun WelcomeScreen(onNext: () -> Unit) {
    OnboardingLayout {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.HealthAndSafety, null, Modifier.size(100.dp), MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(32.dp))
            Text(stringResource(R.string.welcome_title), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.welcome_desc), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(40.dp))
            val features = listOf(
                "🚑" to stringResource(R.string.feature_guides),
                "📱" to stringResource(R.string.feature_offline),
                "🏥" to stringResource(R.string.feature_hospitals),
                "🤖" to stringResource(R.string.feature_triage)
            )
            features.forEach { (icon, text) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.width(16.dp))
                    Text(text, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Button(onNext, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
            Text(stringResource(R.string.get_started), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
        }
    }
}

@Composable
fun LanguageScreen(selected: String, onSelect: (String) -> Unit, onNext: () -> Unit, onSkip: () -> Unit) {
    val langs = listOf(
        "en" to "🇺🇸 English",
        "hi" to "🇮🇳 हिन्दी",
        "gu" to "🇮🇳 ગુજરાતી",
        "mr" to "🇮🇳 मराठी",
        "ta" to "🇮🇳 தமிழ்",
        "kn" to "🇮🇳 ಕನ್ನಡ"
    )
    OnboardingLayout {
        Text(stringResource(R.string.choose_language), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.select_preferred_language), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            langs.forEach { (code, name) ->
                val isSel = selected == code
                Surface(onClick = { onSelect(code) }, shape = RoundedCornerShape(16.dp), color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(0.3f), border = if (isSel) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                        if (isSel) Icon(Icons.Filled.Check, stringResource(R.string.selected), tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(onNext, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) { Text(stringResource(R.string.continue_btn)) }
        TextButton(onSkip, Modifier.fillMaxWidth()) { Text(stringResource(R.string.skip)) }
    }
}

@Composable
fun PermissionsScreen(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingLayout {
        Text(stringResource(R.string.permissions), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.permissions_desc), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))
        PermCard(Icons.Filled.Phone, stringResource(R.string.perm_phone_title), stringResource(R.string.perm_phone_desc))
        PermCard(Icons.Filled.LocationOn, stringResource(R.string.perm_location_title), stringResource(R.string.perm_location_desc))
        Spacer(Modifier.weight(1f))
        Button(onClick = {
            // In real app, request permissions
            onNext()
        }, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) { Text(stringResource(R.string.grant_permissions)) }
        TextButton(onSkip, Modifier.fillMaxWidth()) { Text(stringResource(R.string.skip_permissions)) }
    }
}

@Composable
private fun PermCard(icon: ImageVector, title: String, desc: String) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), Alignment.Center) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun DisclaimerScreen(body: String, reviewedBy: String, onAccept: () -> Unit) {
    var checked by remember { mutableStateOf(false) }
    OnboardingLayout {
        Card(Modifier.weight(1f), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f))) {
            Column(Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text(stringResource(R.string.disclaimer), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                if (reviewedBy.isNotEmpty()) {
                    Surface(color = MaterialTheme.colorScheme.primary.copy(0.1f), shape = RoundedCornerShape(8.dp)) {
                        Text(stringResource(R.string.reviewed_by, reviewedBy), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                }
                Text(body, style = MaterialTheme.typography.bodyMedium, lineHeight = 24.sp)
            }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked, { checked = it })
            Text(stringResource(R.string.accept_continue), style = MaterialTheme.typography.bodyMedium)
        }
        Button(onAccept, Modifier.fillMaxWidth().height(56.dp), enabled = checked, shape = RoundedCornerShape(16.dp)) { Text(stringResource(R.string.accept_continue), style = MaterialTheme.typography.titleMedium) }
    }
}

@Composable
fun EmergencyContactsSetupScreen(onSave: (String) -> Unit, onSkip: () -> Unit) {
    val contacts = remember { mutableStateListOf<String>() }
    var input by remember { mutableStateOf("") }
    val fm = LocalFocusManager.current
    OnboardingLayout {
        Text(stringResource(R.string.emergency_contacts), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.emergency_contacts_desc), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(input, { input = it }, Modifier.weight(1f), label = { Text(stringResource(R.string.phone_number)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { if (input.length >= 10) { contacts.add(input); input = ""; fm.clearFocus() } }),
                singleLine = true, shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.width(8.dp))
            IconButton({ if (input.length >= 10) { contacts.add(input); input = "" } }) { Icon(Icons.Filled.Add, stringResource(R.string.add)) }
        }
        Spacer(Modifier.height(16.dp))
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            contacts.forEachIndexed { i, c ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Phone, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(12.dp))
                        Text(c, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        IconButton({ contacts.removeAt(i) }) { Icon(Icons.Filled.Delete, stringResource(R.string.remove), tint = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button({ onSave(contacts.joinToString(",")) }, Modifier.fillMaxWidth().height(56.dp), enabled = contacts.isNotEmpty(), shape = RoundedCornerShape(16.dp)) { Text(stringResource(R.string.save_continue)) }
        Spacer(Modifier.height(8.dp))
        TextButton(onSkip, Modifier.fillMaxWidth()) { Text(stringResource(R.string.skip_add_later)) }
    }
}
