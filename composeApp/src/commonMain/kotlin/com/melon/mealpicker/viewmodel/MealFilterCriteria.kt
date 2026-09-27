package com.melon.mealpicker.viewmodel

import com.melon.mealpicker.data.EffortLevel
import com.melon.mealpicker.data.Meal
import com.melon.mealpicker.data.MealType
import com.melon.mealpicker.data.NutritionScore
import com.melon.mealpicker.data.SchoolLunchSuitability

data class MealFilterCriteria(
    val query: String = "",                                  // Substring check on name AND ingredients
    val allowedMealTypes: Set<MealType> = emptySet(),        // OR logic across selected meal types
    val selectedTags: Set<String> = emptySet(),              // AND logic across custom tags
    val minRating: Int = 0,                                  // >= minRating
    val maxCookingTimeMinutes: Int? = null,                  // <= cookingTimeMinutes
    val allowedEfforts: Set<EffortLevel> = emptySet(),       // In selected efforts
    val allowedSchoolSuitabilities: Set<SchoolLunchSuitability> = emptySet(),
    val allowedNutritionScores: Set<NutritionScore> = emptySet(),
    val requiredNutritionHighlights: Set<String> = emptySet(),
    val neverServedOnly: Boolean = false,
    val notServedInDays: Int? = null                         // Filter dishes not eaten in past X days
) {
    fun activeFilterCount(): Int {
        var count = 0
        if (query.isNotBlank()) count++
        if (allowedMealTypes.isNotEmpty()) count++
        if (selectedTags.isNotEmpty()) count += selectedTags.size
        if (minRating > 0) count++
        if (maxCookingTimeMinutes != null) count++
        if (allowedEfforts.isNotEmpty()) count++
        if (allowedSchoolSuitabilities.isNotEmpty()) count++
        if (allowedNutritionScores.isNotEmpty()) count++
        if (requiredNutritionHighlights.isNotEmpty()) count += requiredNutritionHighlights.size
        if (neverServedOnly) count++
        if (notServedInDays != null) count++
        return count
    }

    fun isDefault(): Boolean = activeFilterCount() == 0

    fun matches(meal: Meal, currentEpochMillis: Long): Boolean {
        // Query check: Name OR any ingredient
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            val nameMatch = meal.name.lowercase().contains(q)
            val ingredientMatch = meal.ingredients.any { it.lowercase().contains(q) }
            val tagMatch = meal.tags.any { it.lowercase().contains(q) }
            if (!nameMatch && !ingredientMatch && !tagMatch) {
                return false
            }
        }

        // Meal Type (OR logic across selected meal types)
        if (allowedMealTypes.isNotEmpty()) {
            if (meal.mealTypes.none { it in allowedMealTypes }) {
                return false
            }
        }

        // Tags (AND logic across custom tags)
        if (selectedTags.isNotEmpty()) {
            val mealTagSet = meal.tags.map { it.lowercase() }.toSet()
            if (!selectedTags.all { it.lowercase() in mealTagSet }) {
                return false
            }
        }

        // Min rating
        if (minRating > 0 && meal.rating < minRating) {
            return false
        }

        // Max cooking time
        if (maxCookingTimeMinutes != null) {
            val time = meal.cookingTimeMinutes ?: return false
            if (time > maxCookingTimeMinutes) {
                return false
            }
        }

        // Effort
        if (allowedEfforts.isNotEmpty()) {
            val effort = meal.effort ?: return false
            if (effort !in allowedEfforts) {
                return false
            }
        }

        // School suitability
        if (allowedSchoolSuitabilities.isNotEmpty()) {
            val suit = meal.schoolSuitability ?: return false
            if (suit !in allowedSchoolSuitabilities) {
                return false
            }
        }

        // Nutrition score
        if (allowedNutritionScores.isNotEmpty()) {
            val score = meal.nutrition?.score ?: return false
            if (score !in allowedNutritionScores) {
                return false
            }
        }

        // Nutrition highlights (AND logic)
        if (requiredNutritionHighlights.isNotEmpty()) {
            val highlights = meal.nutrition?.highlights ?: return false
            if (!requiredNutritionHighlights.all { it in highlights }) {
                return false
            }
        }

        // Never served only
        if (neverServedOnly) {
            if (meal.historyDates.isNotEmpty()) {
                return false
            }
        }

        // Not served in days
        if (notServedInDays != null && notServedInDays > 0) {
            val lastServed = meal.historyDates.maxOrNull()
            if (lastServed != null) {
                val daysAgo = (currentEpochMillis - lastServed) / (1000L * 60 * 60 * 24)
                if (daysAgo < notServedInDays) {
                    return false
                }
            }
        }

        return true
    }
}
