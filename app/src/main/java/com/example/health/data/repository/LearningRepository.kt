package com.example.health.data.repository

import com.example.health.data.local.JsonDataSource
import com.example.health.data.model.DisclaimerInfo
import com.example.health.data.model.LearningModule
import com.example.health.data.model.MythFact
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningRepository @Inject constructor(
    private val jsonDataSource: JsonDataSource
) {
    private var cachedModules: List<LearningModule>? = null
    private var cachedMyths: List<MythFact>? = null
    private var cachedDisclaimer: DisclaimerInfo? = null

    suspend fun getModules(): Result<List<LearningModule>> {
        cachedModules?.let { return Result.success(it) }
        return jsonDataSource.loadLearning().map { data ->
            data.modules.also { cachedModules = it }
        }
    }

    fun clearCache() {
        cachedModules = null
        cachedMyths = null
        cachedDisclaimer = null
    }

    suspend fun getModuleById(id: String): LearningModule? {
        return cachedModules?.find { it.id == id }
            ?: getModules().getOrNull()?.find { it.id == id }
    }

    suspend fun getMyths(): Result<List<MythFact>> {
        cachedMyths?.let { return Result.success(it) }
        return jsonDataSource.loadMyths().map { data ->
            data.myths.also { cachedMyths = it }
        }
    }

    suspend fun getDisclaimer(): Result<DisclaimerInfo> {
        cachedDisclaimer?.let { return Result.success(it) }
        return jsonDataSource.loadDisclaimer().map { data ->
            data.disclaimer.also { cachedDisclaimer = it }
        }
    }
}
