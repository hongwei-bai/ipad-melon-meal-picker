package com.melon.mealpicker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.melon.mealpicker.data.EffortLevel
import com.melon.mealpicker.data.Meal
import com.melon.mealpicker.data.MealType
import com.melon.mealpicker.data.NutritionInfo
import com.melon.mealpicker.data.NutritionScore
import com.melon.mealpicker.data.SchoolLunchSuitability
import com.melon.mealpicker.media.ImageSourceType
import com.melon.mealpicker.media.rememberImagePickerLauncher
import com.melon.mealpicker.storage.ImageStorage
import com.melon.mealpicker.ui.components.StarRatingBar
import com.melon.mealpicker.ui.theme.CoralPrimary
import kotlinx.datetime.Clock
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MealEditorSheet(
    initialMeal: Meal?,
    onDismiss: () -> Unit,
    onSave: (meal: Meal, newImageBytes: ByteArray?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Form states
    var name by remember(initialMeal) { mutableStateOf(initialMeal?.name ?: "") }
    var rating by remember(initialMeal) { mutableIntStateOf(initialMeal?.rating ?: 5) }
    var selectedMealTypes by remember(initialMeal) {
        mutableStateOf(initialMeal?.mealTypes ?: setOf(MealType.DINNER))
    }
    var cookingTimeMinutesStr by remember(initialMeal) {
        mutableStateOf(initialMeal?.cookingTimeMinutes?.toString() ?: "20")
    }
    var effort by remember(initialMeal) { mutableStateOf(initialMeal?.effort ?: EffortLevel.EASY) }
    var schoolSuitability by remember(initialMeal) {
        mutableStateOf(initialMeal?.schoolSuitability ?: SchoolLunchSuitability.PERFECT)
    }

    // Ingredients
    val ingredients = remember(initialMeal) {
        mutableStateListOf<String>().apply {
            addAll(initialMeal?.ingredients ?: emptyList())
        }
    }
    var ingredientInput by remember { mutableStateOf("") }

    // Custom Tags
    val tags = remember(initialMeal) {
        mutableStateListOf<String>().apply {
            addAll(initialMeal?.tags ?: emptyList())
        }
    }
    var tagInput by remember { mutableStateOf("") }

    // Nutrition
    var nutritionScore by remember(initialMeal) {
        mutableStateOf(initialMeal?.nutrition?.score ?: NutritionScore.BALANCED)
    }
    var selectedHighlights by remember(initialMeal) {
        mutableStateOf(initialMeal?.nutrition?.highlights ?: emptySet())
    }
    var showMacros by remember { mutableStateOf(initialMeal?.nutrition?.calories != null) }
    var caloriesStr by remember(initialMeal) {
        mutableStateOf(initialMeal?.nutrition?.calories?.toString() ?: "")
    }
    var proteinStr by remember(initialMeal) {
        mutableStateOf(initialMeal?.nutrition?.proteinGrams?.toString() ?: "")
    }
    var carbsStr by remember(initialMeal) {
        mutableStateOf(initialMeal?.nutrition?.carbsGrams?.toString() ?: "")
    }
    var fatStr by remember(initialMeal) {
        mutableStateOf(initialMeal?.nutrition?.fatGrams?.toString() ?: "")
    }
    var fiberStr by remember(initialMeal) {
        mutableStateOf(initialMeal?.nutrition?.fiberGrams?.toString() ?: "")
    }

    // Photo
    var pickedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    val pickerLauncher = rememberImagePickerLauncher { bytes ->
        pickedImageBytes = bytes
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun addIngredientFromInput() {
        val trimmed = ingredientInput.trim().trimEnd(',')
        if (trimmed.isNotBlank() && trimmed !in ingredients) {
            ingredients.add(trimmed)
            ingredientInput = ""
        }
    }

    fun addTagFromInput() {
        val trimmed = tagInput.trim().removePrefix("#")
        if (trimmed.isNotBlank() && trimmed !in tags) {
            tags.add(trimmed)
            tagInput = ""
        }
    }

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (initialMeal == null) "Add New Dish" else "Edit Dish",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2022)
                    )
                )

                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color(0xFF6C757D))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Photo Preview & Capture Buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF1F3F5)),
                contentAlignment = Alignment.Center
            ) {
                val absoluteImagePath = remember(initialMeal?.imagePath) {
                    initialMeal?.imagePath?.let { ImageStorage.getAbsolutePath(it) }
                }

                if (pickedImageBytes != null) {
                    // Display newly picked image representation
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("📸", fontSize = 48.sp)
                        Text(
                            "New photo selected (${pickedImageBytes?.size?.div(1024)} KB)",
                            fontSize = 13.sp,
                            color = CoralPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else if (!absoluteImagePath.isNullOrBlank()) {
                    AsyncImage(
                        model = absoluteImagePath,
                        contentDescription = "Dish preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("📷", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Take or choose a dish photo",
                            fontSize = 13.sp,
                            color = Color(0xFF6C757D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Camera / Gallery Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { pickerLauncher.launch(ImageSourceType.CAMERA) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2D42)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📷 Camera", fontSize = 14.sp)
                }

                OutlinedButton(
                    onClick = { pickerLauncher.launch(ImageSourceType.GALLERY) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🖼️ Photos", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Dish Name Field (Required)
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (errorMessage != null) errorMessage = null
                },
                label = { Text("Dish Name *") },
                placeholder = { Text("e.g., Teriyaki Chicken Bowl") },
                isError = errorMessage != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Meal Types Multi-Select
            Text(text = "Meal Type (Multi-select)", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MealType.entries.forEach { type ->
                    val selected = type in selectedMealTypes
                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedMealTypes = if (selected) {
                                if (selectedMealTypes.size > 1) selectedMealTypes - type else selectedMealTypes
                            } else {
                                selectedMealTypes + type
                            }
                        },
                        label = { Text("${type.icon} ${type.label}") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Star Rating (1 to 5)
            Text(text = "Rating ($rating / 5 Stars)", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(6.dp))
            StarRatingBar(
                rating = rating,
                onRatingChanged = { rating = it },
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // Cooking Time & Effort
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = cookingTimeMinutesStr,
                    onValueChange = { cookingTimeMinutesStr = it.filter { char -> char.isDigit() } },
                    label = { Text("Cooking Time (min)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                Column(modifier = Modifier.weight(1.5f)) {
                    Text(text = "Effort Level", fontSize = 13.sp, color = Color(0xFF6C757D))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        EffortLevel.entries.forEach { eff ->
                            val selected = effort == eff
                            FilterChip(
                                selected = selected,
                                onClick = { effort = eff },
                                label = { Text(eff.label.split(" ").first(), fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // School Lunch Suitability
            Text(text = "School Lunch Suitability", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SchoolLunchSuitability.entries.forEach { suit ->
                    val selected = schoolSuitability == suit
                    FilterChip(
                        selected = selected,
                        onClick = { schoolSuitability = suit },
                        label = { Text(suit.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // Ingredients Generator
            Text(text = "Ingredients", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = ingredientInput,
                    onValueChange = {
                        ingredientInput = it
                        if (it.endsWith(",") || it.endsWith("\n")) {
                            addIngredientFromInput()
                        }
                    },
                    placeholder = { Text("Type ingredient & press Add") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { addIngredientFromInput() })
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { addIngredientFromInput() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Add")
                }
            }

            if (ingredients.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ingredients.forEach { item ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFE9ECEF))
                                .clickable { ingredients.remove(item) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "$item ✕", fontSize = 13.sp, color = Color(0xFF495057))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // Nutrition Profile
            Text(text = "Nutrition Score", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutritionScore.entries.forEach { score ->
                    val selected = nutritionScore == score
                    FilterChip(
                        selected = selected,
                        onClick = { nutritionScore = score },
                        label = { Text(score.badge) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Highlights
            Text(text = "Nutrition Highlights", fontSize = 14.sp, color = Color(0xFF495057))
            Spacer(modifier = Modifier.height(6.dp))
            val highlightOptions = listOf("High Protein", "Veggie Loaded", "Whole Grain", "Dairy-Free", "Low Sugar", "Fiber Rich", "Brain Food")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                highlightOptions.forEach { opt ->
                    val selected = opt in selectedHighlights
                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedHighlights = if (selected) {
                                selectedHighlights - opt
                            } else {
                                selectedHighlights + opt
                            }
                        },
                        label = { Text(opt) }
                    )
                }
            }

            // Macros foldable section
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showMacros = !showMacros }
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = if (showMacros) "▼ Hide Numeric Macros" else "▶ Optional: Enter Numeric Macros",
                    fontSize = 13.sp,
                    color = CoralPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            AnimatedVisibility(visible = showMacros) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = caloriesStr,
                            onValueChange = { caloriesStr = it.filter { c -> c.isDigit() } },
                            label = { Text("Calories") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = proteinStr,
                            onValueChange = { proteinStr = it.filter { c -> c.isDigit() } },
                            label = { Text("Protein (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = carbsStr,
                            onValueChange = { carbsStr = it.filter { c -> c.isDigit() } },
                            label = { Text("Carbs (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = fatStr,
                            onValueChange = { fatStr = it.filter { c -> c.isDigit() } },
                            label = { Text("Fat (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = fiberStr,
                            onValueChange = { fiberStr = it.filter { c -> c.isDigit() } },
                            label = { Text("Fiber (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            // Custom Tags
            Text(text = "Custom Tags", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    placeholder = { Text("e.g. Air Fryer, Mom's Special") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { addTagFromInput() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Tag")
                }
            }

            if (tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE3F2FD))
                                .clickable { tags.remove(tag) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "#$tag ✕", fontSize = 12.sp, color = Color(0xFF1976D2))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Save Button
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Dish name is required"
                        return@Button
                    }

                    val dishId = initialMeal?.id ?: "meal_${Clock.System.now().toEpochMilliseconds()}_${Random.nextInt(1000, 9999)}"
                    val imagePath = initialMeal?.imagePath ?: "meals/$dishId.jpg"
                    val cookingTime = cookingTimeMinutesStr.toIntOrNull()

                    val nutrition = NutritionInfo(
                        score = nutritionScore,
                        highlights = selectedHighlights,
                        calories = caloriesStr.toIntOrNull(),
                        proteinGrams = proteinStr.toIntOrNull(),
                        carbsGrams = carbsStr.toIntOrNull(),
                        fatGrams = fatStr.toIntOrNull(),
                        fiberGrams = fiberStr.toIntOrNull()
                    )

                    val meal = Meal(
                        id = dishId,
                        name = name.trim(),
                        imagePath = imagePath,
                        rating = rating,
                        mealTypes = selectedMealTypes,
                        tags = tags.toList(),
                        historyDates = initialMeal?.historyDates ?: emptyList(),
                        ingredients = ingredients.toList(),
                        cookingTimeMinutes = cookingTime,
                        effort = effort,
                        schoolSuitability = schoolSuitability,
                        nutrition = nutrition,
                        createdAt = initialMeal?.createdAt ?: Clock.System.now().toEpochMilliseconds()
                    )

                    onSave(meal, pickedImageBytes)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
            ) {
                Text(
                    text = if (initialMeal == null) "Create Dish" else "Save Changes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        }
    }
}
