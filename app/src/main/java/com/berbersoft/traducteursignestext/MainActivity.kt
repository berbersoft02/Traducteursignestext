package com.berbersoft.traducteursignestext

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.berbersoft.traducteursignestext.ui.theme.TraducteursignestextTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.shadow
import androidx.compose.animation.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import android.widget.Toast
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.animation.AnimatedVisibility
import java.util.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.berbersoft.traducteursignestext.models.Particle
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.hoverable
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.FlipCameraAndroid
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import com.berbersoft.traducteursignestext.ui.theme.DreamyBackground
import com.berbersoft.traducteursignestext.utils.ThemeHelper

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCameraActivity()
        } else {
            Toast.makeText(
                this,
                getLocalizedString(
                    fr = "La permission de la caméra est nécessaire pour cette fonctionnalité",
                    en = "Camera permission is required for this feature",
                    ar = "إذن الكاميرا مطلوب لهذه الميزة"
                ),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private var language by mutableStateOf("fr")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        language = intent.getStringExtra("language") ?: "fr"
        
        val locale = when (language) {
            "en" -> Locale.ENGLISH
            "ar" -> Locale("ar")
            "kab" -> Locale("kab")
            else -> Locale.FRENCH
        }
        updateLocale(locale)

        enableEdgeToEdge()
        setContent {
            TraducteursignestextTheme(
                darkTheme = true
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onCameraRequest = { requestCamera() },
                        language = language,
                        onLanguageChange = { newLanguage -> 
                            val intent = Intent(this, MainActivity::class.java).apply {
                                putExtra("language", newLanguage)
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            }
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }

    private fun requestCamera() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCameraActivity()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(
                    this,
                    getLocalizedString(
                        fr = "La caméra est nécessaire pour la traduction des signes",
                        en = "Camera is required for sign translation",
                        ar = "الكاميرا مطلوبة لترجمة الإشارة"
                    ),
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCameraActivity() {
        try {
            val intent = Intent(this, CameraActivity::class.java)
            intent.putExtra("language", language)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                getLocalizedString(
                    fr = "Erreur lors du lancement de la caméra: ${e.message}",
                    en = "Error launching camera: ${e.message}",
                    ar = "خطأ في تشغيل الكاميرا: ${e.message}"
                ),
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
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
    
    private fun getLocalizedString(fr: String, en: String, ar: String): String {
        return when (language) {
            "en" -> en
            "ar" -> ar
            else -> fr
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onCameraRequest: () -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    var currentTheme by remember { mutableStateOf(ThemeHelper.getTheme(context)) }

    val offsetX by animateDpAsState(
        targetValue = if (drawerState.isOpen) 0.dp else (-50).dp,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
    )

    val alpha by animateFloatAsState(
        targetValue = if (drawerState.isOpen) 1f else 0f,
        animationSpec = tween(500)
    )
    
    val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

    ModalDrawer(
        drawerState = drawerState,
        drawerBackgroundColor = Color.Transparent,
        drawerContent = {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                DreamyBackground(theme = currentTheme)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .graphicsLayer(alpha = alpha)
                        .offset(x = offsetX)
                ) {
                    Spacer(modifier = Modifier.height(48.dp))

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_app),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(1.2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = when (language) {
                            "en" -> "Sign Language\nTranslator"
                            "ar" -> "مترجم\nلغة الإشارة"
                            "kab" -> "Asegzi n tutlayt n yismawen"
                            else -> "Traducteur\nLangue des Signes"
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 32.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                    )

                    DrawerSection(
                        icon = Icons.Default.Person,
                        title = when (language) {
                            "en" -> "Developers"
                            "ar" -> "المطورون"
                            "kab" -> "Imsuɣulen"
                            else -> "Développeurs"
                        },
                        content = {
                            Column(
                                modifier = Modifier.padding(start = 32.dp, top = 8.dp)
                            ) {
                                Text(
                                    text = "Said Ahrikenchikh",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                SocialButton(
                                    icon = R.drawable.ic_instagram,
                                    text = "@said_ahriken",
                                    backgroundColor = Color(0xFF405DE6)
                                ) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/said_ahriken/"))
                                    context.startActivity(intent)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                SocialButton(
                                    icon = R.drawable.ic_facebook,
                                    text = "Saïd Ahriken",
                                    backgroundColor = Color(0xFF1877F2)
                                ) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.facebook.com/p/Sa%C3%AFd-Ahriken-100000878390109/?wtsid=rdr_0F7oo79EecqWCWt5J&hr=1#"))
                                    context.startActivity(intent)
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Jugurtha Azouaou",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                SocialButton(
                                    icon = R.drawable.ic_instagram,
                                    text = "@flameyout",
                                    backgroundColor = Color(0xFF405DE6)
                                ) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/flameyout?igsh=MzRlODBiNWFlZA=="))
                                    context.startActivity(intent)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                SocialButton(
                                    icon = R.drawable.ic_facebook,
                                    text = "Jugurtha Azouaou",
                                    backgroundColor = Color(0xFF1877F2)
                                ) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/fla.mee.3"))
                                    context.startActivity(intent)
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DrawerSection(
                        icon = Icons.Default.Language,
                        title = when (language) {
                            "en" -> "Language"
                            "ar" -> "اللغة"
                            "kab" -> "Tutlayt"
                            else -> "Langue"
                        },
                        content = {
                            Column(
                                modifier = Modifier.padding(start = 32.dp, top = 8.dp)
                            ) {
                                LanguageButtons(
                                    currentLanguage = language,
                                    onLanguageSelected = onLanguageChange
                                )
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DrawerSection(
                        icon = Icons.Default.ColorLens,
                        title = when (language) {
                            "en" -> "Theme"
                            "ar" -> "المظهر"
                            "kab" -> "Tiɣri"
                            else -> "Thème"
                        },
                        content = {
                            Column(
                                modifier = Modifier.padding(start = 32.dp, top = 8.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (currentTheme == ThemeHelper.THEME_DREAMY) Color.White.copy(alpha = 0.2f) 
                                                else Color.Transparent
                                            )
                                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                            .clickable {
                                                currentTheme = ThemeHelper.THEME_DREAMY
                                                ThemeHelper.saveTheme(context, ThemeHelper.THEME_DREAMY)
                                            }
                                            .padding(12.dp)
                                    ) {
                                        Text("Dreamy", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (currentTheme == ThemeHelper.THEME_CLASSIC_BLUE) Color.White.copy(alpha = 0.2f) 
                                                else Color.Transparent
                                            )
                                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                            .clickable {
                                                currentTheme = ThemeHelper.THEME_CLASSIC_BLUE
                                                ThemeHelper.saveTheme(context, ThemeHelper.THEME_CLASSIC_BLUE)
                                            }
                                            .padding(12.dp)
                                    ) {
                                        Text("Blue", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            DreamyBackground(theme = currentTheme)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Text(
                            text = when (language) {
                                "en" -> "Choose an option"
                                "ar" -> "اختر خيارًا"
                                "kab" -> "Xtir tifrat"
                                else -> "Choisissez une option"
                            },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        onClick = { onCameraRequest() }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = 0.2f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_sign_language_to_text),
                                    contentDescription = "Langue des signes vers texte",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .shadow(8.dp, CircleShape),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = when (language) {
                                    "en" -> "Sign Language to Text"
                                    "ar" -> "ترجمة  لغة الإشارة إلى نص"
                                    "kab" -> "Tutlayt n yismawen ɣer uḍris"
                                    else -> "Langue des signes vers texte"
                                },
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            PulseAnimation(
                                duration = 1500,
                                scaleRange = 0.95f to 1.05f
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF3498DB),
                                    shadowElevation = 12.dp,
                                    tonalElevation = 4.dp
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color(0xFF3498DB),
                                                        Color(0xFF2980B9)
                                                    )
                                                )
                                            )
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_camera_custom),
                                            contentDescription = "Caméra",
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = when (language) {
                                                "en" -> "Commencer la traduction"
                                                "ar" -> "بدء الترجمة"
                                                "kab" -> "Evdu"
                                                else -> "Commencer la traduction"
                                            },
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        onClick = { 
                                val intent = Intent(context, VoiceToSignActivity::class.java)
                                context.startActivity(intent)
                        }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = 0.2f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_voice_to_sign),
                                    contentDescription = "Voix vers langue des signes",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .shadow(8.dp, CircleShape),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = if (language == "kab") 4.dp else 16.dp)
                            ) {
                                Text(
                                    text = when (language) {
                                        "en" -> "Voice to Sign Language"
                                        "ar" -> "الترجمة من الصوت إلى لغة الإشارة"
                                        "kab" -> "Taɣect ɣer tutlayt n yismawen"
                                        else -> "Voix vers langue des signes"
                                    },
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 1.sp,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = when (language) {
                                        "en" -> "Convert your voice to sign language"
                                        "ar" -> "حول صوتك إلى لغة الإشارة"
                                        "kab" -> "Ssuqel taɣect-ik ɣer tutlayt n yilsawen"
                                        else -> "Convertissez votre voix en langue des signes"
                                    },
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(0.dp))

                            PulseAnimation(
                                duration = 1500,
                                scaleRange = 0.95f to 1.05f
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF9B59B6), // Violet constant
                                    shadowElevation = 12.dp,
                                    tonalElevation = 4.dp
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color(0xFF9B59B6),
                                                        Color(0xFF8E44AD)
                                                    )
                                                )
                                            )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = if (language == "en") "Microphone" else if (language == "ar") "ميكروفون" else "Microphone",
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = when (language) {
                                                "en" -> "Start Recording"
                                                "ar" -> "بدء التسجيل"
                                                "kab" -> "Ehdar tura"
                                                else -> "Commencer l\'enregistrement"
                                            },
                                            fontSize = if (language == "kab") 15.sp else 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Spacer(modifier = Modifier.height(32.dp))
                }

            IconButton(
                onClick = { scope.launch { drawerState.open() } },
                modifier = Modifier
                    .padding(top = 32.dp, start = 16.dp)
                    .size(48.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        spotColor = Color.Black.copy(alpha = 0.3f),
                        ambientColor = Color.Black.copy(alpha = 0.1f)
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            ) {
                val rotation by animateFloatAsState(
                    targetValue = if (drawerState.isOpen) 90f else 0f,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
                
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu_custom),
                    contentDescription = if (language == "en") "Open menu" else if (language == "ar") "فتح القائمة" else "Ouvrir le menu",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer(
                            rotationZ = rotation,
                            scaleX = 1f,
                            scaleY = 1f
                        )
                )
            }
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else if (isHovered) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val rotationX by animateFloatAsState(
        targetValue = if (isPressed) 2f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    
    val rotationY by animateFloatAsState(
        targetValue = if (isPressed) -2f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.rotationX = rotationX
                this.rotationY = rotationY
                cameraDistance = 12f * density
            }
            .shadow(
                elevation = if (isPressed) 4.dp else 8.dp, // Reduced elevation
                shape = RoundedCornerShape(24.dp), // Slightly less rounded for cleaner look
                spotColor = Color.Black.copy(alpha = 0.2f),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = Color.White.copy(alpha = 0.1f)),
                onClick = onClick
            )
            .background(
                color = Color.White.copy(alpha = 0.08f) // Simple, clean flat transparency
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.15f), // Very subtle single-color border
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        content()
    }
}

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.05f),
        Color.White.copy(alpha = 0.1f),
        Color.White.copy(alpha = 0.05f)
    )

    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 500f, translateAnim - 500f),
        end = Offset(translateAnim + 100f, translateAnim + 100f)
    )

    Box(
        modifier = modifier
            .background(brush)
    )
}

@Composable
fun HoverAnimation(content: @Composable () -> Unit) {
    var scale by remember { mutableStateOf(1f) } 
    
    LaunchedEffect(Unit) {
        while (true) {
            scale = 1.02f
            delay(2000)
            scale = 1f
            delay(2000)
        }
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        content()
    }
}

@Composable
fun PulseAnimation(
    duration: Int = 1000,
    scaleRange: Pair<Float, Float> = 0.98f to 1.02f,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = scaleRange.first,
        targetValue = scaleRange.second,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        content()
    }
}

@Composable
fun DrawerSection(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) } 
    
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            content()
        }
    }
}

@Composable
fun SocialButton(
    @DrawableRes icon: Int,
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor.copy(alpha = 0.2f))
            .border(
                width = 1.dp,
                color = backgroundColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = Color.White),
                onClick = onClick
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.graphicsLayer {
                    shadowElevation = 4f
                    alpha = 0.99f
                }
            )
        }
    }
}

@Composable
fun LanguageButtons(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        // Bouton Français
        LanguageButton(
            language = "fr",
            displayText = "FR",
            isSelected = currentLanguage == "fr",
            onClick = { onLanguageSelected("fr") }
        )
        // Bouton Anglais
        LanguageButton(
            language = "en",
            displayText = "EN",
            isSelected = currentLanguage == "en",
            onClick = { onLanguageSelected("en") }
        )
        // Bouton Arabe
        LanguageButton(
            language = "ar",
            displayText = "ع",
            isSelected = currentLanguage == "ar",
            onClick = { onLanguageSelected("ar") }
        )
        // Bouton Kabyle (ⵣ)
        LanguageButton(
            language = "kab",
            displayText = "ⵣ",
            isSelected = currentLanguage == "kab",
            onClick = { onLanguageSelected("kab") }
        )
    }
}

@Composable
fun LanguageButton(
    language: String,
    displayText: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                   else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}
