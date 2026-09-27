package com.melon.mealpicker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.melon.mealpicker.data.EffortLevel
import com.melon.mealpicker.data.MealType
import com.melon.mealpicker.data.NutritionScore
import com.melon.mealpicker.data.SchoolLunchSuitability
import com.melon.mealpicker.ui.theme.CoralContainer
import com.melon.mealpicker.ui.theme.CoralDark
import com.melon.mealpicker.ui.theme.LunchboxBlue
import com.melon.mealpicker.ui.theme.LunchboxBlueContainer
import com.melon.mealpicker.ui.theme.SageContainer
import com.melon.mealpicker.ui.theme.SageSecondary
import com.melon.mealpicker.ui.theme.SunnyAccent
import com.melon.mealpicker.ui.theme.SunnyContainer
import com.melon.mealpicker.ui.theme.TreatPurple
import com.melon.mealpicker.ui.theme.TreatPurpleContainer

@Composable
fun NutritionBadge(score: NutritionScore, modifier: Modifier = Modifier) {
    val (bgColor, textColor, text) = when (score) {
        NutritionScore.BALANCED -> Triple(SageContainer, SageSecondary, "🥗 Super Healthy")
        NutritionScore.MODERATE -> Triple(SunnyContainer, SunnyAccent, "🥪 Everyday")
        NutritionScore.TREAT -> Triple(TreatPurpleContainer, TreatPurple, "🧁 Occasional Treat")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SchoolSuitabilityBadge(suitability: SchoolLunchSuitability, modifier: Modifier = Modifier) {
    val (bgColor, textColor, text) = when (suitability) {
        SchoolLunchSuitability.PERFECT -> Triple(LunchboxBlueContainer, LunchboxBlue, "🎒 Lunchbox Ready")
        SchoolLunchSuitability.ACCEPTABLE -> Triple(SunnyContainer, SunnyAccent, "♨️ Needs Thermos")
        SchoolLunchSuitability.NOT_SUITABLE -> Triple(Color(0xFFEEEEEE), Color(0xFF616161), "🏠 Home Only")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CookingTimePill(minutes: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF1F3F5))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "⏱️ ${minutes}m",
            color = Color(0xFF495057),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MealTypePill(mealType: MealType, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CoralContainer)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${mealType.icon} ${mealType.label}",
            color = CoralDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EffortBadge(effort: EffortLevel, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (effort) {
        EffortLevel.EASY -> Pair(SageContainer, SageSecondary)
        EffortLevel.MEDIUM -> Pair(SunnyContainer, SunnyAccent)
        EffortLevel.HIGH -> Pair(CoralContainer, CoralDark)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = effort.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TagChip(tag: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFE9ECEF))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "#$tag",
            fontSize = 10.sp,
            color = Color(0xFF495057),
            fontWeight = FontWeight.Normal
        )
    }
}
