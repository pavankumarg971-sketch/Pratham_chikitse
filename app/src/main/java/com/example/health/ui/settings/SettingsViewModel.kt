package com.example.health.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health.data.repository.UserPreferencesRepository
import com.example.health.util.LocaleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPrefsRepo: UserPreferencesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val themeMode = userPrefsRepo.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")
    val language = userPrefsRepo.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocaleHelper.getPersistedLanguage(context))
    val highContrast = userPrefsRepo.highContrast
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val largeText = userPrefsRepo.largeText
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val seniorMode = userPrefsRepo.seniorMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val vibrationCues = userPrefsRepo.vibrationCues
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val voiceFirstMode = userPrefsRepo.voiceFirstMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val disclaimerAccepted = userPrefsRepo.disclaimerAccepted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setThemeMode(v: String) = viewModelScope.launch { userPrefsRepo.setThemeMode(v) }
    fun setHighContrast(v: Boolean) = viewModelScope.launch { userPrefsRepo.setHighContrast(v) }
    fun setLargeText(v: Boolean) = viewModelScope.launch { userPrefsRepo.setLargeText(v) }
    fun setSeniorMode(v: Boolean) = viewModelScope.launch { userPrefsRepo.setSeniorMode(v) }
    fun setVibrationCues(v: Boolean) = viewModelScope.launch { userPrefsRepo.setVibrationCues(v) }
    fun setVoiceFirstMode(v: Boolean) = viewModelScope.launch { userPrefsRepo.setVoiceFirstMode(v) }
    fun setLanguage(v: String) = viewModelScope.launch { userPrefsRepo.setLanguage(v) }
}
