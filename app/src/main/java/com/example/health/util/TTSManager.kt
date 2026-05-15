package com.example.health.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TTSManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null

    fun initialize(language: String? = null, onReady: (() -> Unit)? = null) {
        if (isInitialized && (language == null || tts?.language?.language == language)) {
            onReady?.invoke()
            return
        }
        
        if (tts == null) {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    setTtsLanguage(language ?: Locale.getDefault().language)
                    isInitialized = true
                    onReady?.invoke()
                    pendingText?.let { speak(it, language) }
                    pendingText = null
                }
            }
        } else {
            setTtsLanguage(language ?: Locale.getDefault().language)
            onReady?.invoke()
        }
    }

    private fun setTtsLanguage(langCode: String) {
        val locale = when (langCode) {
            "en" -> Locale("en", "IN")
            "kn" -> Locale("kn", "IN")
            "hi" -> Locale("hi", "IN")
            "gu" -> Locale("gu", "IN")
            "mr" -> Locale("mr", "IN")
            "ta" -> Locale("ta", "IN")
            else -> Locale(langCode)
        }
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale("en", "IN"))
        }
    }

    fun speak(text: String, language: String? = null) {
        if (!isInitialized || (language != null && tts?.language?.language != language)) {
            pendingText = text
            initialize(language)
            return
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "health_tts_${System.currentTimeMillis()}")
    }

    fun speakSteps(steps: List<String>, language: String? = null) {
        if (!isInitialized || (language != null && tts?.language?.language != language)) {
            initialize(language) {
                speakStepsInternal(steps)
            }
            return
        }
        speakStepsInternal(steps)
    }

    private fun speakStepsInternal(steps: List<String>) {
        tts?.let { engine ->
            steps.forEachIndexed { index, step ->
                val utteranceId = "step_$index"
                val mode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                engine.speak("Step ${index + 1}: $step", mode, null, utteranceId)
            }
        }
    }

    fun stop() {
        tts?.stop()
    }

    val isSpeaking: Boolean get() = tts?.isSpeaking == true

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
