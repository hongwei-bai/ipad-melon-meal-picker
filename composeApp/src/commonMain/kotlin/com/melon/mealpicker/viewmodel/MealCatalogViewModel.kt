package com.melon.mealpicker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melon.mealpicker.data.Meal
import com.melon.mealpicker.data.MealRepository
import com.melon.mealpicker.data.MealType
import com.melon.mealpicker.storage.ImageStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random

class MealCatalogViewModel(
    private val repository: MealRepository
) : ViewModel() {

    private val _filterCriteria = MutableStateFlow(MealFilterCriteria())
    val filterCriteria: StateFlow<MealFilterCriteria> = _filterCriteria.asStateFlow()

    private val _selectedMeal = MutableStateFlow<Meal?>(null)
    val selectedMeal: StateFlow<Meal?> = _selectedMeal.asStateFlow()

    private val _editingMeal = MutableStateFlow<Meal?>(null)
    val editingMeal: StateFlow<Meal?> = _editingMeal.asStateFlow()

    private val _isEditorOpen = MutableStateFlow(false)
    val isEditorOpen: StateFlow<Boolean> = _isEditorOpen.asStateFlow()

    private val _isFilterSheetOpen = MutableStateFlow(false)
    val isFilterSheetOpen: StateFlow<Boolean> = _isFilterSheetOpen.asStateFlow()

    private val _celebratingMeal = MutableStateFlow<Meal?>(null)
    val celebratingMeal: StateFlow<Meal?> = _celebratingMeal.asStateFlow()

    val allMeals: StateFlow<List<Meal>> = repository.getAllMealsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredMeals: StateFlow<List<Meal>> = combine(allMeals, _filterCriteria) { meals, criteria ->
        val now = Clock.System.now().toEpochMilliseconds()
        meals.filter { criteria.matches(it, now) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
        }
    }

    fun updateSearchQuery(query: String) {
        _filterCriteria.value = _filterCriteria.value.copy(query = query)
    }

    /**
     * Toggles top bar sticky chip (e.g. All, Breakfast, Lunch, Dinner, Snack, Dessert).
     * If type is null -> All (clears allowedMealTypes).
     */
    fun selectQuickMealType(type: MealType?) {
        val current = _filterCriteria.value
        val newSet = if (type == null) {
            emptySet()
        } else {
            if (type in current.allowedMealTypes && current.allowedMealTypes.size == 1) {
                emptySet() // toggle back to All
            } else {
                setOf(type)
            }
        }
        _filterCriteria.value = current.copy(allowedMealTypes = newSet)
    }

    fun updateFilterCriteria(criteria: MealFilterCriteria) {
        _filterCriteria.value = criteria
    }

    fun resetFilters() {
        _filterCriteria.value = MealFilterCriteria()
    }

    fun openFilterSheet() {
        _isFilterSheetOpen.value = true
    }

    fun closeFilterSheet() {
        _isFilterSheetOpen.value = false
    }

    fun openMealDetail(meal: Meal) {
        _selectedMeal.value = meal
    }

    fun closeMealDetail() {
        _selectedMeal.value = null
    }

    fun openCreateMeal() {
        _editingMeal.value = null
        _isEditorOpen.value = true
    }

    fun openEditMeal(meal: Meal) {
        _editingMeal.value = meal
        _isEditorOpen.value = true
        _selectedMeal.value = null
    }

    fun duplicateMeal(meal: Meal) {
        val newId = "meal_${Clock.System.now().toEpochMilliseconds()}_${Random.nextInt(1000, 9999)}"
        val copy = meal.copy(
            id = newId,
            name = "${meal.name} (Copy)",
            historyDates = emptyList(),
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        _editingMeal.value = copy
        _isEditorOpen.value = true
    }

    fun closeMealEditor() {
        _editingMeal.value = null
        _isEditorOpen.value = false
    }

    fun saveMeal(meal: Meal, newImageBytes: ByteArray?) {
        viewModelScope.launch {
            val finalMeal = if (newImageBytes != null) {
                val relativePath = ImageStorage.saveMealImage(meal.id, newImageBytes)
                meal.copy(imagePath = relativePath)
            } else {
                meal
            }

            repository.upsertMeal(finalMeal)
            closeMealEditor()

            // Update selected meal if currently open
            if (_selectedMeal.value?.id == finalMeal.id) {
                _selectedMeal.value = finalMeal
            }
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
            if (_selectedMeal.value?.id == meal.id) {
                _selectedMeal.value = null
            }
        }
    }

    fun onIWantThisTapped(meal: Meal) {
        viewModelScope.launch {
            com.melon.mealpicker.platform.playSuccessHaptic()
            val now = Clock.System.now().toEpochMilliseconds()
            repository.recordMealServed(meal.id, now)
            _celebratingMeal.value = meal
            _selectedMeal.value = null
        }
    }

    fun dismissCelebration() {
        _celebratingMeal.value = null
    }

    fun getAllAvailableTags(): Set<String> {
        return allMeals.value.flatMap { it.tags }.toSet()
    }
}
