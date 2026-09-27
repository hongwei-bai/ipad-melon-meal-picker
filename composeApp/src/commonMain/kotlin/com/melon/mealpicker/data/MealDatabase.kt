package com.melon.mealpicker.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [MealEntity::class], version = 1, exportSchema = false)
@ConstructedBy(MealDatabaseConstructor::class)
abstract class MealDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MealDatabaseConstructor : RoomDatabaseConstructor<MealDatabase>

expect fun getDatabaseBuilder(): RoomDatabase.Builder<MealDatabase>

fun createMealDatabase(builder: RoomDatabase.Builder<MealDatabase>): MealDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .build()
}
