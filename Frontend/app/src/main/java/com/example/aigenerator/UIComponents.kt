package com.example.aigenerator


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.rotate

@Composable

fun NeonLoadingAnimation() {
    // 1. Create an infinite rotation for the outer ring
    val infiniteTransition = rememberInfiniteTransition(label = "ai_loader")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing)
        ), label = "rotation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Outer Rotating Neon Ring
        Box(
            modifier = Modifier
                .size(60.dp)
                .rotate(angle)
                .border(
                    width = 4.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF6200EA), // Deep Purple
                            Color(0xFF00E5FF), // Cyan
                            Color(0xFF6200EA)  // Back to Purple
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Inner static "AI Core"
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            color = Color(0xFFD0BCFF),
            strokeWidth = 3.dp
        )
    }
}
@Composable
fun ModernGradientButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(), // Remove default padding to let gradient fill
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // Transparent to show gradient
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF6200EA), // Deep Purple
                            Color(0xFFB00020)  // Red/Pink
                        )
                    )
                )
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}