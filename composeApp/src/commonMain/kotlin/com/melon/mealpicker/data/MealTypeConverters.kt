package com.melon.mealpicker.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object MealMappers {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun toEntity(meal: Meal): MealEntity {
        return MealEntity(
            id = meal.id,
            name = meal.name,
            imagePath = meal.imagePath,
            rating = meal.rating,
            mealTypesJson = json.encodeToString(meal.mealTypes.map { it.name }),
            tagsJson = json.encodeToString(meal.tags),
            historyDatesJson = json.encodeToString(meal.historyDates),
            ingredientsJson = json.encodeToString(meal.ingredients),
            cookingTimeMinutes = meal.cookingTimeMinutes,
            effort = meal.effort?.name,
            schoolSuitability = meal.schoolSuitability?.name,
            nutritionJson = meal.nutrition?.let { json.encodeToString(it) },
            createdAt = meal.createdAt
        )
    }

    fun toDomain(entity: MealEntity): Meal {
        val mealTypes: Set<MealType> = try {
            val list: List<String> = json.decodeFromString(entity.mealTypesJson)
            list.mapNotNull { name ->
                try { MealType.valueOf(name) } catch (e: Exception) { null }
            }.toSet()
        } catch (e: Exception) {
            emptySet()
        }

        val tags: List<String> = try {
            json.decodeFromString(entity.tagsJson)
        } catch (e: Exception) {
            emptyList()
        }

        val historyDates: List<Long> = try {
            json.decodeFromString(entity.historyDatesJson)
        } catch (e: Exception) {
            emptyList()
        }

        val ingredients: List<String> = try {
            json.decodeFromString(entity.ingredientsJson)
        } catch (e: Exception) {
            emptyList()
        }

        val effort: EffortLevel? = entity.effort?.let {
            try { EffortLevel.valueOf(it) } catch (e: Exception) { null }
        }

        val schoolSuitability: SchoolLunchSuitability? = entity.schoolSuitability?.let {
            try { SchoolLunchSuitability.valueOf(it) } catch (e: Exception) { null }
        }

        val nutrition: NutritionInfo? = entity.nutritionJson?.let {
            try {
                json.decodeFromString<NutritionInfo>(it)
            } catch (e: Exception) {
                null
            }
        }

        return Meal(
            id = entity.id,
            name = entity.name,
            imagePath = entity.imagePath,
            rating = entity.rating,
            mealTypes = mealTypes,
            tags = tags,
            historyDates = historyDates,
            ingredients = ingredients,
            cookingTimeMinutes = entity.cookingTimeMinutes,
            effort = effort,
            schoolSuitability = schoolSuitability,
            nutrition = nutrition,
            createdAt = entity.createdAt
        )
    }
}
