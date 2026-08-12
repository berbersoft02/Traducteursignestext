package com.berbersoft.traducteursignestext

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berbersoft.traducteursignestext.ui.theme.TraducteursignestextTheme
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearEasing
import androidx.annotation.DrawableRes
import com.berbersoft.traducteursignestext.utils.EaseInOutSine
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.blur
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import com.berbersoft.traducteursignestext.ui.theme.DreamyBackground
import com.berbersoft.traducteursignestext.utils.ThemeHelper

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TraducteursignestextTheme(darkTheme = true) {
                WelcomeScreen(
                    onLanguageSelected = { language ->
                        // Rediriger vers HomeActivity avec la langue sélectionnée
                        val intent = Intent(this, HomeActivity::class.java).apply {
                            putExtra("language", language)
                        }
                        startActivity(intent)
                        finish() // Fermer WelcomeActivity
                    }
                )
            }
        }
    }
}

@Composable
fun WelcomeScreen(onLanguageSelected: (String) -> Unit) {
    val context = LocalContext.current
    val currentTheme = remember { ThemeHelper.getTheme(context) }
    
    var visible by remember { mutableStateOf(false) }
    var showLangDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Nouveau fond unifié avec thème
        DreamyBackground(theme = currentTheme)

        // Contenu principal avec animation
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(1000)
            ) + slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(1000, easing = EaseOutBack)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo et titre
                LogoSection()
                
                // Texte de sélection de langue avec style amélioré
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 32.dp)
                ) {
                    Text(
                        text = "Select Your Language",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Choisissez Votre Langue",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "اختر لغتك",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }

                // Boutons de langue améliorés
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EnhancedLanguageButton(
                        flagRes = R.drawable.ic_french,
                        text = "Français",
                        onClick = { onLanguageSelected("fr") }
                    )
                    
                    EnhancedLanguageButton(
                        flagRes = R.drawable.ic_english,
                        text = "English",
                        onClick = { onLanguageSelected("en") }
                    )
                    
                    EnhancedLanguageButton(
                        flagRes = R.drawable.ic_algeria,
                        text = "العربية ,    Tamazight",
                        onClick = { showLangDialog = true }
                    )
                }
                if (showLangDialog) {
                    AlertDialog(
                        onDismissRequest = { showLangDialog = false },
                        shape = RoundedCornerShape(24.dp),
                        title = {
                            Text(
                                when (LocalContext.current.resources.configuration.locale.language) {
                                    "en" -> "Choose the language"
                                    "ar" -> "اختر اللغة"
                                    "kab" -> "Fren tutlayt"
                                    else -> "Choisissez la langue"
                                },
                                color = Color(0xFF1976D2),
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        text = {
                            Text(
                                when (LocalContext.current.resources.configuration.locale.language) {
                                    "en" -> "Please choose between Arabic and Tamazight for the app."
                                    "ar" -> "يرجى اختيار العربية أو الأمازيغية للتطبيق."
                                    "kab" -> "Ttxil-k xtir gar taɛrabt d tmaziɣt i useqdec."
                                    else -> "Veuillez choisir entre l'arabe et le tamazight pour l'application."
                                },
                                color = Color.Gray,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            )
                        },
                        containerColor = Color.White,
                        confirmButton = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(
                                    onClick = {
                                        showLangDialog = false
                                        onLanguageSelected("ar")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                                    modifier = Modifier.fillMaxWidth(0.85f).padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(32.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("ع", color = Color(0xFF388E3C), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            when (LocalContext.current.resources.configuration.locale.language) {
                                                "en" -> "Arabic"
                                                "ar" -> "العربية"
                                                "kab" -> "Taɛrabt"
                                                else -> "Arabe"
                                            },
                                            color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold
                                        )
                                        Text("العربية", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        showLangDialog = false
                                        onLanguageSelected("kab")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                                    modifier = Modifier.fillMaxWidth(0.85f).padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(32.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(Color(0xFFFFD600), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("ⵣ", color = Color(0xFF1976D2), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            when (LocalContext.current.resources.configuration.locale.language) {
                                                "en" -> "Kabyle"
                                                "ar" -> "القبايلية"
                                                "kab" -> "Taqbaylit"
                                                else -> "Tamazight"
                                            },
                                            color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold
                                        )
                                        Text("ⵜⴰⵎⴰⵣⵉⵖⵜ", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { showLangDialog = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .padding(top = 8.dp),
                                    shape = RoundedCornerShape(32.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                                ) {
                                    Text(
                                        when (LocalContext.current.resources.configuration.locale.language) {
                                            "en" -> "Cancel"
                                            "ar" -> "إلغاء"
                                            "kab" -> "Sefsex"
                                            else -> "Annuler"
                                        },
                                        color = Color.DarkGray, fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        },
                        dismissButton = {}
                    )
                }

                // Crédit du développeur
                Text(
                    text = "Mouloud Mammeri University ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
fun LogoSection() {
    var scale by remember { mutableStateOf(1f) }
    
    // Animation de flottement pour le logo
    val infiniteTransition = rememberInfiniteTransition()
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0.8f,
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) { value, _ -> scale = value }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 48.dp)
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .offset(y = floatingOffset.dp)
                .scale(scale)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    spotColor = Color(0xFF304FFE).copy(alpha = 0.5f)
                )
                .background(Color.Transparent, CircleShape), // Fond transparent
            contentAlignment = Alignment.Center
        ) {
            // Lueur derrière le logo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(20.dp)
                    .background(Color(0xFF304FFE).copy(alpha = 0.3f), CircleShape)
            )
            
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = null,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Titre avec animation de type machine à écrire
        AnimatedTitle(
            text = "BERBERSOFT",
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun AnimatedTitle(text: String, modifier: Modifier = Modifier) {
    var textToShow by remember { mutableStateOf("") }
    var currentIndex by remember { mutableStateOf(0) }
    
    // Animation de brillance (Shimmer) sur le texte lui-même
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Animation de pulsation
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    LaunchedEffect(text) {
        while (currentIndex < text.length) {
            delay(50)
            textToShow = text.substring(0, currentIndex + 1)
            currentIndex++
        }
    }

    Box(modifier = modifier) {
        // Effet de lueur (Glow)
        Text(
            text = textToShow,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64B5F6).copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            modifier = Modifier
                .scale(scale)
                .blur(radius = 12.dp)
        )

        // Texte principal propre sans fond blanc
        Text(
            text = textToShow,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            style = TextStyle(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFF90CAF9),
                        Color.White
                    ),
                    startX = shimmerTranslate - 200f,
                    endX = shimmerTranslate + 200f,
                    tileMode = androidx.compose.ui.graphics.TileMode.Clamp
                )
            ),
            modifier = Modifier
                .scale(scale)
        )
    }
}

@Composable
fun EnhancedLanguageButton(
    @DrawableRes flagRes: Int,
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animation de survol
    val infiniteTransition = rememberInfiniteTransition(label = "hover")
    val hoverScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(110.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, radius = 80.dp)
            ) { onClick() }
    ) {
        // Effet d'élévation et d'animation au clic
        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer {
                    // Animation combinée : échelle au clic et au survol
                    val pressScale = if (isPressed) 0.95f else 1f
                    scaleX = pressScale * hoverScale
                    scaleY = pressScale * hoverScale
                }
                .shadow(
                    elevation = if (isPressed) 4.dp else 8.dp,
                    shape = CircleShape,
                    spotColor = Color.White.copy(alpha = 0.2f)
                )
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF3F51B5).copy(alpha = 0.9f),
                            Color(0xFF303F9F).copy(alpha = 0.9f)
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.7f),
                            Color.White.copy(alpha = 0.3f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Ajouter un effet de lueur derrière le drapeau
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            // Drapeau avec effet d'ombre
            Image(
                painter = painterResource(id = flagRes),
                contentDescription = text,
                modifier = Modifier
                    .size(80.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Texte avec ombre pour meilleure lisibilité
        Box {
            // Ombre du texte
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = 1.dp, x = 1.dp)
            )
            
            // Texte principal
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0f),
                        Color.White.copy(alpha = 0.2f),
                        Color.White.copy(alpha = 0f)
                    ),
                    start = Offset(translateAnim - 300f, 0f),
                    end = Offset(translateAnim + 300f, 300f)
                )
            )
    ) {
        content()
    }
}
