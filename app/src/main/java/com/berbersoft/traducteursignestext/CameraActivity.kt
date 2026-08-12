package com.berbersoft.traducteursignestext

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.camera.core.ImageAnalysis
import android.util.Size
import androidx.compose.animation.*
import androidx.compose.ui.text.font.FontWeight
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.ImageProxy
import com.berbersoft.traducteursignestext.ui.theme.TraducteursignestextTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.CameraRear
import android.content.Intent
import android.speech.tts.TextToSpeech
import java.util.Locale
import android.content.Context
import android.content.SharedPreferences
import android.widget.VideoView
import android.widget.MediaController
import com.berbersoft.traducteursignestext.R
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.common.MediaItem
import androidx.compose.runtime.DisposableEffect
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.compose.material.icons.filled.Replay
import androidx.compose.ui.text.font.FontStyle

class CameraActivity : ComponentActivity() {
    private var isBackCamera by mutableStateOf(true)
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Préférence pour le tutoriel
        prefs = getSharedPreferences("camera_prefs", Context.MODE_PRIVATE)
        val showTutorial = prefs.getBoolean("show_tutorial", true)

        setContent {
            TraducteursignestextTheme(darkTheme = true) {
                var showTutorialState by remember { mutableStateOf(showTutorial) }
                var showCamera by remember { mutableStateOf(!showTutorial) }
                Box(modifier = Modifier.fillMaxSize()) {
                    if (showTutorialState) {
                        TutorialVideo(
                            onClose = {
                                showTutorialState = false
                                showCamera = true
                                prefs.edit().putBoolean("show_tutorial", false).apply()
                            }
                        )
                    }
                    if (showCamera) {
                        CameraContent(
                            isBackCamera = isBackCamera,
                            onCameraSwitch = { newIsBack ->
                                isBackCamera = newIsBack
                            }
                        )
                        IconButton(
                            onClick = {
                                finish()
                                startActivity(Intent(this@CameraActivity, MainActivity::class.java))
                            },
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.TopStart)
                                .size(48.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Retour",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // On nettoie la caméra quand on quitte
        try {
            val cameraProvider = ProcessCameraProvider.getInstance(this).get()
            cameraProvider.unbindAll()
        } catch (e: Exception) {
            e.printStackTrace() // Au cas où ça plante
        }
    }
}

// La vue principale de la caméra
@Composable
private fun CameraContent(
    isBackCamera: Boolean,
    onCameraSwitch: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraScreen(
            isBackCamera = isBackCamera,
            onCameraSwitch = onCameraSwitch
        )
        
        // Bouton pour revenir en arrière
        IconButton(
            onClick = { /* On gère ça plus haut */ },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Retour",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CameraScreen(
    isBackCamera: Boolean,
    onCameraSwitch: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    var detectedText by remember { mutableStateOf("") }
    var lastUpdateTime by remember { mutableStateOf(0L) }
    val updateDelay = 300L
    
    var gestureHistory by remember { mutableStateOf(listOf<String>()) }
    val maxHistorySize = 5

    // --- TTS ---
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var lastSpokenText by remember { mutableStateOf("") }

    // Fonction pour transformer le texte avant la lecture
    fun prepareTextForSpeech(text: String): String {
        return text
            .replace("LSA", "L S A")
            .replace("ASL", "A S L")
    }

    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.FRENCH
            }
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }
    // --- FIN TTS ---

    val gestureRecognizer = remember { 
        GestureRecognizerHelper(context) { result ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastUpdateTime > updateDelay) {
                detectedText = result
                if (result.isNotEmpty() && !result.contains("Positionnez")) {
                    gestureHistory = (listOf(result) + gestureHistory).take(maxHistorySize)
                }
                lastUpdateTime = currentTime
            }
        }
    }

    // Ajout : lire le texte détecté automatiquement
    LaunchedEffect(detectedText) {
        if (detectedText.isNotBlank() && detectedText != lastSpokenText && !detectedText.contains("Positionnez") && !detectedText.contains("Préparation")) {
            val textToSpeak = prepareTextForSpeech(detectedText)
            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
            lastSpokenText = detectedText
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { previewView ->
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(640, 480))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .apply {
                        setAnalyzer(
                            ContextCompat.getMainExecutor(context)
                        ) { imageProxy ->
                            val bitmap = imageProxy.toBitmap()?.let {
                                if (imageProxy.imageInfo.rotationDegrees != 0) {
                                    it.rotate(imageProxy.imageInfo.rotationDegrees)
                                } else {
                                    it
                                }
                            }
                            
                            bitmap?.let {
                                gestureRecognizer.recognize(it)
                                it.recycle()
                            }
                            
                            imageProxy.close()
                        }
                    }

                val cameraSelector = if (isBackCamera) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))  // Pour pousser le carré vers le centre

            // Guide de positionnement de la main
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Text(
                    text = "Essayez de positionner votre main ici",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_hand_guide),
                    contentDescription = "Guide de la main",
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sélecteur de caméra déplacé ici
            CameraSwitchButtons(
                isBackCamera = isBackCamera,
                onCameraSwitch = onCameraSwitch
            )

            Spacer(modifier = Modifier.weight(1f))  // Pour équilibrer l'espace
        }

        // Garder l'historique des gestes en bas à droite
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(
                    Color.Black.copy(alpha = 0.7f),
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Text(
                text = "Historique",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            gestureHistory.forEach { gesture ->
                Text(
                    text = "• $gesture",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        // Garder le texte détecté en haut
        AnimatedVisibility(
            visible = detectedText.isNotEmpty(),
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(16.dp)
            ) {
                Text(
                    text = detectedText.ifEmpty { "Préparation de la détection..." },
                    color = when {
                        detectedText.contains("Pouce en l'air") -> Color(0xFF4CAF50)
                        detectedText.contains("Victoire") -> Color(0xFFFFEB3B)
                        detectedText.contains("Main ouverte") -> Color(0xFF2196F3)
                        detectedText.contains("Poing fermé") -> Color(0xFFFF5722)
                        detectedText.contains("Je t'aime") -> Color(0xFFE91E63)
                        else -> Color.White
                    },
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CameraSwitchButtons(
    isBackCamera: Boolean,
    onCameraSwitch: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bouton caméra avant
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onCameraSwitch(false) }
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = if (!isBackCamera) Color(0xFF2196F3) else Color(0xFF424242),
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = if (!isBackCamera) Color(0xFF64B5F6) else Color.Gray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraFront,
                    contentDescription = "Caméra avant",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Avant",
                color = if (!isBackCamera) Color(0xFF2196F3) else Color.Gray,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        // Bouton caméra arrière
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onCameraSwitch(true) }
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = if (isBackCamera) Color(0xFF2196F3) else Color(0xFF424242),
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = if (isBackCamera) Color(0xFF64B5F6) else Color.Gray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraRear,
                    contentDescription = "Caméra arrière",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Arrière",
                color = if (isBackCamera) Color(0xFF2196F3) else Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

private fun ImageProxy.toBitmap(): Bitmap? {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

private fun Bitmap.rotate(degrees: Int): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

private fun Bitmap.scaleDown(maxImageSize: Int): Bitmap {
    val ratio = maxImageSize.toFloat() / maxOf(width, height)
    return if (ratio < 1) {
        Bitmap.createScaledBitmap(
            this,
            (width * ratio).toInt(),
            (height * ratio).toInt(),
            true
        )
    } else {
        this
    }
} 

@Composable
fun TutorialVideo(onClose: () -> Unit) {
    val context = LocalContext.current
    var showReplay by remember { mutableStateOf(false) }
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.tutoriel}")
            setMediaItem(mediaItem)
            repeatMode = ExoPlayer.REPEAT_MODE_OFF
            prepare()
        }
    }
    DisposableEffect(exoPlayer) {
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == ExoPlayer.STATE_ENDED) {
                    showReplay = true
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .zIndex(10f)
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM // crop/zoom
                }
            },
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Center)
        )
        // Bouton Fermer élégant et moderne
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                    .clickable(onClick = onClose)
                    .shadow(4.dp, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fermer le tutoriel",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(28.dp)
                )
            }
            Text(
                text = "Fermer le tutoriel",
                color = Color.LightGray,
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        if (showReplay) {
            // Bouton Rejouer amélioré
            Button(
                onClick = {
                    exoPlayer.seekTo(0)
                    exoPlayer.playWhenReady = true
                    showReplay = false
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)) // Bleu accent
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Rejouer le tutoriel",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rejouer", color = Color.White, fontSize = 18.sp)
            }
        }
    }
} 