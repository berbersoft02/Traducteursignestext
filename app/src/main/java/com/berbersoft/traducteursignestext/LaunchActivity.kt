
package com.berbersoft.traducteursignestext

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.berbersoft.traducteursignestext.ui.theme.TraducteursignestextTheme
import kotlinx.coroutines.delay

class LaunchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TraducteursignestextTheme(darkTheme = true) {
                CleanLaunchScreen(
                    onAnimationFinished = {
                        startActivity(Intent(this, WelcomeActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun CleanLaunchScreen(onAnimationFinished: () -> Unit) {
    // États pour contrôler les animations
    var showGif by remember { mutableStateOf(false) }
    var exitAnimation by remember { mutableStateOf(false) }
    var showUniversityLogo by remember { mutableStateOf(false) }
    
    // Lancer les animations séquentiellement
    LaunchedEffect(key1 = true) {
        delay(300) // Petit délai initial
        showUniversityLogo = true // Afficher le logo de l'université
        delay(800) // Attendre un peu
        showGif = true // Afficher l'animation GIF
        delay(4500) // Attendre plus longtemps pour voir le GIF
        exitAnimation = true // Lancer l'animation de sortie
        delay(600) // Attendre que l'animation de sortie se termine
        onAnimationFinished() // Naviguer vers la page suivante
    }
    
    // Configuration de Coil pour les GIFs
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    // Fond dégradé élégant
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1B3E), // Bleu très foncé
                        Color(0xFF1A237E), // Bleu foncé
                        Color(0xFF283593), // Bleu indigo
                        Color(0xFF1565C0)  // Bleu clair
                    )
                )
            )
    ) {
        // Effet de lumière radiale subtil
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x40304FFE),
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Logo de l'université en haut
        AnimatedVisibility(
            visible = showUniversityLogo,
            enter = fadeIn(
                animationSpec = tween(800, easing = EaseOutQuad)
            ) + slideInVertically(
                animationSpec = tween(1000, easing = EaseOutBack),
                initialOffsetY = { -it }
            ),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Image(
                painter = painterResource(id = R.drawable.universite),
                contentDescription = "Logo de l'université",
                modifier = Modifier
                    .size(300.dp)
                    .padding(top = 0.dp)
            )
        }
        
        // Contenu principal avec animations
        AnimatedVisibility(
            visible = !exitAnimation,
            exit = fadeOut(
                animationSpec = tween(600, easing = EaseInOutQuad)
            ) + scaleOut(
                animationSpec = tween(500, easing = EaseIn),
                targetScale = 0.8f
            ),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Animation GIF Sign Language (Unique élément central)
                AnimatedVisibility(
                    visible = showGif,
                    enter = fadeIn(animationSpec = tween(1500)) + scaleIn(
                        initialScale = 0.85f,
                        animationSpec = tween(1500, easing = EaseOutQuad)
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .height(350.dp) // Plus grand
                            .clip(RoundedCornerShape(16.dp)) // Rectangle arrondi simple
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data(R.drawable.signlanguageanimation)
                                    .size(Size.ORIGINAL)
                                    .build(),
                                imageLoader = imageLoader
                            ),
                            contentDescription = "Sign Language Animation",
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit, // On voit tout
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
} 