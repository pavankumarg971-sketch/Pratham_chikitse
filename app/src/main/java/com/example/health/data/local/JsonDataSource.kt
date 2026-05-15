package com.example.health.data.local

import android.content.Context
import com.example.health.data.model.DisclaimerData
import com.example.health.data.model.EmergencyData
import com.example.health.data.model.HospitalData
import com.example.health.data.model.LearningData
import com.example.health.data.model.MythsData
import com.example.health.data.repository.UserPreferencesRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPrefsRepo: UserPreferencesRepository
) {
    private val gson = Gson()

    private suspend inline fun <reified T> loadJson(fileName: String): Result<T> {
        return try {
            val lang = userPrefsRepo.language.first()
            val targetFileName = if (lang != "en") fileName.replace(".json", "_${lang}.json") else fileName
            val json = try {
                context.assets.open(targetFileName).bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                context.assets.open(fileName).bufferedReader().use { it.readText() }
            }
            val data = gson.fromJson(json, T::class.java)
            if (data != null) Result.success(data)
            else Result.failure(IllegalStateException("Failed to parse $fileName"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadEmergencies(): Result<EmergencyData> = loadJson("emergencies.json")

    suspend fun loadHospitals(): Result<HospitalData> = loadJson("hospitals.json")

    suspend fun loadLearning(): Result<LearningData> = loadJson("learning.json")

    suspend fun loadMyths(): Result<MythsData> = loadJson("myths.json")

    suspend fun loadDisclaimer(): Result<DisclaimerData> = loadJson("disclaimer.json")
}
