package com.example.health.ui.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health.data.model.EmergencyCategory
import com.example.health.data.repository.EmergencyRepository
import com.example.health.util.TTSManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val emergencyRepo: EmergencyRepository,
    val ttsManager: TTSManager
) : ViewModel() {

    private val _categories = MutableStateFlow<List<EmergencyCategory>>(emptyList())
    val categories: StateFlow<List<EmergencyCategory>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<EmergencyCategory?>(null)
    val selectedCategory: StateFlow<EmergencyCategory?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val bookmarks = emergencyRepo.bookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            emergencyRepo.getCategories().fold(
                onSuccess = {
                    _categories.value = it
                    _isLoading.value = false
                },
                onFailure = {
                    _error.value = it.message
                    _isLoading.value = false
                }
            )
        }
    }

    fun selectCategory(id: String) {
        viewModelScope.launch {
            _selectedCategory.value = emergencyRepo.getCategoryById(id)
            emergencyRepo.addRecent(id)
        }
    }

    fun toggleBookmark(id: String) {
        viewModelScope.launch { emergencyRepo.toggleBookmark(id) }
    }

    fun speakSteps(steps: List<String>, language: String? = null) {
        ttsManager.speakSteps(steps, language)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
