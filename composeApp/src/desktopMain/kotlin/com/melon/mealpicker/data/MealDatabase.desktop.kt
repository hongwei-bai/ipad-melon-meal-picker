package com.melon.mealpicker.data

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<MealDatabase> {
    val dbFile = File(System.getProperty("user.home"), ".mealquest/meals.db")
    dbFile.parentFile.mkdirs()
    return Room.databaseBuilder<MealDatabase>(
        name = dbFile.absolutePath,
    )
}
