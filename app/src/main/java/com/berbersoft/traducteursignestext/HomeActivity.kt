package com.berbersoft.traducteursignestext

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berbersoft.traducteursignestext.ui.theme.TraducteursignestextTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import java.util.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import com.berbersoft.traducteursignestext.models.Particle
import kotlin.random.Random
import com.berbersoft.traducteursignestext.utils.EaseInOutQuart
import com.berbersoft.traducteursignestext.utils.EaseInOutSine
import com.berbersoft.traducteursignestext.utils.Quadruple
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.draw.blur
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.TextUnit
import kotlin.math.sin
import com.berbersoft.traducteursignestext.ui.theme.DreamyBackground
import com.berbersoft.traducteursignestext.utils.ThemeHelper
import androidx.compose.ui.platform.LocalContext

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Récupérer la langue depuis l'intent
        val language = intent.getStringExtra("language") ?: "fr"
        
        // Mettre à jour la locale
        val locale = when (language) {
            "en" -> Locale.ENGLISH
            "ar" -> Locale("ar")
            "kab" -> Locale("kab")
            else -> Locale.FRENCH
        }
        updateLocale(locale)

        setContent {
            TraducteursignestextTheme(darkTheme = true) {
                HomeScreen(
                    language = language,
                        onStartClick = {
                        val intent = Intent(this, MainActivity::class.java).apply {
                            putExtra("language", language)
                        }
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    private fun updateLocale(locale: Locale) {
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
        applicationContext.resources.updateConfiguration(config, resources.displayMetrics)
    }
}

@Composable
fun HomeScreen(language: String, onStartClick: () -> Unit) {
    val context = LocalContext.current
    val currentTheme = ThemeHelper.getTheme(context)

    // Textes selon la langue
    val title = when (language) {
        "en" -> "Sign Language Translator"
        "ar" -> "مترجم لغة الإشارة"
        "kab" -> "Asemmeslay n tutlayt n yiɣallen"
        else -> "Traducteur Langue des Signes"
    }
    
    val subtitle = when (language) {
        "en" -> "Bridging communication barriers"
        "ar" -> "تجسير حواجز التواصل"
        "kab" -> "Asishel n tmeslay"
        else -> "Franchir les barrières de communication"
    }
    
    val feature1Title = when (language) {
        "en" -> "Real-time Translation"
        "ar" -> "ترجمة فورية"
        "kab" -> "Asegzi di lwaqt"
        else -> "Traduction en temps réel"
    }
    
    val feature1Desc = when (language) {
        "en" -> "Instantly translate sign language gestures into text"
        "ar" -> "ترجمة إشارات لغة الإشارة إلى نص بشكل فوري"
        "kab" -> "Ssuqel imeslayen n tutlayt n isebtaren ɣer uḍris di teswiεt"
        else -> "Traduisez instantanément les gestes de la langue des signes en texte"
    }
    
    val feature2Title = when (language) {
        "en" -> "Intuitive Interface"
        "ar" -> "واجهة بديهية"
        "kab" -> "Tameẓla tadigant"
        else -> "Interface intuitive"
    }
    
    val feature2Desc = when (language) {
        "en" -> "Easy to use for everyone, regardless of experience level"
        "ar" -> "سهلة الاستخدام للجميع، بغض النظر عن مستوى الخبرة"
        "kab" -> "Yeshel iwakken ad t-yexdem yal yiwen, akken yebɣu yili uẓar n tmusni-s"
        else -> "Facile à utiliser pour tous, quel que soit le niveau d'expérience"
    }
    
    val feature3Title = when (language) {
        "en" -> "High Precision"
        "ar" -> "دقة عالية"
        "kab" -> "Taḥeqqit tameqqrant"
        else -> "Haute précision"
    }
    
    val feature3Desc = when (language) {
        "en" -> "Advanced AI technology for accurate gesture recognition"
        "ar" -> "تقنية الذكاء الاصطناعي المتقدمة للتعرف الدقيق على الإشارات"
        "kab" -> "Asexdem n IA akken ad d-yettwaεqel swaswa"
        else -> "Technologie IA avancée pour une reconnaissance précise des gestes"
    }
    
    val startButtonText = when (language) {
        "en" -> "Get Started"
        "ar" -> "ابدأ الآن"
        "kab" -> "evdu"
        else -> "Commencer"
    }
    
    val textDirection = if (language == "ar") TextAlign.Right else TextAlign.Left

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Nouveau fond unifié avec thème
        DreamyBackground(theme = currentTheme)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Logo et titre
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(16.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_app),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Titre avec animation
            Text(
                text = title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Caractéristiques
            FeatureItem(
                icon = R.drawable.ic_feature_realtime,
                title = feature1Title,
                description = feature1Desc,
                textAlign = textDirection
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            FeatureItem(
                icon = R.drawable.ic_feature_interface,
                title = feature2Title,
                description = feature2Desc,
                textAlign = textDirection
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            FeatureItem(
                icon = R.drawable.ic_feature_precision,
                title = feature3Title,
                description = feature3Desc,
                textAlign = textDirection
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Bouton de démarrage
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(16.dp, RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = startButtonText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                            
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FeatureItem(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    textAlign: TextAlign = TextAlign.Left
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Icône avec animation
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(8.dp, CircleShape)
                .background(Color(0xFF3F51B5), CircleShape)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Texte
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = textAlign
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = textAlign
            )
        }
    }
}
