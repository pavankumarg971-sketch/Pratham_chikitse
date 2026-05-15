package com.example.health.data.repository

import com.example.health.data.local.UserPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) {
    val onboardingCompleted: Flow<Boolean> = dataStore.onboardingCompleted
    val disclaimerAccepted: Flow<Boolean> = dataStore.disclaimerAccepted
    val themeMode: Flow<String> = dataStore.themeMode
    val language: Flow<String> = dataStore.language
    val fontSizeScale: Flow<Float> = dataStore.fontSizeScale
    val highContrast: Flow<Boolean> = dataStore.highContrast
    val largeText: Flow<Boolean> = dataStore.largeText
    val seniorMode: Flow<Boolean> = dataStore.seniorMode
    val vibrationCues: Flow<Boolean> = dataStore.vibrationCues
    val voiceFirstMode: Flow<Boolean> = dataStore.voiceFirstMode
    val emergencyContacts: Flow<String> = dataStore.emergencyContacts
    val lastLat: Flow<Float> = dataStore.lastLat
    val lastLng: Flow<Float> = dataStore.lastLng

    suspend fun setOnboardingCompleted(v: Boolean) = dataStore.setOnboardingCompleted(v)
    suspend fun setDisclaimerAccepted(v: Boolean) = dataStore.setDisclaimerAccepted(v)
    suspend fun setThemeMode(v: String) = dataStore.setThemeMode(v)
    suspend fun setLanguage(v: String) = dataStore.setLanguage(v)
    suspend fun setFontSizeScale(v: Float) = dataStore.setFontSizeScale(v)
    suspend fun setHighContrast(v: Boolean) = dataStore.setHighContrast(v)
    suspend fun setLargeText(v: Boolean) = dataStore.setLargeText(v)
    suspend fun setSeniorMode(v: Boolean) = dataStore.setSeniorMode(v)
    suspend fun setVibrationCues(v: Boolean) = dataStore.setVibrationCues(v)
    suspend fun setVoiceFirstMode(v: Boolean) = dataStore.setVoiceFirstMode(v)
    suspend fun setEmergencyContacts(v: String) = dataStore.setEmergencyContacts(v)
    suspend fun setLastLocation(lat: Float, lng: Float) = dataStore.setLastLocation(lat, lng)
}
