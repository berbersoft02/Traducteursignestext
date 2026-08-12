package com.berbersoft.traducteursignestext.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.berbersoft.traducteursignestext.models.Particle
import kotlin.random.Random

@Composable
fun DreamyBackground(theme: String = "dreamy") {
    if (theme == "classic_blue") {
        // Classic Blue Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A237E),
                            Color(0xFF0D47A1),
                            Color(0xFF0277BD),
                            Color(0xFF0288D1)
                        )
                    )
                )
        ) {
            // Effet de particules (plus subtil pour le classique)
            val particles = remember {
                List(30) {
                    Particle(
                        initialX = Random.nextFloat() * 1000,
                        initialY = Random.nextFloat() * 2000,
                        radius = Random.nextFloat() * 1.5f + 0.5f,
                        speed = Random.nextFloat() * 0.3f + 0.1f
                    )
                }
            }
            SharedParticleEffect(particles)
        }
    } else {
        // Dreamy Dark Aurora Background
        val infiniteTransition = rememberInfiniteTransition(label = "background")
        
        val auroraOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(60000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "aurora"
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF050A18))
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                
                // Couche 1: Bleu mystique (Diagonale)
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1565C0).copy(alpha = 0.1f), 
                            Color.Transparent,
                            Color.Transparent
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(width, height * 0.8f + auroraOffset)
                    )
                )
                
                // Couche 2: Violet profond (Venant du bas)
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent, 
                            Color.Transparent,
                            Color(0xFF7B1FA2).copy(alpha = 0.08f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(width, height)
                    )
                )
                
                // Couche 3: Légère lumière Cyan mouvante (Verticale)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF00ACC1).copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        startY = auroraOffset - 500f,
                        endY = auroraOffset + height
                    )
                )
            }
            
            val particles = remember {
                List(30) {
                    Particle(
                        initialX = Random.nextFloat() * 1000,
                        initialY = Random.nextFloat() * 2000,
                        radius = Random.nextFloat() * 1.5f + 0.5f,
                        speed = Random.nextFloat() * 0.3f + 0.1f
                    )
                }
            }
            SharedParticleEffect(particles)
        }
    }
}

@Composable
fun SharedParticleEffect(particles: List<Particle>) {
    val infiniteTransition = rememberInfiniteTransition(label = "particleTransition")
    
    val particleAnimations = particles.map { particle ->
        val xOffset by infiniteTransition.animateFloat(
            initialValue = particle.initialX,
            targetValue = particle.initialX + 50,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 8000 + Random.nextInt(4000),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "xOffset"
        )
        
        val yOffset by infiniteTransition.animateFloat(
            initialValue = -100f,
            targetValue = 2000f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 15000 + (particle.speed * 20000).toInt(),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "yOffset"
        )
        
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2000 + Random.nextInt(2000),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
        
        Triple(xOffset, (yOffset + particle.initialY) % 2200 - 200, scale)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEachIndexed { index, particle ->
            val (x, y, scale) = particleAnimations[index]
            
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = particle.radius * scale * 4,
                center = Offset(x, y)
            )
            
            drawCircle(
                color = Color.White.copy(alpha = 0.2f * scale),
                radius = particle.radius * scale,
                center = Offset(x, y)
            )
        }
    }
}
