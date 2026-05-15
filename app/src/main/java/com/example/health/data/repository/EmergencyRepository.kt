package com.example.health.data.repository

import com.example.health.data.local.JsonDataSource
import com.example.health.data.local.UserPreferencesDataStore
import com.example.health.data.model.EmergencyCategory
import com.example.health.data.model.TriageResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class EmergencyRepository
@Inject
constructor(
        private val jsonDataSource: JsonDataSource,
        private val prefsDataStore: UserPreferencesDataStore
) {
    private var cachedCategories: List<EmergencyCategory>? = null

    suspend fun getCategories(): Result<List<EmergencyCategory>> {
        cachedCategories?.let {
            return Result.success(it)
        }
        return jsonDataSource.loadEmergencies().map { data ->
            data.categories.also { cachedCategories = it }
        }
    }

    fun clearCache() {
        cachedCategories = null
    }

    suspend fun getCategoryById(id: String): EmergencyCategory? {
        return cachedCategories?.find { it.id == id }
                ?: getCategories().getOrNull()?.find { it.id == id }
    }

    suspend fun searchCategories(query: String): List<EmergencyCategory> {
        val categories = getCategories().getOrNull() ?: return emptyList()
        if (query.isBlank()) return categories
        val lowerQuery = query.lowercase().trim()
        return categories
                .filter { cat ->
                    cat.name.lowercase().contains(lowerQuery) ||
                            cat.keywords.any { it.lowercase().contains(lowerQuery) } ||
                            cat.description.lowercase().contains(lowerQuery)
                }
                .sortedByDescending { cat ->
                    when {
                        cat.name.lowercase() == lowerQuery -> 100
                        cat.name.lowercase().startsWith(lowerQuery) -> 80
                        cat.keywords.any { it.lowercase() == lowerQuery } -> 70
                        cat.name.lowercase().contains(lowerQuery) -> 50
                        cat.keywords.any { it.lowercase().contains(lowerQuery) } -> 30
                        else -> 10
                    }
                }
    }

    suspend fun triageQuery(query: String): TriageResult {
        val categories =
                getCategories().getOrNull()
                        ?: return TriageResult(
                                message = "Unable to load emergency data. Please restart the app."
                        )
        if (query.isBlank())
                return TriageResult(message = "Please describe the emergency situation.")

        val lowerQuery = query.lowercase().trim()
        val words = lowerQuery.split("\\s+".toRegex())

        var bestMatch: EmergencyCategory? = null
        var bestScore = 0f

        for (category in categories) {
            var score = 0f
            for (keyword in category.keywords) {
                val kw = keyword.lowercase()
                when {
                    lowerQuery == kw -> score += 10f
                    lowerQuery.contains(kw) -> score += 5f
                    words.any { it == kw } -> score += 4f
                    words.any { kw.contains(it) && it.length > 3 } -> score += 2f
                }
            }
            if (category.name.lowercase().let { lowerQuery.contains(it) }) score += 8f
            if (score > bestScore) {
                bestScore = score
                bestMatch = category
            }
        }

        return if (bestMatch != null && bestScore >= 4f) {
            val confidence = (bestScore / 15f).coerceAtMost(1f)
            TriageResult(
                    matchedCategory = bestMatch,
                    urgencyLevel = bestMatch.urgencyLevel,
                    confidence = confidence,
                    immediateActions = bestMatch.immediateSteps.take(5),
                    doList = bestMatch.doList,
                    dontList = bestMatch.dontList,
                    callEmergency = bestMatch.urgencyLevel >= 4,
                    message =
                            if (bestMatch.urgencyLevel >= 4)
                                    "⚠️ This is a CRITICAL emergency. Call 108 immediately!"
                            else "Follow these first-aid steps carefully."
            )
        } else {
            TriageResult(
                    urgencyLevel = 5,
                    callEmergency = true,
                    message =
                            "I couldn't identify the specific emergency. When in doubt, call 108 immediately.",
                    immediateActions =
                            listOf(
                                    "Call 108 or local emergency number",
                                    "Keep the person calm and comfortable",
                                    "Do not move the person unless in danger",
                                    "Monitor breathing and consciousness",
                                    "Wait for professional medical help"
                            )
            )
        }
    }

    val bookmarks: Flow<Set<String>> = prefsDataStore.bookmarks
    val recents: Flow<List<String>> = prefsDataStore.recents
    val viewCounts: Flow<Map<String, Int>> = prefsDataStore.viewCounts

    suspend fun toggleBookmark(id: String) = prefsDataStore.toggleBookmark(id)
    suspend fun addRecent(id: String) {
        prefsDataStore.addRecent(id)
        prefsDataStore.incrementViewCount(id)
    }
}
