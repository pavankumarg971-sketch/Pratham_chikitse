package com.example.health.navigation

sealed class Screen(val route: String) {
    // Onboarding
    data object Splash : Screen("splash")
    data object Welcome : Screen("welcome")
    data object LanguageSelect : Screen("language_select")
    data object Permissions : Screen("permissions")
    data object Disclaimer : Screen("disclaimer")
    data object EmergencyContactsSetup : Screen("emergency_contacts_setup")

    // Main
    data object Home : Screen("home")
    data object EmergencyList : Screen("emergency_list")
    data object Hospitals : Screen("hospitals")
    data object Learning : Screen("learning")
    data object Settings : Screen("settings")

    // Detail / Deep
    data object EmergencyDetail : Screen("emergency_detail/{categoryId}") {
        fun createRoute(categoryId: String) = "emergency_detail/$categoryId"
    }
    data object LearningDetail : Screen("learning_detail/{moduleId}") {
        fun createRoute(moduleId: String) = "learning_detail/$moduleId"
    }

    // Special
    data object SOS : Screen("sos")
    data object Assistant : Screen("assistant")
}
