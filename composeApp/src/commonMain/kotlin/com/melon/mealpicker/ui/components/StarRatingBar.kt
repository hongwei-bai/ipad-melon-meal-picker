package com.melon.mealpicker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.melon.mealpicker.ui.theme.StarGold

@Composable
fun StarRatingBar(
    rating: Int,
    maxStars: Int = 5,
    onRatingChanged: ((Int) -> Unit)? = null,
    fontSize: TextUnit = 14.sp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isFilled = i <= rating
            val starChar = if (isFilled) "★" else "☆"
            val starColor = if (isFilled) StarGold else Color(0xFFCED4DA)

            val clickModifier = if (onRatingChanged != null) {
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onRatingChanged(i)
                }
            } else {
                Modifier
            }

            Text(
                text = starChar,
                color = starColor,
                fontSize = fontSize,
                modifier = clickModifier.padding(horizontal = 1.dp)
            )
        }
    }
}
