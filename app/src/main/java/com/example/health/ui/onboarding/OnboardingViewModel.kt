package com.example.health.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health.data.model.DisclaimerInfo
import com.example.health.data.repository.LearningRepository
import com.example.health.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPrefsRepo: UserPreferencesRepository,
    private val learningRepo: LearningRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
) : ViewModel() {

    val onboardingCompleted = userPrefsRepo.onboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val disclaimerAccepted = userPrefsRepo.disclaimerAccepted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _selectedLanguage = MutableStateFlow(com.example.health.util.LocaleHelper.getPersistedLanguage(context))
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _disclaimer = MutableStateFlow<DisclaimerInfo?>(null)
    val disclaimer: StateFlow<DisclaimerInfo?> = _disclaimer.asStateFlow()

    init {
        loadDisclaimer()
    }

    private fun loadDisclaimer() {
        viewModelScope.launch {
            learningRepo.getDisclaimer().onSuccess { _disclaimer.value = it }
        }
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
        viewModelScope.launch { userPrefsRepo.setLanguage(lang) }
    }

    fun acceptDisclaimer() {
        viewModelScope.launch { userPrefsRepo.setDisclaimerAccepted(true) }
    }

    fun completeOnboarding() {
        viewModelScope.launch { userPrefsRepo.setOnboardingCompleted(true) }
    }

    fun saveEmergencyContacts(contacts: String) {
        viewModelScope.launch { userPrefsRepo.setEmergencyContacts(contacts) }
    }

    fun saveLocation(lat: Double, lng: Double) {
        viewModelScope.launch { userPrefsRepo.setLastLocation(lat.toFloat(), lng.toFloat()) }
    }
}
