package com.example.health.data.model

data class LearningData(
    val version: String = "",
    val lastUpdated: String = "",
    val language: String = "en",
    val modules: List<LearningModule> = emptyList()
)

data class LearningModule(
    val id: String = "",
    val title: String = "",
    val icon: String = "",
    val category: String = "",
    val content: String = "",
    val estimatedReadTime: Int = 0,
    val difficulty: String = ""
) {
    val difficultyLevel: DifficultyLevel
        get() = when (difficulty.lowercase()) {
            "essential" -> DifficultyLevel.ESSENTIAL
            "beginner" -> DifficultyLevel.BEGINNER
            "intermediate" -> DifficultyLevel.INTERMEDIATE
            "advanced" -> DifficultyLevel.ADVANCED
            else -> DifficultyLevel.BEGINNER
        }
}

enum class DifficultyLevel(val label: String) {
    ESSENTIAL("Essential"),
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced")
}

data class MythsData(
    val version: String = "",
    val lastUpdated: String = "",
    val language: String = "en",
    val myths: List<MythFact> = emptyList()
)

data class MythFact(
    val id: String = "",
    val myth: String = "",
    val fact: String = "",
    val category: String = "",
    val severity: String = ""
)

data class DisclaimerData(
    val version: String = "",
    val lastUpdated: String = "",
    val language: String = "en",
    val disclaimer: DisclaimerInfo = DisclaimerInfo()
)

data class DisclaimerInfo(
    val title: String = "",
    val body: String = "",
    val reviewedBy: String = "",
    val reviewDate: String = "",
    val version: String = "",
    val severity: String = "",
    val acceptanceRequired: Boolean = true
)

data class TriageResult(
    val matchedCategory: EmergencyCategory? = null,
    val urgencyLevel: Int = 0,
    val confidence: Float = 0f,
    val immediateActions: List<String> = emptyList(),
    val doList: List<String> = emptyList(),
    val dontList: List<String> = emptyList(),
    val callEmergency: Boolean = false,
    val message: String = ""
)
