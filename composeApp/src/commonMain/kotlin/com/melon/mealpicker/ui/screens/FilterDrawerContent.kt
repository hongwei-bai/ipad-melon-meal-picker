package com.melon.mealpicker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.melon.mealpicker.data.EffortLevel
import com.melon.mealpicker.data.NutritionScore
import com.melon.mealpicker.data.SchoolLunchSuitability
import com.melon.mealpicker.ui.theme.CoralPrimary
import com.melon.mealpicker.viewmodel.MealFilterCriteria
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterSheet(
    criteria: MealFilterCriteria,
    availableTags: Set<String>,
    onCriteriaChanged: (MealFilterCriteria) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
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
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Filter Meals",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2022)
                    )
                )

                TextButton(onClick = onReset) {
                    Text("Reset All", color = CoralPrimary, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cooking Time Slider
            Text(
                text = "Max Cooking Time: ${criteria.maxCookingTimeMinutes?.let { "${it}m" } ?: "Any"}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            val currentSliderVal = (criteria.maxCookingTimeMinutes ?: 90).toFloat()
            Slider(
                value = currentSliderVal,
                onValueChange = { newVal ->
                    val rounded = newVal.roundToInt()
                    if (rounded >= 90) {
                        onCriteriaChanged(criteria.copy(maxCookingTimeMinutes = null))
                    } else {
                        onCriteriaChanged(criteria.copy(maxCookingTimeMinutes = rounded))
                    }
                },
                valueRange = 5f..90f,
                steps = 16,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // Effort Multi-Select
            Text(
                text = "Effort Level",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EffortLevel.entries.forEach { effort ->
                    val selected = effort in criteria.allowedEfforts
                    FilterChip(
                        selected = selected,
                        onClick = {
                            val newSet = if (selected) {
                                criteria.allowedEfforts - effort
                            } else {
                                criteria.allowedEfforts + effort
                            }
                            onCriteriaChanged(criteria.copy(allowedEfforts = newSet))
                        },
                        label = { Text(effort.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // School Lunch Suitability
            Text(
                text = "School Lunch Readiness",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SchoolLunchSuitability.entries.forEach { suit ->
                    val selected = suit in criteria.allowedSchoolSuitabilities
                    FilterChip(
                        selected = selected,
                        onClick = {
                            val newSet = if (selected) {
                                criteria.allowedSchoolSuitabilities - suit
                            } else {
                                criteria.allowedSchoolSuitabilities + suit
                            }
                            onCriteriaChanged(criteria.copy(allowedSchoolSuitabilities = newSet))
                        },
                        label = { Text(suit.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // Nutrition Score Multi-Select
            Text(
                text = "Nutrition Score",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutritionScore.entries.forEach { score ->
                    val selected = score in criteria.allowedNutritionScores
                    FilterChip(
                        selected = selected,
                        onClick = {
                            val newSet = if (selected) {
                                criteria.allowedNutritionScores - score
                            } else {
                                criteria.allowedNutritionScores + score
                            }
                            onCriteriaChanged(criteria.copy(allowedNutritionScores = newSet))
                        },
                        label = { Text(score.badge) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // Nutrition Highlights
            val commonHighlights = listOf("High Protein", "Veggie Loaded", "Whole Grain", "Dairy-Free", "Low Sugar", "Fiber Rich", "Brain Food")
            Text(
                text = "Nutrition Highlights",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                commonHighlights.forEach { hl ->
                    val selected = hl in criteria.requiredNutritionHighlights
                    FilterChip(
                        selected = selected,
                        onClick = {
                            val newSet = if (selected) {
                                criteria.requiredNutritionHighlights - hl
                            } else {
                                criteria.requiredNutritionHighlights + hl
                            }
                            onCriteriaChanged(criteria.copy(requiredNutritionHighlights = newSet))
                        },
                        label = { Text(hl) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // Minimum Star Rating
            Text(
                text = "Minimum Star Rating",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0 to "Any", 3 to "3★+", 4 to "4★+", 5 to "5★").forEach { (stars, label) ->
                    val selected = criteria.minRating == stars
                    FilterChip(
                        selected = selected,
                        onClick = {
                            onCriteriaChanged(criteria.copy(minRating = stars))
                        },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // Meal History Filters: "Never served only" & "Not eaten in past X days"
            Text(
                text = "History & Recency",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Show only 'Never Served'", fontSize = 14.sp)
                Switch(
                    checked = criteria.neverServedOnly,
                    onCheckedChange = {
                        onCriteriaChanged(criteria.copy(neverServedOnly = it))
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Not eaten in past days", fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(null to "Off", 3 to "3d", 7 to "7d", 14 to "14d").forEach { (days, lbl) ->
                        val selected = criteria.notServedInDays == days
                        FilterChip(
                            selected = selected,
                            onClick = {
                                onCriteriaChanged(criteria.copy(notServedInDays = days))
                            },
                            label = { Text(lbl, fontSize = 12.sp) }
                        )
                    }
                }
            }

            if (availableTags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Custom Tags",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableTags.forEach { tag ->
                        val selected = tag in criteria.selectedTags
                        FilterChip(
                            selected = selected,
                            onClick = {
                                val newSet = if (selected) {
                                    criteria.selectedTags - tag
                                } else {
                                    criteria.selectedTags + tag
                                }
                                onCriteriaChanged(criteria.copy(selectedTags = newSet))
                            },
                            label = { Text("#$tag") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Done / Apply Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
            ) {
                Text("Done (${criteria.activeFilterCount()} active)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
