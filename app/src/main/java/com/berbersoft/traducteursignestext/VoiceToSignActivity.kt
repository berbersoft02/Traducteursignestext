package com.berbersoft.traducteursignestext

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import com.berbersoft.traducteursignestext.ui.theme.TraducteursignestextTheme
import androidx.compose.foundation.clickable
import kotlin.random.Random
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import com.berbersoft.traducteursignestext.models.Particle
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.platform.LocalDensity
import android.net.Uri
import android.widget.VideoView
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.border
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.lerp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale
import com.berbersoft.traducteursignestext.ui.theme.DreamyBackground
import com.berbersoft.traducteursignestext.utils.ThemeHelper

class VoiceToSignActivity : ComponentActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private var currentWordState by mutableStateOf<SignWord?>(null)
    private var isListening by mutableStateOf(false)
    // État pour la langue de reconnaissance (indépendant de la langue de l'UI)
    private var recognitionLanguage by mutableStateOf("fr")

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startListening()
        } else {
            Toast.makeText(this, "Permission micro refusée", Toast.LENGTH_SHORT).show()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialiser avec la langue de l'app par défaut
        val appLanguage = intent?.getStringExtra("language") ?: "fr"
        recognitionLanguage = appLanguage

        // Traiter l'intent s'il provient de Google Assistant
        intent?.data?.let { uri ->
            if (uri.toString().contains("/translate")) {
                val query = uri.getQueryParameter("query")
                query?.let { text ->
                    val signWord = SignDictionary.findSignByWord(text)
                    currentWordState = signWord
                }
            }
        }

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "La reconnaissance vocale n'est pas disponible sur cet appareil", Toast.LENGTH_LONG).show()
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.get(0)?.let { text ->
                    Log.d("VoiceRecognition", "Texte reconnu ($recognitionLanguage): '$text'")
                    
                    // Trouver le mot
                    val signWord = SignDictionary.findSignByWord(text)
                    
                    if (signWord != null) {
                        currentWordState = signWord
                        Toast.makeText(this@VoiceToSignActivity, "Trouvé: ${signWord.word}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@VoiceToSignActivity, "Non trouvé: $text", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onReadyForSpeech(params: Bundle?) { }
            override fun onBeginningOfSpeech() { }
            override fun onError(error: Int) {
                isListening = false
                // ... (gestion erreurs simplifiée pour lisibilité)
            }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        setContent {
            TraducteursignestextTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    VoiceToSignScreen(
                        currentWord = currentWordState,
                        isListening = isListening,
                        selectedLanguage = recognitionLanguage,
                        onLanguageSelected = { recognitionLanguage = it },
                        onStartListening = {
                            isListening = true
                            checkPermissionAndStartListening()
                        },
                        onStopListening = {
                            isListening = false
                            speechRecognizer.stopListening()
                        },
                        onReset = { currentWordState = null }
                    )
                }
            }
        }
    }

    private fun checkPermissionAndStartListening() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED -> {
                startListening()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startListening() {
        val intentLocale = when (recognitionLanguage) {
            "ar" -> "ar-SA"
            "en" -> "en-US"
            else -> "fr-FR"
        }
        
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, intentLocale)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, intentLocale) 
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, intentLocale)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez maintenant...")
        }
        try {
            isListening = true
            speechRecognizer.startListening(recognizerIntent)
        } catch (e: Exception) {
            isListening = false
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}

@Composable
fun VoiceToSignScreen(
    currentWord: SignWord?,
    isListening: Boolean,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onReset: () -> Unit
) {
    val context = LocalContext.current
    val currentTheme = ThemeHelper.getTheme(context)

    Box(modifier = Modifier.fillMaxSize()) {
        DreamyBackground(theme = currentTheme)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sélecteur de langue vocal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LanguageChip("FR", "fr", selectedLanguage, onLanguageSelected)
                Spacer(modifier = Modifier.width(12.dp))
                LanguageChip("EN", "en", selectedLanguage, onLanguageSelected)
                Spacer(modifier = Modifier.width(12.dp))
                LanguageChip("AR", "ar", selectedLanguage, onLanguageSelected)
            }

            // Zone principale avec effet de profondeur
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1A237E).copy(alpha = 0.2f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (currentWord != null) {
                    // ... (Affichage du résultat, inchangé)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(280.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        ) {
                            if (SignDictionary.isVideoWord(currentWord.word)) {
                                VideoPlayer(currentWord)
                            } else {
                                Image(
                                    painter = painterResource(id = currentWord.imageRes),
                                    contentDescription = currentWord.description,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize().padding(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = currentWord.word.uppercase(),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = currentWord.description, color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                    }
                } else {
                    LiveWaveform(isListening)
                }
            }

            // Barre d'actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = onReset,
                    containerColor = Color(0xFF455A64),
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp).shadow(8.dp, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                }

                ListeningButton(
                    isListening = isListening,
                    onStartListening = onStartListening,
                    onStopListening = onStopListening
                )
                
                // Spacer pour équilibrer
                Spacer(modifier = Modifier.size(56.dp))
            }
        }
    }
}

@Composable
fun LanguageChip(
    text: String,
    code: String,
    selectedCode: String,
    onSelect: (String) -> Unit
) {
    val isSelected = code == selectedCode
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) Color(0xFF2196F3) else Color.White.copy(alpha = 0.1f)
            )
            .border(
                1.dp,
                if (isSelected) Color(0xFF64B5F6) else Color.White.copy(alpha = 0.2f),
                RoundedCornerShape(20.dp)
            )
            .clickable { onSelect(code) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ListeningButton(
    isListening: Boolean,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit
) {
    val buttonAnim = rememberInfiniteTransition(label = "buttonPulse")
    val scale by buttonAnim.animateFloat(
        initialValue = if (isListening) 0.95f else 1f,
        targetValue = if (isListening) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonScale"
    )

    FloatingActionButton(
        onClick = if (isListening) onStopListening else onStartListening,
        containerColor = if (isListening) Color(0xFFE53935) else Color(0xFF4CAF50),
        contentColor = Color.White,
        modifier = Modifier
            .size(72.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (isListening) 16.dp else 8.dp,
                shape = CircleShape,
                ambientColor = if (isListening) Color(0xFFE53935) else Color(0xFF4CAF50)
            )
    ) {
        Icon(
            imageVector = if (isListening) Icons.Default.MicNone else Icons.Default.Mic,
            contentDescription = if (isListening) "Arrêter l'écoute" else "Commencer l'écoute",
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
fun VoiceToSignContent(
    currentWord: SignWord?,
    isListening: Boolean,
    selectedLanguage: String = "fr",
    onLanguageSelected: (String) -> Unit = {},
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onReset: () -> Unit
) {
    VoiceToSignScreen(
        currentWord = currentWord,
        isListening = isListening,
        selectedLanguage = selectedLanguage,
        onLanguageSelected = onLanguageSelected,
        onStartListening = onStartListening,
        onStopListening = onStopListening,
        onReset = onReset
    )
}

@Composable
fun VideoPlayer(signWord: SignWord) {
    val context = LocalContext.current
    val videoFileName = SignDictionary.getVideoFileName(signWord.word)
    val videoUri = Uri.parse("android.resource://${context.packageName}/raw/${videoFileName}")
    
    AndroidView(
        factory = { ctx ->
            VideoView(ctx).apply {
                setVideoURI(videoUri)
                setOnPreparedListener { mp ->
                    mp.isLooping = true
                    start()
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun LiveWaveform(isListening: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    
    // Animation de couleur
    val color by infiniteTransition.animateColor(
        initialValue = Color(0xFF4CAF50),
        targetValue = if (isListening) Color(0xFFFF5252) else Color(0xFF4CAF50),
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Cercles concentriques (Ripple effect)
            if (isListening) {
                for (i in 0..2) {
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 2.5f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, delayMillis = i * 500, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "ripple$i"
                    )
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, delayMillis = i * 500, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "alpha$i"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(scale)
                            .border(2.dp, color.copy(alpha = alpha), CircleShape)
                    )
                }
            }

            // Barres d'onde audio (simulées)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(60.dp)
            ) {
                repeat(5) { index ->
                    val height by infiniteTransition.animateFloat(
                        initialValue = 10f,
                        targetValue = if (isListening) Random.nextFloat() * 50f + 20f else 10f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 300 + index * 100,
                                easing = FastOutSlowInEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "bar$index"
                    )
                    
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(height.dp)
                            .background(color, RoundedCornerShape(3.dp))
                    )
                }
            }
            
            // Icône micro centrale (plus petite)
            Icon(
                imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .offset(y = 40.dp) // Décalé sous les ondes
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (isListening) "Je vous écoute..." else "Appuyez sur le bouton pour parler",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}