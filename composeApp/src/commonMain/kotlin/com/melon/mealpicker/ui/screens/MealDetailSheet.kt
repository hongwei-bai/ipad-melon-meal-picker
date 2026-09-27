package com.melon.mealpicker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.melon.mealpicker.data.Meal
import com.melon.mealpicker.storage.ImageStorage
import com.melon.mealpicker.ui.components.CookingTimePill
import com.melon.mealpicker.ui.components.EffortBadge
import com.melon.mealpicker.ui.components.MealTypePill
import com.melon.mealpicker.ui.components.NutritionBadge
import com.melon.mealpicker.ui.components.SchoolSuitabilityBadge
import com.melon.mealpicker.ui.components.StarRatingBar
import com.melon.mealpicker.ui.components.TagChip
import com.melon.mealpicker.ui.components.formatLastServed
import com.melon.mealpicker.ui.theme.CoralPrimary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MealDetailSheet(
    meal: Meal?,
    currentEpochMillis: Long,
    onDismiss: () -> Unit,
    onIWantThis: (Meal) -> Unit,
    onEdit: (Meal) -> Unit
) {
    if (meal == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF1F3F5))
            ) {
                val absoluteImagePath = remember(meal.imagePath) {
                    ImageStorage.getAbsolutePath(meal.imagePath)
                }
                val hasImage = remember(meal.imagePath) {
                    ImageStorage.exists(meal.imagePath)
                }

                if (hasImage) {
                    AsyncImage(
                        model = absoluteImagePath,
                        contentDescription = meal.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Large emoji icon fallback
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = meal.mealTypes.firstOrNull()?.icon ?: "🍽️",
                            fontSize = 64.sp
                        )
                    }
                }

                // Edit button in corner
                IconButton(
                    onClick = { onEdit(meal) },
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(20.dp))
                ) {
                    Text("✏️", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dish Title
            Text(
                text = meal.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2022)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Rating & Last Served Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StarRatingBar(rating = meal.rating, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(${meal.rating} / 5)",
                    fontSize = 14.sp,
                    color = Color(0xFF6C757D),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
                val lastServed = formatLastServed(meal.historyDates, currentEpochMillis)
                Text(
                    text = "Served: $lastServed",
                    fontSize = 13.sp,
                    color = if (lastServed == "Never served") Color(0xFF9E9E9E) else Color(0xFF388E3C),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Badges row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                meal.mealTypes.forEach { type ->
                    MealTypePill(mealType = type)
                }
                meal.cookingTimeMinutes?.let {
                    CookingTimePill(minutes = it)
                }
                meal.effort?.let {
                    EffortBadge(effort = it)
                }
                meal.schoolSuitability?.let {
                    SchoolSuitabilityBadge(suitability = it)
                }
            }

            if (meal.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    meal.tags.forEach { tag ->
                        TagChip(tag = tag)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // "I Want This!" Kid Selection Button (Hero Button)
            val mealTargetName = meal.mealTypes.firstOrNull()?.label ?: "Meal"
            Button(
                onClick = { onIWantThis(meal) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoralPrimary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("🌟", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "I Want This for $mealTargetName!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("😋", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ingredients Section
            if (meal.ingredients.isNotEmpty()) {
                Text(
                    text = "🛒 Ingredients Needed",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2D42)
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    meal.ingredients.forEach { item ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F3F5))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "• $item",
                                fontSize = 13.sp,
                                color = Color(0xFF333333),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Nutrition Profile Section
            meal.nutrition?.let { nutrition ->
                Text(
                    text = "🥗 Nutrition Profile",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2D42)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NutritionBadge(score = nutrition.score)
                }

                if (nutrition.highlights.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        nutrition.highlights.forEach { hl ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "✨ $hl",
                                    fontSize = 12.sp,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Macros Card
                val hasMacros = nutrition.calories != null || nutrition.proteinGrams != null ||
                        nutrition.carbsGrams != null || nutrition.fatGrams != null || nutrition.fiberGrams != null
                if (hasMacros) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            nutrition.calories?.let { MacroItem("Calories", "$it", "kcal") }
                            nutrition.proteinGrams?.let { MacroItem("Protein", "${it}g", "macro") }
                            nutrition.carbsGrams?.let { MacroItem("Carbs", "${it}g", "macro") }
                            nutrition.fatGrams?.let { MacroItem("Fat", "${it}g", "macro") }
                            nutrition.fiberGrams?.let { MacroItem("Fiber", "${it}g", "macro") }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(16.dp))
            }

            // History Log Section
            Text(
                text = "📅 Meal History",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2B2D42)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total times served: ${meal.historyDates.size}",
                fontSize = 14.sp,
                color = Color(0xFF6C757D)
            )
        }
    }
}

@Composable
private fun MacroItem(title: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 11.sp, color = Color(0xFF6C757D))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E2022))
    }
}
