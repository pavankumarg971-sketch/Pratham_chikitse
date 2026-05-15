package com.example.health.data.remote

import android.util.Log
import com.example.health.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps the Google Gemini Generative AI SDK. Used as a fallback when the local keyword-based triage
 * engine cannot match the user's query to any emergency category.
 *
 * The system prompt constrains responses to the medical / first-aid domain of Pratham Chikitse so
 * the model does not hallucinate unrelated content.
 */
@Singleton
class GeminiService @Inject constructor() {

    private val apiKey: String = BuildConfig.API_KEY

    /** True when a valid API key has been configured in the build environment. */
    val isAvailable: Boolean
        get() = apiKey.isNotBlank()

    private val model: GenerativeModel? by lazy {
        if (!isAvailable) return@lazy null
        GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = apiKey,
                generationConfig =
                        generationConfig {
                            temperature = 0.7f
                            topK = 40
                            topP = 0.95f
                            maxOutputTokens = 512
                        },
                systemInstruction = content { text(SYSTEM_PROMPT) }
        )
    }

    /**
     * Sends a user query to Gemini and returns the response text. Returns null if the API key is
     * missing or an error occurs.
     */
    suspend fun ask(userMessage: String): String? {
        if (!isAvailable) {
            Log.w(TAG, "Gemini API key not configured — skipping AI fallback")
            return null
        }
        return try {
            val response = model?.generateContent(userMessage)
            response?.text?.trim()
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API error", e)
            null
        }
    }

    companion object {
        private const val TAG = "GeminiService"

        private const val SYSTEM_PROMPT =
                """
You are the friendly AI assistant for "Pratham Chikitse" (First Aid), an Android 
medical first-aid application. Your name is "Pratham Assistant".

BEHAVIOR RULES:

1. For casual greetings (hi, hello, how are you, etc.):
   Reply warmly and briefly, like: "Hello! I'm Pratham Assistant. 
   How can I help you with health or first-aid today?" 
   Do NOT add medical disclaimers to greetings.

2. For health/medical/first-aid questions:
   - Give concise, actionable advice (3-5 sentences max)
   - End with: "⚕️ Note: This is general guidance, not professional 
     medical advice. For serious conditions, call 108 or visit a hospital."

3. For app-related questions (how to use features, navigation, etc.):
   Explain the app features helpfully. The app has: Emergency first-aid guides, 
   AI chatbot, Hospital finder, SOS calling (108), Learning modules, 
   Myth-busting section, and supports 6 languages.

4. For completely unrelated topics (coding, sports, movies, etc.):
   Politely say: "I'm Pratham Chikitse's health assistant! I specialize 
   in first aid and health topics. Try asking me about health or first aid! 😊"

5. Respond in the SAME language the user writes in. 
   Supported: English, Kannada, Hindi, Gujarati, Marathi, Tamil.

6. Be warm, helpful, and human-like. Avoid robotic responses.
"""
    }
}
