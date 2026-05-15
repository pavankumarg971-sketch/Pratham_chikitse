package com.example.health.data.model

data class HospitalData(
    val version: String = "",
    val lastUpdated: String = "",
    val language: String = "en",
    val hospitals: List<Hospital> = emptyList()
)

data class Hospital(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val type: String = "",
    val specialties: List<String> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val emergencyAvailable: Boolean = false,
    val ambulanceAvailable: Boolean = false,
    val open24x7: Boolean = false,
    val city: String = ""
) {
    fun distanceTo(lat: Double, lng: Double): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(latitude - lat)
        val dLng = Math.toRadians(longitude - lng)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(latitude)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }
}
