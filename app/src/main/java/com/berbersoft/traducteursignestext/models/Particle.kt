package com.berbersoft.traducteursignestext.models

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Particle(
    val initialX: Float,
    val initialY: Float,
    val radius: Float,
    val speed: Float = Random.nextFloat() * 2 + 0.5f,
    val color: Color = Color.White.copy(alpha = Random.nextFloat() * 0.5f + 0.1f)
) 