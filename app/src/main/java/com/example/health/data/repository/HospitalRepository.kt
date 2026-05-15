package com.example.health.data.repository

import com.example.health.data.local.JsonDataSource
import com.example.health.data.model.Hospital
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HospitalRepository @Inject constructor(
    private val jsonDataSource: JsonDataSource
) {
    private var cachedHospitals: List<Hospital>? = null

    suspend fun getHospitals(): Result<List<Hospital>> {
        cachedHospitals?.let { return Result.success(it) }
        return jsonDataSource.loadHospitals().map { data ->
            data.hospitals.also { cachedHospitals = it }
        }
    }

    fun clearCache() {
        cachedHospitals = null
    }

    suspend fun getHospitalsForLocation(lat: Double, lng: Double): List<Hospital> {
        val hospitals = getHospitals().getOrNull() ?: return emptyList()
        val isBangalore = lat in 12.7..13.2 && lng in 77.4..77.8
        val isKarnataka = lat in 11.5..18.5 && lng in 74.0..78.5
        
        return if (isBangalore) {
            hospitals.filter { it.city.equals("Bangalore", true) }.sortedBy { it.distanceTo(lat, lng) }
        } else if (isKarnataka) {
            hospitals.filter { !it.city.equals("Bangalore", true) }.sortedBy { it.distanceTo(lat, lng) }
        } else {
            hospitals.sortedBy { it.distanceTo(lat, lng) }
        }
    }

    suspend fun getEmergencyHospitals(): List<Hospital> {
        return getHospitals().getOrNull()?.filter { it.emergencyAvailable } ?: emptyList()
    }

    suspend fun searchHospitals(query: String): List<Hospital> {
        val hospitals = getHospitals().getOrNull() ?: return emptyList()
        if (query.isBlank()) return hospitals
        val lq = query.lowercase().trim()
        return hospitals.filter {
            it.name.lowercase().contains(lq) ||
            it.address.lowercase().contains(lq) ||
            it.specialties.any { s -> s.lowercase().contains(lq) } ||
            it.type.lowercase().contains(lq)
        }
    }
}
