package com.melon.mealpicker.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.melon.mealpicker.data.Meal
import com.melon.mealpicker.storage.ImageStorage

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun MealCard(
    meal: Meal,
    currentEpochMillis: Long,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Thumbnail container (16:9 ratio)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .background(Color(0xFFF1F3F5))
            ) {
                val absoluteImagePath = remember(meal.imagePath) {
                    ImageStorage.getAbsolutePath(meal.imagePath)
                }
                val hasImage = remember(meal.imagePath) {
                    ImageStorage.exists(meal.imagePath)
                }

                if (hasImage) {
                    // Food photo via Coil
                    AsyncImage(
                        model = absoluteImagePath,
                        contentDescription = meal.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback decorative plate emoji background if no local image file
                    val mainIcon = meal.mealTypes.firstOrNull()?.icon ?: "🍲"
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFF8F9FA), Color(0xFFE9ECEF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mainIcon,
                            fontSize = 48.sp
                        )
                    }
                }

                // Top-left meal type badge
                meal.mealTypes.firstOrNull()?.let { type ->
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.TopStart)
                    ) {
                        MealTypePill(mealType = type)
                    }
                }

                // Top-right overflow menu button for easy touch on iPad
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.TopEnd)
                ) {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                    ) {
                        Text("⋮", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("✏️ Edit Dish") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("📋 Duplicate") },
                            onClick = {
                                showMenu = false
                                onDuplicate()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🗑️ Delete", color = Color.Red) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            // Dish Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Dish Name
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF1E2022)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Star rating row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StarRatingBar(rating = meal.rating, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${meal.rating})",
                        fontSize = 12.sp,
                        color = Color(0xFF6C757D),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Badges flow row: Cooking Time, Lunchbox, Nutrition
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    meal.cookingTimeMinutes?.let { minutes ->
                        CookingTimePill(minutes = minutes)
                    }

                    meal.schoolSuitability?.let { suitability ->
                        SchoolSuitabilityBadge(suitability = suitability)
                    }

                    meal.nutrition?.score?.let { score ->
                        NutritionBadge(score = score)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Subtext: "Last served: X days ago" or "Never served"
                val lastServedText = formatLastServed(meal.historyDates, currentEpochMillis)
                Text(
                    text = "Last served: $lastServedText",
                    fontSize = 12.sp,
                    color = if (lastServedText == "Never served") Color(0xFF9E9E9E) else Color(0xFF388E3C),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

fun formatLastServed(historyDates: List<Long>, nowEpochMillis: Long): String {
    if (historyDates.isEmpty()) return "Never served"
    val last = historyDates.maxOrNull() ?: return "Never served"
    val diffMillis = nowEpochMillis - last
    if (diffMillis < 0) return "Just now"
    val days = (diffMillis / (1000L * 60 * 60 * 24)).toInt()
    return when {
        days == 0 -> "Today"
        days == 1 -> "Yesterday"
        else -> "$days days ago"
    }
}
