package com.melon.mealpicker.data

import com.melon.mealpicker.storage.ImageStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mealquest.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

class MealRepository(
    private val mealDao: MealDao
) {
    fun getAllMealsFlow(): Flow<List<Meal>> {
        return mealDao.getAllMealsFlow().map { entities ->
            entities.map { MealMappers.toDomain(it) }
        }
    }

    suspend fun getMealById(id: String): Meal? {
        val entity = mealDao.getMealById(id) ?: return null
        return MealMappers.toDomain(entity)
    }

    suspend fun upsertMeal(meal: Meal) {
        val entity = MealMappers.toEntity(meal)
        mealDao.upsertMeal(entity)
    }

    suspend fun deleteMeal(meal: Meal) {
        // Delete from Room SQLite
        val entity = MealMappers.toEntity(meal)
        mealDao.deleteMeal(entity)

        // Unlink local image file via Okio to avoid orphan files
        if (meal.imagePath.isNotBlank()) {
            ImageStorage.deleteMealImage(meal.imagePath)
        }
    }

    suspend fun recordMealServed(mealId: String, timestamp: Long) {
        val existing = getMealById(mealId) ?: return
        val updatedHistory = existing.historyDates + timestamp
        val updated = existing.copy(historyDates = updatedHistory)
        upsertMeal(updated)
    }

    @OptIn(ExperimentalResourceApi::class)
    suspend fun seedSampleDataIfEmpty() {
        val sampleMeals = MealSeedData.createSampleMeals()
        for (meal in sampleMeals) {
            val existing = mealDao.getMealById(meal.id)
            if (existing == null) {
                upsertMeal(meal)
            }
        }

        // Unpack starter dish photos to sandbox if not already present
        for (asset in MealSeedData.starterAssets) {
            if (!ImageStorage.exists(asset.diskPath)) {
                try {
                    val bytes = Res.readBytes(asset.resourcePath)
                    ImageStorage.saveFile(asset.diskPath, bytes)
                } catch (e: Exception) {
                    // Ignored in test environment
                }
            }
        }
    }
}
