package com.example.health

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.health.data.repository.EmergencyRepository
import com.example.health.data.repository.HospitalRepository
import com.example.health.data.repository.LearningRepository
import com.example.health.navigation.AppNavGraph
import com.example.health.ui.onboarding.OnboardingViewModel
import com.example.health.ui.settings.SettingsViewModel
import com.example.health.ui.theme.HealthTheme
import com.example.health.util.LocaleHelper
import com.example.health.util.TTSManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var emergencyRepo: EmergencyRepository
    @Inject lateinit var hospitalRepo: HospitalRepository
    @Inject lateinit var learningRepo: LearningRepository
    @Inject lateinit var ttsManager: TTSManager

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getPersistedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.wrap(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsVm: SettingsViewModel = hiltViewModel()
            val themeMode by settingsVm.themeMode.collectAsStateWithLifecycle()
            val language by settingsVm.language.collectAsStateWithLifecycle()
            
            val isDarkTheme = when (themeMode) {
                "dark"  -> true
                "light" -> false
                else    -> isSystemInDarkTheme()
            }

            // Fixed recreation logic: Compare against the actual applied locale
            LaunchedEffect(language) {
                val currentLang = resources.configuration.locales[0].language
                if (language.isNotEmpty() && currentLang != language) {
                    LocaleHelper.persistLanguage(applicationContext, language)
                    
                    // Clear caches and shutdown services before recreation
                    emergencyRepo.clearCache()
                    hospitalRepo.clearCache()
                    learningRepo.clearCache()
                    ttsManager.shutdown()
                    
                    recreate()
                }
            }

            HealthTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val onboardingVm: OnboardingViewModel = hiltViewModel()
                    val onboardingDone by onboardingVm.onboardingCompleted.collectAsStateWithLifecycle()
                    val disclaimerDone by onboardingVm.disclaimerAccepted.collectAsStateWithLifecycle()

                    AppNavGraph(
                        onboardingDone = onboardingDone,
                        disclaimerDone = disclaimerDone
                    )
                }
            }
        }
    }
}
