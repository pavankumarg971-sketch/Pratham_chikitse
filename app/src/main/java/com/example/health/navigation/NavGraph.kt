package com.example.health.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.health.R
import com.example.health.ui.assistant.AssistantScreen
import com.example.health.ui.emergency.EmergencyDetailScreen
import com.example.health.ui.emergency.EmergencyListScreen
import com.example.health.ui.home.HomeScreen
import com.example.health.ui.hospital.HospitalScreen
import com.example.health.ui.learning.LearningDetailScreen
import com.example.health.ui.learning.LearningScreen
import com.example.health.ui.onboarding.*
import com.example.health.ui.settings.SettingsScreen
import com.example.health.ui.sos.SOSScreen
import com.example.health.ui.theme.HealthThemeExtras

private data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)

@Composable
fun AppNavGraph(
    onboardingDone: Boolean,
    disclaimerDone: Boolean
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in topLevelRoutes

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home.route, Icons.Filled.Home, stringResource(R.string.home)),
        BottomNavItem(Screen.EmergencyList.route, Icons.Filled.MedicalServices, stringResource(R.string.medical_services)),
        BottomNavItem("sos_placeholder", Icons.Filled.Phone, "SOS"),
        BottomNavItem(Screen.Hospitals.route, Icons.Filled.LocalHospital, stringResource(R.string.hospitals)),
        BottomNavItem(Screen.Settings.route, Icons.Filled.Settings, stringResource(R.string.settings))
    )

    val startDest = when {
        !onboardingDone -> Screen.Splash.route
        !disclaimerDone -> Screen.Disclaimer.route
        else -> Screen.Home.route
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController, currentRoute, bottomNavItems)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
            enterTransition = { fadeIn(tween(200)) + slideInHorizontally(tween(250)) { it / 6 } },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(200)) + slideInHorizontally(tween(250)) { -it / 6 } },
            popExitTransition = { fadeOut(tween(200)) }
        ) {
            // Onboarding
            composable(Screen.Splash.route) {
                SplashScreen { navController.navigate(Screen.LanguageSelect.route) { popUpTo(Screen.Splash.route) { inclusive = true } } }
            }
            composable(Screen.LanguageSelect.route) {
                val vm: OnboardingViewModel = hiltViewModel()
                val lang by vm.selectedLanguage.collectAsStateWithLifecycle()
                LanguageScreen(lang, vm::setLanguage, { navController.navigate(Screen.Welcome.route) }, { navController.navigate(Screen.Welcome.route) })
            }
            composable(Screen.Welcome.route) {
                WelcomeScreen { navController.navigate(Screen.Permissions.route) }
            }
            composable(Screen.Permissions.route) {
                PermissionsScreen({ navController.navigate(Screen.Disclaimer.route) }, { navController.navigate(Screen.Disclaimer.route) })
            }
            composable(Screen.Disclaimer.route) {
                val vm: OnboardingViewModel = hiltViewModel()
                val disc by vm.disclaimer.collectAsStateWithLifecycle()
                DisclaimerScreen(
                    body = disc?.body ?: "Loading disclaimer...",
                    reviewedBy = disc?.reviewedBy ?: "",
                    onAccept = {
                        vm.acceptDisclaimer()
                        navController.navigate(Screen.EmergencyContactsSetup.route)
                    }
                )
            }
            composable(Screen.EmergencyContactsSetup.route) {
                val vm: OnboardingViewModel = hiltViewModel()
                EmergencyContactsSetupScreen(
                    onSave = { contacts ->
                        vm.saveEmergencyContacts(contacts)
                        vm.completeOnboarding()
                        navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } }
                    },
                    onSkip = {
                        vm.completeOnboarding()
                        navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            // Main
            composable(Screen.Home.route) {
                HomeScreen(
                    onCategoryClick = { navController.navigate(Screen.EmergencyDetail.createRoute(it)) },
                    onSOSClick = { navController.navigate(Screen.SOS.route) },
                    onAssistantClick = { navController.navigate(Screen.Assistant.route) },
                    onHospitalsClick = { navController.navigate(Screen.Hospitals.route) },
                    onViewAllCategories = { navController.navigate(Screen.EmergencyList.route) }
                )
            }
            composable(Screen.EmergencyList.route) {
                EmergencyListScreen(
                    onCategoryClick = { navController.navigate(Screen.EmergencyDetail.createRoute(it)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.EmergencyDetail.route) { entry ->
                val catId = entry.arguments?.getString("categoryId") ?: return@composable
                EmergencyDetailScreen(catId, onBack = { navController.popBackStack() }, onSOS = { navController.navigate(Screen.SOS.route) })
            }
            composable(Screen.Hospitals.route) {
                HospitalScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Learning.route) {
                LearningScreen(
                    onModuleClick = { navController.navigate(Screen.LearningDetail.createRoute(it)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.LearningDetail.route) { entry ->
                val modId = entry.arguments?.getString("moduleId") ?: return@composable
                LearningDetailScreen(modId, onBack = { navController.popBackStack() })
            }
            composable(Screen.Settings.route) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.SOS.route) {
                SOSScreen(
                    onBack = { navController.popBackStack() },
                    onHospitals = { navController.navigate(Screen.Hospitals.route) },
                    onGuides = { navController.navigate(Screen.EmergencyList.route) }
                )
            }
            composable(Screen.Assistant.route) {
                AssistantScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToCategory = { navController.navigate(Screen.EmergencyDetail.createRoute(it)) },
                    onNavigateToHospitals = { navController.navigate(Screen.Hospitals.route) }
                )
            }
        }
    }
}

private val topLevelRoutes = setOf(Screen.Home.route, Screen.EmergencyList.route, Screen.Hospitals.route, Screen.Settings.route, Screen.Learning.route)

@Composable
private fun BottomNavBar(navController: NavHostController, currentRoute: String?, items: List<BottomNavItem>) {
    NavigationBar(
        tonalElevation = 2.dp
    ) {
        items.forEach { item ->
            if (item.route == "sos_placeholder") {
                // Center SOS CTA
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.SOS.route) },
                    icon = {
                        FloatingActionButton(
                            onClick = { navController.navigate(Screen.SOS.route) },
                            containerColor = HealthThemeExtras.colors.emergency,
                            contentColor = HealthThemeExtras.colors.onEmergency,
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Filled.Phone, "SOS", Modifier.size(24.dp))
                        }
                    },
                    label = { Text("SOS", style = MaterialTheme.typography.labelSmall) }
                )
            } else {
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(item.icon, item.label) },
                    label = { Text(item.label, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
    }
}
