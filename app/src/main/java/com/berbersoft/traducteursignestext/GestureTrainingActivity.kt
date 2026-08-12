package com.berbersoft.traducteursignestext

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.style.TextAlign
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.berbersoft.traducteursignestext.ui.theme.TraducteursignestextTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.DisposableEffect
import kotlinx.coroutines.*

class GestureTrainingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TraducteursignestextTheme(darkTheme = true) {
                GestureTestScreen()
            }
        }
    }
}

@Composable
fun GestureTestScreen() {
    var selectedGesture by remember { mutableStateOf<GestureType?>(null) }
    var showCamera by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E),
                        Color(0xFF0D47A1)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Test des Gestes LSF",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                item {
                    GestureCategory(
                        title = "Gestes LSF de base",
                        gestures = listOf(
                            GestureType.BONJOUR,
                            GestureType.OUI,
                            GestureType.NON,
                            GestureType.AIDE,
                            GestureType.MERCI
                        ),
                        onGestureSelected = { selectedGesture = it }
                    )
                }

                item {
                    GestureCategory(
                        title = "Gestes LSF dynamiques",
                        gestures = listOf(
                            GestureType.AU_REVOIR,
                            GestureType.MANGER,
                            GestureType.BOIRE,
                            GestureType.TOILETTES,
                            GestureType.URGENCE
                        ),
                        onGestureSelected = { selectedGesture = it }
                    )
                }
            }

            // Zone d'information sur le geste sélectionné
            selectedGesture?.let { gesture ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2C3E50).copy(alpha = 0.9f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Geste sélectionné : ${gesture.description}",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { showCamera = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text("Tester ce geste")
                        }
                    }
                }
            }
        }

        // Affichage de la caméra pour le test
        if (showCamera) {
            CameraPreview(
                onGestureDetected = { result ->
                    // Afficher un Toast avec le geste détecté
                    Toast.makeText(context, "Geste détecté : $result", Toast.LENGTH_SHORT).show()
                    
                    // Si le geste correspond au geste sélectionné
                    if (result.contains(selectedGesture?.label ?: "")) {
                        // Feedback positif
                        Toast.makeText(context, "Bravo ! Geste correct !", Toast.LENGTH_LONG).show()
                    }
                },
                onClose = { showCamera = false }
            )
        }
    }
}

@Composable
fun GestureCategory(
    title: String,
    gestures: List<GestureType>,
    onGestureSelected: (GestureType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(gestures) { gesture ->
                GestureChip(
                    gesture = gesture,
                    onClick = { onGestureSelected(gesture) }
                )
            }
        }
    }
}

@Composable
fun GestureChip(
    gesture: GestureType,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color(0xFF3498DB).copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = gesture.label,
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun CameraPreview(
    onGestureDetected: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var lastProcessingTimeMs = remember { 0L }
    val processingTimeThreshold = 30L
    var detectedGesture by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Prévisualisation de la caméra
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { previewView ->
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()

            preview.setSurfaceProvider(previewView.surfaceProvider)

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(android.util.Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(
                        ContextCompat.getMainExecutor(context)
                    ) { imageProxy ->
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastProcessingTimeMs >= processingTimeThreshold) {
                            imageProxy.toBitmap()?.let { bitmap ->
                                try {
                                    // Mise à jour de l'état avant la détection
                                    detectedGesture = "Analyse en cours..."
                                    
                                    // Créer un nouveau gestureRecognizer pour chaque frame
                                    val recognizer = GestureRecognizerHelper(context) { result ->
                                        detectedGesture = result
                                        onGestureDetected(result)
                                    }
                                    
                                    recognizer.recognize(bitmap)
                                    
                                } catch (e: Exception) {
                                    detectedGesture = "Erreur: ${e.message}"
                                    e.printStackTrace()
                                }
                            }
                            lastProcessingTimeMs = currentTime
                        }
                        imageProxy.close()
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Cadre de guidage
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
                .border(
                    width = 2.dp,
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp)
                )
        )

        // Instructions et résultats en haut
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(16.dp)
        ) {
            Text(
                text = "Placez votre main dans le cadre",
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = detectedGesture,
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Bouton de fermeture
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Fermer",
                tint = Color.White
            )
        }
    }
}

// Extension function pour convertir ImageProxy en Bitmap
private fun ImageProxy.toBitmap(): Bitmap? {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
} 