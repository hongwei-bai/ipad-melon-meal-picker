package com.melon.mealpicker.data

import kotlinx.datetime.Clock

object MealSeedData {
    data class StarterAsset(val diskPath: String, val resourcePath: String)

    val starterAssets = listOf(
        StarterAsset("meals/waffles.jpg", "drawable/meal_waffles.jpg"),
        StarterAsset("meals/teriyaki_chicken.jpg", "drawable/meal_teriyaki.jpg"),
        StarterAsset("meals/apple_snack.jpg", "drawable/meal_apple.jpg"),
        StarterAsset("meals/rainbow_bento.jpg", "drawable/meal_bento.jpg"),
        StarterAsset("meals/veggie_pasta.jpg", "drawable/meal_pasta.jpg"),
        StarterAsset("meals/salmon_bites.jpg", "drawable/meal_salmon.jpg"),
        StarterAsset("meals/banana_muffins.jpg", "drawable/meal_muffins.jpg"),
        StarterAsset("meals/yogurt_sundae.jpg", "drawable/meal_sundae.jpg")
    )

    fun createSampleMeals(): List<Meal> {
        val now = Clock.System.now().toEpochMilliseconds()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        return listOf(
            Meal(
                id = "sample-waffles-01",
                name = "Homemade Fluffy Waffles",
                imagePath = "meals/waffles.jpg",
                rating = 5,
                mealTypes = setOf(MealType.BREAKFAST),
                tags = listOf("Kid Favorite", "Freezer Friendly", "Weekend Special"),
                historyDates = listOf(now - (3 * oneDayMillis)),
                ingredients = listOf("Flour", "Eggs", "Milk", "Butter", "Baking Powder", "Pure Maple Syrup", "Fresh Berries"),
                cookingTimeMinutes = 20,
                effort = EffortLevel.EASY,
                schoolSuitability = SchoolLunchSuitability.PERFECT,
                nutrition = NutritionInfo(
                    score = NutritionScore.MODERATE,
                    highlights = setOf("Whole Grain", "Low Sugar"),
                    calories = 340,
                    proteinGrams = 9,
                    carbsGrams = 48,
                    fatGrams = 12,
                    fiberGrams = 4
                ),
                createdAt = now - (14 * oneDayMillis)
            ),
            Meal(
                id = "sample-teriyaki-02",
                name = "Teriyaki Chicken Rice Bowl",
                imagePath = "meals/teriyaki_chicken.jpg",
                rating = 4,
                mealTypes = setOf(MealType.LUNCH, MealType.DINNER),
                tags = listOf("Meal Prep", "High Protein"),
                historyDates = listOf(now - oneDayMillis), // Yesterday
                ingredients = listOf("Chicken Breast", "Broccoli", "Carrots", "Soy Sauce", "Honey", "Garlic", "Jasmine Rice"),
                cookingTimeMinutes = 35,
                effort = EffortLevel.MEDIUM,
                schoolSuitability = SchoolLunchSuitability.ACCEPTABLE,
                nutrition = NutritionInfo(
                    score = NutritionScore.BALANCED,
                    highlights = setOf("High Protein", "Veggie Loaded"),
                    calories = 480,
                    proteinGrams = 38,
                    carbsGrams = 56,
                    fatGrams = 10,
                    fiberGrams = 6
                ),
                createdAt = now - (12 * oneDayMillis)
            ),
            Meal(
                id = "sample-apple-03",
                name = "Apple & Nut Butter Bites",
                imagePath = "meals/apple_snack.jpg",
                rating = 5,
                mealTypes = setOf(MealType.SNACK, MealType.BREAKFAST),
                tags = listOf("Quick 5m", "Energy Boost"),
                historyDates = emptyList(), // Never served
                ingredients = listOf("Honeycrisp Apple", "Almond Butter", "Chia Seeds", "Ground Cinnamon"),
                cookingTimeMinutes = 5,
                effort = EffortLevel.EASY,
                schoolSuitability = SchoolLunchSuitability.PERFECT,
                nutrition = NutritionInfo(
                    score = NutritionScore.BALANCED,
                    highlights = setOf("Fiber Rich", "Dairy-Free", "Low Sugar"),
                    calories = 190,
                    proteinGrams = 5,
                    carbsGrams = 22,
                    fatGrams = 11,
                    fiberGrams = 5
                ),
                createdAt = now - (10 * oneDayMillis)
            ),
            Meal(
                id = "sample-bento-04",
                name = "Rainbow Bento Box",
                imagePath = "meals/rainbow_bento.jpg",
                rating = 5,
                mealTypes = setOf(MealType.LUNCH),
                tags = listOf("School Lunch", "Colorful", "Finger Food"),
                historyDates = listOf(now - (5 * oneDayMillis)),
                ingredients = listOf("Turkey Breast Rolls", "Cheddar Cubes", "Baby Carrots", "Cherry Tomatoes", "Cucumber Slices", "Hummus Dip", "Grapes"),
                cookingTimeMinutes = 15,
                effort = EffortLevel.EASY,
                schoolSuitability = SchoolLunchSuitability.PERFECT,
                nutrition = NutritionInfo(
                    score = NutritionScore.BALANCED,
                    highlights = setOf("Veggie Loaded", "High Protein", "Whole Grain"),
                    calories = 360,
                    proteinGrams = 22,
                    carbsGrams = 32,
                    fatGrams = 14,
                    fiberGrams = 7
                ),
                createdAt = now - (8 * oneDayMillis)
            ),
            Meal(
                id = "sample-pasta-05",
                name = "Cheesy Veggie Pasta Bake",
                imagePath = "meals/veggie_pasta.jpg",
                rating = 4,
                mealTypes = setOf(MealType.DINNER),
                tags = listOf("Comfort Food", "Veggie Hidden", "Batch Cook"),
                historyDates = listOf(now - (6 * oneDayMillis)),
                ingredients = listOf("Penne Pasta", "Zucchini", "Baby Spinach", "Marinara Sauce", "Mozzarella", "Parmesan", "Olive Oil"),
                cookingTimeMinutes = 40,
                effort = EffortLevel.MEDIUM,
                schoolSuitability = SchoolLunchSuitability.ACCEPTABLE,
                nutrition = NutritionInfo(
                    score = NutritionScore.MODERATE,
                    highlights = setOf("Veggie Loaded"),
                    calories = 430,
                    proteinGrams = 18,
                    carbsGrams = 58,
                    fatGrams = 15,
                    fiberGrams = 6
                ),
                createdAt = now - (6 * oneDayMillis)
            ),
            Meal(
                id = "sample-salmon-06",
                name = "Crispy Air-Fryer Salmon",
                imagePath = "meals/salmon_bites.jpg",
                rating = 5,
                mealTypes = setOf(MealType.DINNER),
                tags = listOf("Air Fryer", "Brain Food", "Omega 3"),
                historyDates = listOf(now - (8 * oneDayMillis)),
                ingredients = listOf("Wild Salmon Fillet", "Olive Oil", "Garlic Powder", "Sweet Paprika", "Lemon", "Steamed Edamame"),
                cookingTimeMinutes = 18,
                effort = EffortLevel.EASY,
                schoolSuitability = SchoolLunchSuitability.ACCEPTABLE,
                nutrition = NutritionInfo(
                    score = NutritionScore.BALANCED,
                    highlights = setOf("High Protein", "Brain Food"),
                    calories = 410,
                    proteinGrams = 34,
                    carbsGrams = 8,
                    fatGrams = 26,
                    fiberGrams = 3
                ),
                createdAt = now - (4 * oneDayMillis)
            ),
            Meal(
                id = "sample-muffin-07",
                name = "Banana Oat Mini Muffins",
                imagePath = "meals/banana_muffins.jpg",
                rating = 4,
                mealTypes = setOf(MealType.SNACK, MealType.BREAKFAST, MealType.DESSERT),
                tags = listOf("Baking", "Freezer Stash", "Lunchbox Snack"),
                historyDates = listOf(now - (2 * oneDayMillis)),
                ingredients = listOf("Ripe Bananas", "Rolled Oats", "Eggs", "Cinnamon", "Vanilla", "Dark Chocolate Chips"),
                cookingTimeMinutes = 25,
                effort = EffortLevel.EASY,
                schoolSuitability = SchoolLunchSuitability.PERFECT,
                nutrition = NutritionInfo(
                    score = NutritionScore.MODERATE,
                    highlights = setOf("Whole Grain", "Fiber Rich", "Low Sugar"),
                    calories = 160,
                    proteinGrams = 4,
                    carbsGrams = 24,
                    fatGrams = 5,
                    fiberGrams = 3
                ),
                createdAt = now - (2 * oneDayMillis)
            ),
            Meal(
                id = "sample-sundae-08",
                name = "Greek Yogurt Berry Sundae",
                imagePath = "meals/yogurt_sundae.jpg",
                rating = 5,
                mealTypes = setOf(MealType.DESSERT, MealType.SNACK),
                tags = listOf("Treat Night", "Kid Favorite"),
                historyDates = emptyList(), // Never served
                ingredients = listOf("Greek Yogurt", "Strawberries", "Blueberries", "Honey Drizzle", "Crunchy Granola"),
                cookingTimeMinutes = 5,
                effort = EffortLevel.EASY,
                schoolSuitability = SchoolLunchSuitability.ACCEPTABLE,
                nutrition = NutritionInfo(
                    score = NutritionScore.TREAT,
                    highlights = setOf("High Protein"),
                    calories = 240,
                    proteinGrams = 14,
                    carbsGrams = 32,
                    fatGrams = 6,
                    fiberGrams = 4
                ),
                createdAt = now - oneDayMillis
            )
        )
    }
}
