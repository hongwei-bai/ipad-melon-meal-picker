package com.melon.mealpicker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imagePath: String,
    val rating: Int,
    val mealTypesJson: String,         // Serialized Set<MealType>
    val tagsJson: String,              // Serialized List<String>
    val historyDatesJson: String,      // Serialized List<Long>
    val ingredientsJson: String,       // Serialized List<String>
    val cookingTimeMinutes: Int?,
    val effort: String?,               // EffortLevel enum name
    val schoolSuitability: String?,    // SchoolLunchSuitability enum name
    val nutritionJson: String?,        // Serialized NutritionInfo
    val createdAt: Long
)

@Dao
interface MealDao {
    @Query("SELECT * FROM meals ORDER BY createdAt DESC")
    fun getAllMealsFlow(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE id = :id LIMIT 1")
    suspend fun getMealById(id: String): MealEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)
}
