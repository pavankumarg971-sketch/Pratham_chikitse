package com.example.health.ui.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health.data.model.LearningModule
import com.example.health.data.model.MythFact
import com.example.health.data.repository.LearningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LearningViewModel @Inject constructor(
    private val learningRepo: LearningRepository
) : ViewModel() {

    private val _modules = MutableStateFlow<List<LearningModule>>(emptyList())
    val modules: StateFlow<List<LearningModule>> = _modules.asStateFlow()

    private val _myths = MutableStateFlow<List<MythFact>>(emptyList())
    val myths: StateFlow<List<MythFact>> = _myths.asStateFlow()

    private val _selectedModule = MutableStateFlow<LearningModule?>(null)
    val selectedModule: StateFlow<LearningModule?> = _selectedModule.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            learningRepo.getModules().onSuccess { _modules.value = it }
            learningRepo.getMyths().onSuccess { _myths.value = it }
            _isLoading.value = false
        }
    }

    fun selectModule(id: String) {
        viewModelScope.launch {
            _selectedModule.value = learningRepo.getModuleById(id)
        }
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }
}
