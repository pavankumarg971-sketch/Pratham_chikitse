package com.example.health.data.model

import com.google.gson.annotations.SerializedName

data class EmergencyData(
    val version: String = "",
    val lastUpdated: String = "",
    val language: String = "en",
    val categories: List<EmergencyCategory> = emptyList()
)

data class EmergencyCategory(
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val severity: String = "",
    val keywords: List<String> = emptyList(),
    val description: String = "",
    @SerializedName("urgencyLevel")
    val urgencyLevel: Int = 1,
    val immediateSteps: List<String> = emptyList(),
    val doList: List<String> = emptyList(),
    val dontList: List<String> = emptyList(),
    val whenToCallEmergency: String = "",
    val preventionTips: List<String> = emptyList()
) {
    val severityLevel: SeverityLevel
        get() = when (severity.lowercase()) {
            "critical" -> SeverityLevel.CRITICAL
            "high" -> SeverityLevel.HIGH
            "medium" -> SeverityLevel.MEDIUM
            else -> SeverityLevel.LOW
        }
}

enum class SeverityLevel(val label: String, val priority: Int) {
    CRITICAL("Critical", 4),
    HIGH("High", 3),
    MEDIUM("Medium", 2),
    LOW("Low", 1)
}
