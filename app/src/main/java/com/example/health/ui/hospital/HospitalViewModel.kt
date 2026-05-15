package com.example.health.ui.hospital

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health.data.model.Hospital
import com.example.health.data.repository.HospitalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HospitalViewModel @Inject constructor(
    private val hospitalRepo: HospitalRepository
) : ViewModel() {

    private val _hospitals = MutableStateFlow<List<Hospital>>(emptyList())
    val hospitals: StateFlow<List<Hospital>> = _hospitals.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _userLat = MutableStateFlow(0.0)
    private val _userLng = MutableStateFlow(0.0)
    private val _hasLocation = MutableStateFlow(false)
    val hasLocation: StateFlow<Boolean> = _hasLocation.asStateFlow()

    init {
        loadHospitals()
    }

    private fun loadHospitals() {
        viewModelScope.launch {
            hospitalRepo.getHospitals().fold(
                onSuccess = {
                    _hospitals.value = it
                    _isLoading.value = false
                },
                onFailure = {
                    _error.value = it.message
                    _isLoading.value = false
                }
            )
        }
    }

    fun updateLocation(lat: Double?, lng: Double?) {
        viewModelScope.launch {
            if (lat != null && lng != null) {
                _userLat.value = lat
                _userLng.value = lng
                _hasLocation.value = true
                _hospitals.value = hospitalRepo.getHospitalsForLocation(lat, lng)
            } else {
                // Default fallback to Bangalore
                _hasLocation.value = false
                _hospitals.value = hospitalRepo.getHospitalsForLocation(12.9716, 77.5946)
            }
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _hospitals.value = if (query.isBlank()) {
                if (_hasLocation.value) hospitalRepo.getHospitalsForLocation(_userLat.value, _userLng.value)
                else hospitalRepo.getHospitalsForLocation(12.9716, 77.5946)
            } else {
                hospitalRepo.searchHospitals(query)
            }
        }
    }

    fun getDistanceText(hospital: Hospital): String {
        if (!_hasLocation.value) return ""
        val dist = hospital.distanceTo(_userLat.value, _userLng.value)
        return if (dist < 1) "${(dist * 1000).toInt()}m" else "%.1f km".format(dist)
    }
}
