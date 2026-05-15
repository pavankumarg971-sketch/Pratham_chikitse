package com.example.health.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "health_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val DISCLAIMER_ACCEPTED = booleanPreferencesKey("disclaimer_accepted")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val FONT_SIZE_SCALE = floatPreferencesKey("font_size_scale")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val LARGE_TEXT = booleanPreferencesKey("large_text")
        val SENIOR_MODE = booleanPreferencesKey("senior_mode")
        val VIBRATION_CUES = booleanPreferencesKey("vibration_cues")
        val VOICE_FIRST_MODE = booleanPreferencesKey("voice_first_mode")
        val EMERGENCY_CONTACTS = stringPreferencesKey("emergency_contacts")
        val BOOKMARKS = stringPreferencesKey("bookmarks")
        val RECENTS = stringPreferencesKey("recents")
        val SEARCH_HISTORY = stringPreferencesKey("search_history")
        val VIEW_COUNTS = stringPreferencesKey("view_counts")
        val LAST_LAT = floatPreferencesKey("last_lat")
        val LAST_LNG = floatPreferencesKey("last_lng")
    }

    private fun <T> safeFlow(flow: Flow<T>, default: T): Flow<T> =
        flow.catch { emit(default) }

    val onboardingCompleted: Flow<Boolean> = safeFlow(
        context.dataStore.data.map { it[Keys.ONBOARDING_COMPLETED] ?: false }, false
    )

    val disclaimerAccepted: Flow<Boolean> = safeFlow(
        context.dataStore.data.map { it[Keys.DISCLAIMER_ACCEPTED] ?: false }, false
    )

    val themeMode: Flow<String> = safeFlow(
        context.dataStore.data.map { it[Keys.THEME_MODE] ?: "system" }, "system"
    )

    val language: Flow<String> = safeFlow(
        context.dataStore.data.map { it[Keys.LANGUAGE] ?: "en" }, "en"
    )

    val fontSizeScale: Flow<Float> = safeFlow(
        context.dataStore.data.map { it[Keys.FONT_SIZE_SCALE] ?: 1.0f }, 1.0f
    )

    val highContrast: Flow<Boolean> = safeFlow(
        context.dataStore.data.map { it[Keys.HIGH_CONTRAST] ?: false }, false
    )

    val largeText: Flow<Boolean> = safeFlow(
        context.dataStore.data.map { it[Keys.LARGE_TEXT] ?: false }, false
    )

    val seniorMode: Flow<Boolean> = safeFlow(
        context.dataStore.data.map { it[Keys.SENIOR_MODE] ?: false }, false
    )

    val vibrationCues: Flow<Boolean> = safeFlow(
        context.dataStore.data.map { it[Keys.VIBRATION_CUES] ?: true }, true
    )

    val voiceFirstMode: Flow<Boolean> = safeFlow(
        context.dataStore.data.map { it[Keys.VOICE_FIRST_MODE] ?: false }, false
    )

    val emergencyContacts: Flow<String> = safeFlow(
        context.dataStore.data.map { it[Keys.EMERGENCY_CONTACTS] ?: "[]" }, "[]"
    )

    val bookmarks: Flow<Set<String>> = safeFlow(
        context.dataStore.data.map {
            (it[Keys.BOOKMARKS] ?: "").split(",").filter { s -> s.isNotBlank() }.toSet()
        }, emptySet()
    )

    val recents: Flow<List<String>> = safeFlow(
        context.dataStore.data.map {
            (it[Keys.RECENTS] ?: "").split(",").filter { s -> s.isNotBlank() }
        }, emptyList()
    )

    val viewCounts: Flow<Map<String, Int>> = safeFlow(
        context.dataStore.data.map {
            val raw = it[Keys.VIEW_COUNTS] ?: ""
            if (raw.isBlank()) emptyMap()
            else raw.split(",").mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) parts[0] to (parts[1].toIntOrNull() ?: 0) else null
            }.toMap()
        }, emptyMap()
    )

    val lastLat: Flow<Float> = safeFlow(
        context.dataStore.data.map { it[Keys.LAST_LAT] ?: 0f }, 0f
    )
    val lastLng: Flow<Float> = safeFlow(
        context.dataStore.data.map { it[Keys.LAST_LNG] ?: 0f }, 0f
    )

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setDisclaimerAccepted(accepted: Boolean) {
        context.dataStore.edit { it[Keys.DISCLAIMER_ACCEPTED] = accepted }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = lang }
    }

    suspend fun setFontSizeScale(scale: Float) {
        context.dataStore.edit { it[Keys.FONT_SIZE_SCALE] = scale }
    }

    suspend fun setHighContrast(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HIGH_CONTRAST] = enabled }
    }

    suspend fun setLargeText(enabled: Boolean) {
        context.dataStore.edit { it[Keys.LARGE_TEXT] = enabled }
    }

    suspend fun setSeniorMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SENIOR_MODE] = enabled }
    }

    suspend fun setVibrationCues(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VIBRATION_CUES] = enabled }
    }

    suspend fun setVoiceFirstMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VOICE_FIRST_MODE] = enabled }
    }

    suspend fun setEmergencyContacts(json: String) {
        context.dataStore.edit { it[Keys.EMERGENCY_CONTACTS] = json }
    }

    suspend fun toggleBookmark(id: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[Keys.BOOKMARKS] ?: "").split(",").filter { it.isNotBlank() }.toMutableSet()
            if (current.contains(id)) current.remove(id) else current.add(id)
            prefs[Keys.BOOKMARKS] = current.joinToString(",")
        }
    }

    suspend fun addRecent(id: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[Keys.RECENTS] ?: "").split(",").filter { it.isNotBlank() }.toMutableList()
            current.remove(id)
            current.add(0, id)
            prefs[Keys.RECENTS] = current.take(20).joinToString(",")
        }
    }

    suspend fun incrementViewCount(id: String) {
        context.dataStore.edit { prefs ->
            val raw = prefs[Keys.VIEW_COUNTS] ?: ""
            val counts = if (raw.isBlank()) mutableMapOf()
            else raw.split(",").mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) parts[0] to (parts[1].toIntOrNull() ?: 0) else null
            }.toMap().toMutableMap()
            counts[id] = (counts[id] ?: 0) + 1
            prefs[Keys.VIEW_COUNTS] = counts.entries.joinToString(",") { "${it.key}:${it.value}" }
        }
    }

    suspend fun setLastLocation(lat: Float, lng: Float) {
        context.dataStore.edit {
            it[Keys.LAST_LAT] = lat
            it[Keys.LAST_LNG] = lng
        }
    }
}
