package com.melon.mealpicker.data

import kotlinx.serialization.Serializable

enum class MealType(val label: String, val icon: String) {
    BREAKFAST("Breakfast", "🥞"),
    LUNCH("Lunch", "🥪"),
    DINNER("Dinner", "🍲"),
    SNACK("Snack", "🍎"),
    DESSERT("Dessert", "🧁")
}

enum class EffortLevel(val label: String) {
    EASY("Easy / Quick"),
    MEDIUM("Moderate"),
    HIGH("High Effort")
}

enum class SchoolLunchSuitability(val label: String) {
    NOT_SUITABLE("Home Only (Hot / Messy)"),
    ACCEPTABLE("Acceptable / Needs Thermos"),
    PERFECT("Great for Lunchbox (Cold-Friendly)")
}

enum class NutritionScore(val label: String, val badge: String) {
    BALANCED("Balanced / Nutrient-Dense", "🥗 Super Healthy"),
    MODERATE("Moderate / Everyday", "🥪 Everyday"),
    TREAT("Treat / Occasional", "🧁 Occasional Treat")
}

@Serializable
data class NutritionInfo(
    val score: NutritionScore = NutritionScore.MODERATE,
    val highlights: Set<String> = emptySet(), // e.g., ["High Protein", "Veggie Loaded"]
    val calories: Int? = null,
    val proteinGrams: Int? = null,
    val carbsGrams: Int? = null,
    val fatGrams: Int? = null,
    val fiberGrams: Int? = null
)

@Serializable
data class Meal(
    val id: String,                              // UUID string
    val name: String,
    val imagePath: String,                       // Sandboxed relative file path
    val rating: Int = 0,                         // 0-5 stars
    val mealTypes: Set<MealType> = emptySet(),
    val tags: List<String> = emptyList(),
    val historyDates: List<Long> = emptyList(),  // Epoch millis of dates served
    
    // Optional Attributes
    val ingredients: List<String> = emptyList(), // Items needed for grocery shopping
    val cookingTimeMinutes: Int? = null,         // In minutes
    val effort: EffortLevel? = null,
    val schoolSuitability: SchoolLunchSuitability? = null,
    val nutrition: NutritionInfo? = null,
    val createdAt: Long                          // Epoch millis
)
