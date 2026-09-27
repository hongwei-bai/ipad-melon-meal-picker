package com.melon.mealpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.melon.mealpicker.data.MealRepository
import com.melon.mealpicker.data.createMealDatabase
import com.melon.mealpicker.data.getDatabaseBuilder
import com.melon.mealpicker.ui.screens.CatalogScreen
import com.melon.mealpicker.ui.theme.MealQuestTheme
import com.melon.mealpicker.viewmodel.MealCatalogViewModel

@Composable
fun App() {
    val database = remember {
        val builder = getDatabaseBuilder()
        createMealDatabase(builder)
    }

    val repository = remember {
        MealRepository(database.mealDao())
    }

    val viewModel = remember {
        MealCatalogViewModel(repository)
    }

    MealQuestTheme {
        CatalogScreen(viewModel = viewModel)
    }
}
