package com.melon.mealpicker

import com.melon.mealpicker.data.EffortLevel
import com.melon.mealpicker.data.Meal
import com.melon.mealpicker.data.MealMappers
import com.melon.mealpicker.data.MealType
import com.melon.mealpicker.data.NutritionInfo
import com.melon.mealpicker.data.NutritionScore
import com.melon.mealpicker.data.SchoolLunchSuitability
import com.melon.mealpicker.viewmodel.MealFilterCriteria
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MealFilterTest {

    private val now = 1700000000000L
    private val oneDayMillis = 24 * 60 * 60 * 1000L

    private val testMeal1 = Meal(
        id = "m1",
        name = "Homemade Waffles",
        imagePath = "meals/m1.jpg",
        rating = 5,
        mealTypes = setOf(MealType.BREAKFAST),
        tags = listOf("Kid Favorite", "Quick"),
        historyDates = listOf(now - (3 * oneDayMillis)),
        ingredients = listOf("Flour", "Eggs", "Milk", "Maple Syrup"),
        cookingTimeMinutes = 20,
        effort = EffortLevel.EASY,
        schoolSuitability = SchoolLunchSuitability.PERFECT,
        nutrition = NutritionInfo(
            score = NutritionScore.MODERATE,
            highlights = setOf("Low Sugar")
        ),
        createdAt = now
    )

    private val testMeal2 = Meal(
        id = "m2",
        name = "Teriyaki Chicken",
        imagePath = "meals/m2.jpg",
        rating = 4,
        mealTypes = setOf(MealType.DINNER, MealType.LUNCH),
        tags = listOf("Meal Prep", "High Protein"),
        historyDates = listOf(now - (1 * oneDayMillis)),
        ingredients = listOf("Chicken Breast", "Broccoli", "Soy Sauce", "Rice"),
        cookingTimeMinutes = 40,
        effort = EffortLevel.MEDIUM,
        schoolSuitability = SchoolLunchSuitability.ACCEPTABLE,
        nutrition = NutritionInfo(
            score = NutritionScore.BALANCED,
            highlights = setOf("High Protein", "Veggie Loaded")
        ),
        createdAt = now
    )

    private val testMeal3 = Meal(
        id = "m3",
        name = "Apple & Nut Butter",
        imagePath = "meals/m3.jpg",
        rating = 5,
        mealTypes = setOf(MealType.SNACK),
        tags = listOf("Quick"),
        historyDates = emptyList(), // Never served
        ingredients = listOf("Apple", "Almond Butter"),
        cookingTimeMinutes = 5,
        effort = EffortLevel.EASY,
        schoolSuitability = SchoolLunchSuitability.PERFECT,
        nutrition = NutritionInfo(
            score = NutritionScore.BALANCED,
            highlights = setOf("Fiber Rich")
        ),
        createdAt = now
    )

    @Test
    fun testSearchQueryMatchesNameAndIngredients() {
        val nameQuery = MealFilterCriteria(query = "waffles")
        assertTrue(nameQuery.matches(testMeal1, now))
        assertFalse(nameQuery.matches(testMeal2, now))

        val ingredientQuery = MealFilterCriteria(query = "Broccoli")
        assertFalse(ingredientQuery.matches(testMeal1, now))
        assertTrue(ingredientQuery.matches(testMeal2, now))

        val partialQuery = MealFilterCriteria(query = "butt")
        assertTrue(partialQuery.matches(testMeal3, now))
    }

    @Test
    fun testMealTypeFilterOrLogic() {
        val breakfastOrSnack = MealFilterCriteria(
            allowedMealTypes = setOf(MealType.BREAKFAST, MealType.SNACK)
        )
        assertTrue(breakfastOrSnack.matches(testMeal1, now))
        assertFalse(breakfastOrSnack.matches(testMeal2, now))
        assertTrue(breakfastOrSnack.matches(testMeal3, now))
    }

    @Test
    fun testCookingTimeAndEffortFilter() {
        val quickOnly = MealFilterCriteria(maxCookingTimeMinutes = 20)
        assertTrue(quickOnly.matches(testMeal1, now))
        assertFalse(quickOnly.matches(testMeal2, now))
        assertTrue(quickOnly.matches(testMeal3, now))

        val easyOnly = MealFilterCriteria(allowedEfforts = setOf(EffortLevel.EASY))
        assertTrue(easyOnly.matches(testMeal1, now))
        assertFalse(easyOnly.matches(testMeal2, now))
        assertTrue(easyOnly.matches(testMeal3, now))
    }

    @Test
    fun testSchoolSuitabilityFilter() {
        val perfectForLunchbox = MealFilterCriteria(
            allowedSchoolSuitabilities = setOf(SchoolLunchSuitability.PERFECT)
        )
        assertTrue(perfectForLunchbox.matches(testMeal1, now))
        assertFalse(perfectForLunchbox.matches(testMeal2, now))
        assertTrue(perfectForLunchbox.matches(testMeal3, now))
    }

    @Test
    fun testNutritionScoreAndHighlightsFilter() {
        val healthyHighProtein = MealFilterCriteria(
            allowedNutritionScores = setOf(NutritionScore.BALANCED),
            requiredNutritionHighlights = setOf("High Protein")
        )
        assertFalse(healthyHighProtein.matches(testMeal1, now))
        assertTrue(healthyHighProtein.matches(testMeal2, now))
        assertFalse(healthyHighProtein.matches(testMeal3, now))
    }

    @Test
    fun testNeverServedFilter() {
        val neverServed = MealFilterCriteria(neverServedOnly = true)
        assertFalse(neverServed.matches(testMeal1, now))
        assertFalse(neverServed.matches(testMeal2, now))
        assertTrue(neverServed.matches(testMeal3, now))
    }

    @Test
    fun testNotServedInDaysFilter() {
        // testMeal1 served 3 days ago, testMeal2 served 1 day ago, testMeal3 never served
        val notServedIn2Days = MealFilterCriteria(notServedInDays = 2)
        assertTrue(notServedIn2Days.matches(testMeal1, now))
        assertFalse(notServedIn2Days.matches(testMeal2, now))
        assertTrue(notServedIn2Days.matches(testMeal3, now))
    }

    @Test
    fun testMappersSerialization() {
        val entity = MealMappers.toEntity(testMeal2)
        assertEquals("m2", entity.id)
        assertEquals("Teriyaki Chicken", entity.name)
        assertEquals(EffortLevel.MEDIUM.name, entity.effort)
        assertEquals(SchoolLunchSuitability.ACCEPTABLE.name, entity.schoolSuitability)

        val restored = MealMappers.toDomain(entity)
        assertEquals(testMeal2.id, restored.id)
        assertEquals(testMeal2.name, restored.name)
        assertEquals(testMeal2.mealTypes, restored.mealTypes)
        assertEquals(testMeal2.tags, restored.tags)
        assertEquals(testMeal2.historyDates, restored.historyDates)
        assertEquals(testMeal2.ingredients, restored.ingredients)
        assertEquals(testMeal2.cookingTimeMinutes, restored.cookingTimeMinutes)
        assertEquals(testMeal2.effort, restored.effort)
        assertEquals(testMeal2.schoolSuitability, restored.schoolSuitability)
        assertEquals(testMeal2.nutrition, restored.nutrition)
    }
}
