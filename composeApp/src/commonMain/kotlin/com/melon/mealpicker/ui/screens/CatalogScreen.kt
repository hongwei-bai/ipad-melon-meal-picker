package com.melon.mealpicker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.melon.mealpicker.data.Meal
import com.melon.mealpicker.data.MealType
import com.melon.mealpicker.ui.components.CelebrationOverlay
import com.melon.mealpicker.ui.components.MealCard
import com.melon.mealpicker.ui.theme.CoralContainer
import com.melon.mealpicker.ui.theme.CoralDark
import com.melon.mealpicker.ui.theme.CoralPrimary
import com.melon.mealpicker.viewmodel.MealCatalogViewModel
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: MealCatalogViewModel
) {
    val allMeals by viewModel.allMeals.collectAsState()
    val filteredMeals by viewModel.filteredMeals.collectAsState()
    val filterCriteria by viewModel.filterCriteria.collectAsState()
    val selectedMeal by viewModel.selectedMeal.collectAsState()
    val editingMeal by viewModel.editingMeal.collectAsState()
    val isEditorOpen by viewModel.isEditorOpen.collectAsState()
    val isFilterSheetOpen by viewModel.isFilterSheetOpen.collectAsState()
    val celebratingMeal by viewModel.celebratingMeal.collectAsState()

    val currentEpochMillis = remember { Clock.System.now().toEpochMilliseconds() }
    val gridState = rememberLazyGridState()

    val activeFiltersCount = filterCriteria.activeFilterCount()

    Scaffold(
        containerColor = Color(0xFFF9F9FB)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Application Bar & Controls
                Card(
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        // Title row + Search + Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Logo and Title
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🍉", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "MealQuest",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF1E2022),
                                            fontSize = 22.sp
                                        )
                                    )
                                    Text(
                                        text = "Kids & Family Meal Catalog",
                                        fontSize = 11.sp,
                                        color = Color(0xFF868E96)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            // Search bar (query on dish name AND ingredients)
                            OutlinedTextField(
                                value = filterCriteria.query,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                placeholder = { Text("Search dish or ingredient...", fontSize = 14.sp) },
                                singleLine = true,
                                trailingIcon = {
                                    if (filterCriteria.query.isNotBlank()) {
                                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                            Text("✕", fontSize = 14.sp, color = Color(0xFF6C757D))
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF8F9FA),
                                    unfocusedContainerColor = Color(0xFFF8F9FA),
                                    unfocusedBorderColor = Color(0xFFE9ECEF),
                                    focusedBorderColor = CoralPrimary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Filter Button with active count badge
                            Button(
                                onClick = { viewModel.openFilterSheet() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activeFiltersCount > 0) CoralContainer else Color(0xFFF1F3F5),
                                    contentColor = if (activeFiltersCount > 0) CoralDark else Color(0xFF495057)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Text(
                                    text = if (activeFiltersCount > 0) "⚡ Filter ($activeFiltersCount)" else "⚡ Filter",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // "+ Add Dish" Button
                            Button(
                                onClick = { viewModel.openCreateMeal() },
                                colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Text("+ Add Dish", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sticky Horizontal Row: All, Breakfast, Lunch, Dinner, Snack, Dessert
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // "All" chip
                            val isAllSelected = filterCriteria.allowedMealTypes.isEmpty()
                            FilterChip(
                                selected = isAllSelected,
                                onClick = { viewModel.selectQuickMealType(null) },
                                label = {
                                    Text(
                                        text = "All (${allMeals.size})",
                                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CoralPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )

                            // Meal Type chips
                            MealType.entries.forEach { mealType ->
                                val isSelected = mealType in filterCriteria.allowedMealTypes
                                val count = allMeals.count { mealType in it.mealTypes }
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.selectQuickMealType(mealType) },
                                    label = {
                                        Text(
                                            text = "${mealType.icon} ${mealType.label} ($count)",
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CoralPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // Grid of Meals or Empty state
                if (filteredMeals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🍽️", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No dishes found",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2B2D42)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (activeFiltersCount > 0)
                                    "Try clearing some filters or searching for something else!"
                                else
                                    "Get started by adding your first home-cooked dish!",
                                fontSize = 14.sp,
                                color = Color(0xFF6C757D),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            if (activeFiltersCount > 0) {
                                OutlinedButton(
                                    onClick = { viewModel.resetFilters() },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Reset Filters")
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.openCreateMeal() },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
                                ) {
                                    Text("+ Add Dish", color = Color.White)
                                }
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 220.dp),
                        state = gridState,
                        contentPadding = PaddingValues(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredMeals, key = { it.id }) { meal ->
                            MealCard(
                                meal = meal,
                                currentEpochMillis = currentEpochMillis,
                                onClick = { viewModel.openMealDetail(meal) },
                                onEdit = { viewModel.openEditMeal(meal) },
                                onDuplicate = { viewModel.duplicateMeal(meal) },
                                onDelete = { viewModel.deleteMeal(meal) }
                            )
                        }
                    }
                }
            }

            // Sheets & Overlays
            if (selectedMeal != null) {
                MealDetailSheet(
                    meal = selectedMeal,
                    currentEpochMillis = currentEpochMillis,
                    onDismiss = { viewModel.closeMealDetail() },
                    onIWantThis = { viewModel.onIWantThisTapped(it) },
                    onEdit = { viewModel.openEditMeal(it) }
                )
            }

            if (isFilterSheetOpen) {
                FilterSheet(
                    criteria = filterCriteria,
                    availableTags = viewModel.getAllAvailableTags(),
                    onCriteriaChanged = { viewModel.updateFilterCriteria(it) },
                    onReset = { viewModel.resetFilters() },
                    onDismiss = { viewModel.closeFilterSheet() }
                )
            }

            if (isEditorOpen) {
                MealEditorSheet(
                    initialMeal = editingMeal,
                    onDismiss = { viewModel.closeMealEditor() },
                    onSave = { meal, bytes -> viewModel.saveMeal(meal, bytes) }
                )
            }

            // "I Want This!" Celebration Overlay
            if (celebratingMeal != null) {
                CelebrationOverlay(
                    meal = celebratingMeal,
                    onDismiss = { viewModel.dismissCelebration() }
                )
            }
        }
    }
}
