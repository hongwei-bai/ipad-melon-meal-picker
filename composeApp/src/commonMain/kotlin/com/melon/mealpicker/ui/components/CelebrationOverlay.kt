package com.melon.mealpicker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.melon.mealpicker.data.Meal
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun CelebrationOverlay(
    meal: Meal?,
    onDismiss: () -> Unit
) {
    if (meal == null) return

    val progress = remember { Animatable(0f) }

    LaunchedEffect(meal) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing)
        )
        delay(300)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // Confetti particle canvas
        val particles = remember {
            List(40) {
                ConfettiParticle(
                    startX = Random.nextFloat(),
                    startY = Random.nextFloat() * 0.4f,
                    speedX = (Random.nextFloat() - 0.5f) * 600f,
                    speedY = Random.nextFloat() * 800f + 200f,
                    radius = Random.nextFloat() * 8f + 5f,
                    color = listOf(
                        Color(0xFFFF5252),
                        Color(0xFFFFD700),
                        Color(0xFF4CAF50),
                        Color(0xFF2196F3),
                        Color(0xFFFF4081),
                        Color(0xFF9C27B0)
                    ).random()
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val p = progress.value
            val width = size.width
            val height = size.height
            particles.forEach { particle ->
                val x = (particle.startX * width + particle.speedX * p).coerceIn(0f, width)
                val y = (particle.startY * height + particle.speedY * p).coerceIn(0f, height)
                val alpha = (1f - p * 0.7f).coerceIn(0f, 1f)
                drawCircle(
                    color = particle.color.copy(alpha = alpha),
                    radius = particle.radius,
                    center = Offset(x, y)
                )
            }
        }

        // Center card with celebration banner
        Card(
            modifier = Modifier
                .padding(32.dp)
                .clip(RoundedCornerShape(28.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎉 YAY! 🎉",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFF6347)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Selected for Today!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2D42)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = meal.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4A4E69),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                val mainType = meal.mealTypes.firstOrNull()?.icon ?: "🍽️"
                Text(
                    text = mainType,
                    fontSize = 54.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Logged to meal history!",
                    fontSize = 14.sp,
                    color = Color(0xFF6C757D)
                )
            }
        }
    }
}

private data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val speedX: Float,
    val speedY: Float,
    val radius: Float,
    val color: Color
)
