package com.example.health.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    private const val PREFS_NAME = "locale_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    /** Read the persisted language code (fast, synchronous — safe in attachBaseContext). */
    fun getPersistedLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, "en") ?: "en"
    }

    /** Persist language code to SharedPreferences so it survives recreate(). */
    fun persistLanguage(context: Context, language: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }

    /**
     * Wrap a Context with the given locale.
     * Call from Activity.attachBaseContext so every string resource is localised
     * before any View is inflated.
     */
    fun wrap(context: Context, language: String): ContextWrapper {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        val localised = context.createConfigurationContext(config)
        return ContextWrapper(localised)
    }
}
