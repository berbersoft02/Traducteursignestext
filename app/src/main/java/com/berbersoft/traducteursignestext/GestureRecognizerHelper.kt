package com.berbersoft.traducteursignestext

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.berbersoft.traducteursignestext.models.GestureType
import com.berbersoft.traducteursignestext.models.GestureType.*

class GestureRecognizerHelper(
    private val context: Context,
    private val onResult: (String) -> Unit
) {
    private var gestureRecognizer: GestureRecognizer? = null
    private val confidenceThreshold = 0.3f
    private var lastDetectedGesture: GestureType = GestureType.NONE
    private var consecutiveDetections = 0
    private val requiredConsecutiveDetections = 2
    private val gestureHistory = mutableListOf<Pair<GestureType, Long>>()
    private val historyTimeWindow = 1500L
    private var lastProcessingTimeMs = 0L
    private val processingDelay = 100L
    private val GESTURE_THRESHOLD = 0.5f
    
    // Variables pour la détection du "Non" avec le mouvement d'index
    private var lastIndexPositions = mutableListOf<Float>()
    private val maxHistorySize = 10
    private val requiredDirectionChanges = 2
    private val movementThreshold = 0.05f // Diminué pour détecter des mouvements plus petits
    private var lastDetectedNonTime = 0L
    private val nonDetectionCooldown = 2000L // Délai de 2 secondes entre les détections de "Non"
    
    // Variables pour la détection du F avec mouvement
    private var lastFDetections = mutableListOf<Long>()
    private val fDetectionWindow = 2000L // Fenêtre de 2 secondes pour détecter deux F consécutifs
    private var lastDetectedFTime = 0L
    private val fDetectionCooldown = 2000L // Délai entre les détections de "Oui"

    // Variables pour la détection du H avec mouvement (Non en LSA)
    private var lastHDetections = mutableListOf<Long>()
    private val hDetectionWindow = 3000L // Fenêtre de 3 secondes pour détecter 4-5 H consécutifs
    private var lastDetectedHTime = 0L
    private val hDetectionCooldown = 2000L // Délai entre les détections de "Non en LSA"
    private val requiredHDetections = 4 // Nombre requis de détections H

    // Variables pour la détection du Oui répété
    private var lastOuiDetections = mutableListOf<Long>()
    private val ouiDetectionWindow = 3000L // Fenêtre de 3 secondes pour détecter trois Oui consécutifs
    private var lastDetectedOuiTime = 0L
    private val ouiDetectionCooldown = 2000L // Délai entre les détections de "Désolé"

    // Ajouter les variables pour la détection du Z
    private var indexPositionsForZ = mutableListOf<Pair<Float, Float>>() // Pour stocker les positions X et Y
    private val maxZHistorySize = 15
    private var lastDetectedZTime = 0L
    private val zDetectionCooldown = 2000L
    private val zMovementThreshold = 0.05f

    init {
        setupGestureRecognizer()
    }

    private fun setupGestureRecognizer() {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("gesture_recognizer.task")
                .setDelegate(Delegate.CPU)
                .build()
            
            val options = GestureRecognizer.GestureRecognizerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setNumHands(2)
                .setMinHandDetectionConfidence(0.3f)
                .setMinHandPresenceConfidence(0.3f)
                .setMinTrackingConfidence(0.3f)
                .build()

            gestureRecognizer = GestureRecognizer.createFromOptions(context, options)
        } catch (e: Exception) {
            onResult("Erreur d'initialisation: ${e.message}")
        }
    }

    fun recognize(bitmap: Bitmap) {
        val currentTime = System.currentTimeMillis()

        try {
            val mpImage = BitmapImageBuilder(bitmap).build()
            val result = gestureRecognizer?.recognize(mpImage)
            
            result?.let { 
                processResult(it, currentTime)
            }
        } catch (e: Exception) {
            onResult("Erreur: ${e.message}")
        }
    }

    private fun processResult(result: GestureRecognizerResult, currentTime: Long) {
        if (result.gestures().isEmpty() || result.landmarks().isEmpty()) {
            resetDetection()
            return
        }

        val landmarks = result.landmarks()[0]
        
        // Ajouter la détection du Z
        detectZGesture(landmarks, currentTime)
        
        // Tenter de détecter le geste "Non" par le mouvement de l'index
        detectNonGesture(landmarks, currentTime)
        
        // Continuer avec la détection normale des gestes
        val gestureResults = result.gestures()[0]
        
        Log.e("GESTURE_DEBUG", "=== START GESTURE DETECTION ===")
        
        gestureResults.forEachIndexed { index, gesture ->
            Log.e("GESTURE_DEBUG", "Gesture $index: label=${gesture.categoryName()}, score=${gesture.score()}")
        }
        
        val bestGesture = gestureResults[0]
        Log.e("GESTURE_DEBUG", "Best gesture: ${bestGesture.categoryName()} with score: ${bestGesture.score()}")
        
        if (result.landmarks().isNotEmpty()) {
            Log.e("GESTURE_DEBUG", "Number of landmarks: ${result.landmarks()[0].size}")
        }

        Log.e("GESTURE_DEBUG", "=== END GESTURE DETECTION ===")

        val currentGesture = GestureType.fromLabel(bestGesture.categoryName())
        
        // Détection du F avec mouvement
        if (currentGesture == GestureType.F && isGestureRecognized(bestGesture.score())) {
            lastFDetections.add(currentTime)
            // Nettoyer les anciennes détections
            lastFDetections = lastFDetections.filter { currentTime - it <= fDetectionWindow }.toMutableList()
            
            // Si on a détecté deux F dans la fenêtre de temps
            if (lastFDetections.size >= 2 && currentTime - lastDetectedFTime > fDetectionCooldown) {
                onResult("Oui en LSA")
                lastDetectedFTime = currentTime
                lastFDetections.clear()
            }
        }

        // Détection du H avec mouvement pour "Non en LSA"
        if (currentGesture == GestureType.H && isGestureRecognized(bestGesture.score())) {
            lastHDetections.add(currentTime)
            // Nettoyer les anciennes détections
            lastHDetections = lastHDetections.filter { currentTime - it <= hDetectionWindow }.toMutableList()
            
            // Si on a détecté 4 à 5 H dans la fenêtre de temps
            if (lastHDetections.size >= requiredHDetections && currentTime - lastDetectedHTime > hDetectionCooldown) {
                onResult("Non en LSA")
                lastDetectedHTime = currentTime
                lastHDetections.clear()
            }
        }

        // Détection du Oui répété
        if (currentGesture == GestureType.OUI && isGestureRecognized(bestGesture.score())) {
            lastOuiDetections.add(currentTime)
            // Nettoyer les anciennes détections
            lastOuiDetections = lastOuiDetections.filter { currentTime - it <= ouiDetectionWindow }.toMutableList()
            
            // Si on a détecté trois Oui dans la fenêtre de temps
            if (lastOuiDetections.size >= 3 && currentTime - lastDetectedOuiTime > ouiDetectionCooldown) {
                onResult("Désolé en LSA")
                lastDetectedOuiTime = currentTime
                lastOuiDetections.clear()
            }
        }
        
        if (isGestureRecognized(bestGesture.score())) {
            if (currentGesture == lastDetectedGesture) {
                consecutiveDetections++
                if (consecutiveDetections >= requiredConsecutiveDetections) {
                    handleStaticGesture(currentGesture)
                    consecutiveDetections = 0
                }
            } else {
                lastDetectedGesture = currentGesture
                consecutiveDetections = 1
            }
        }
    }

    private fun detectNonGesture(landmarks: List<NormalizedLandmark>, currentTime: Long) {
        if (landmarks.size < 21 || currentTime - lastDetectedNonTime < nonDetectionCooldown) return
        
        if (isIndexPointing(landmarks)) {
            val indexTip = landmarks[8]
            val currentX = indexTip.x()
            
            // Ajouter la position actuelle à l'historique
            lastIndexPositions.add(currentX)
            
            // Limiter la taille de l'historique
            if (lastIndexPositions.size > maxHistorySize) {
                lastIndexPositions.removeAt(0)
            }
            
            // Détecter les changements de direction
            if (lastIndexPositions.size >= 3) {
                var directionChanges = 0
                var previousDirection = 0
                
                for (i in 2 until lastIndexPositions.size) {
                    val movement = lastIndexPositions[i] - lastIndexPositions[i-1]
                    
                    // Si le mouvement est significatif
                    if (Math.abs(movement) > movementThreshold) {
                        val currentDirection = if (movement > 0) 1 else -1
                        
                        // Si on a changé de direction
                        if (previousDirection != 0 && currentDirection != previousDirection) {
                            directionChanges++
                        }
                        
                        previousDirection = currentDirection
                    }
                }
                
                // Si on a détecté assez de changements de direction
                if (directionChanges >= requiredDirectionChanges) {
                    onResult("Non en LSF")
                    lastIndexPositions.clear()
                    lastDetectedNonTime = currentTime
                }
            }
        } else {
            // Réinitialiser si l'index n'est plus pointé
            lastIndexPositions.clear()
        }
    }

    private fun detectZGesture(landmarks: List<NormalizedLandmark>, currentTime: Long) {
        if (landmarks.size < 21 || currentTime - lastDetectedZTime < zDetectionCooldown) return
        
        if (isIndexPointing(landmarks)) {
            val indexTip = landmarks[8]
            // Stocker les positions X et Y
            indexPositionsForZ.add(Pair(indexTip.x(), indexTip.y()))
            
            // Limiter l'historique
            if (indexPositionsForZ.size > maxZHistorySize) {
                indexPositionsForZ.removeAt(0)
            }
            
            // Vérifier si on a assez de points
            if (indexPositionsForZ.size >= 10) {
                // Détecter le mouvement en Z
                if (isZMovement()) {
                    onResult("Lettre Z")
                    indexPositionsForZ.clear()
                    lastDetectedZTime = currentTime
                }
            }
        } else {
            indexPositionsForZ.clear()
        }
    }

    private fun isZMovement(): Boolean {
        // On a besoin d'au moins 2 changements de direction horizontale
        var directionChangesX = 0
        var previousDirectionX = 0
        
        // Et d'un mouvement général de haut en bas
        val startY = indexPositionsForZ.first().second
        val endY = indexPositionsForZ.last().second
        val downwardMovement = endY > startY + 0.1f // Un mouvement significatif vers le bas
        
        for (i in 1 until indexPositionsForZ.size) {
            val movementX = indexPositionsForZ[i].first - indexPositionsForZ[i-1].first
            
            if (Math.abs(movementX) > zMovementThreshold) { // Mouvement horizontal significatif
                val currentDirectionX = if (movementX > 0) 1 else -1
                
                if (previousDirectionX != 0 && currentDirectionX != previousDirectionX) {
                    directionChangesX++
                }
                
                previousDirectionX = currentDirectionX
            }
        }
        
        return directionChangesX >= 2 && downwardMovement
    }

    private fun handleStaticGesture(gesture: GestureType) {
        val message = when (gesture) {
            GestureType.AMOUR -> "Je t'aime en LSF"
            GestureType.AU_REVOIR -> "Au revoir/Bonjour en LSF"
            GestureType.PAS_BIEN -> "Ça va pas en LSF"
            GestureType.OUI -> "Oui en LSF"
            GestureType.MERCI -> "Merci en LSF"
            GestureType.MOI -> "Moi en LSF"
            GestureType.NON -> "Non en LSF"
            GestureType.DIRE -> "Dire en LSF"
            GestureType.HELLO_ASL -> "Assalam Alaikoum en LSA"
            GestureType.APPRENDRE -> "Apprendre en LSF"
            GestureType.NOM -> "Nom en LSF"
            GestureType.STYLO_ASL -> "Stylo en ASL"
            GestureType.PLEASE -> "S'il te plaît en LSA"
            GestureType.A -> "Lettre A"
            GestureType.B -> "Lettre B"
            GestureType.C -> "Lettre C"
            GestureType.D -> "Lettre D"
            GestureType.E -> "Lettre E"
            GestureType.F -> "Lettre F"
            GestureType.G -> "Lettre G"
            GestureType.H -> "Lettre H"
            GestureType.I -> "Lettre I"
            GestureType.J -> "Lettre J"
            GestureType.K -> "Lettre K"
            GestureType.L -> "Lettre L"
            GestureType.M -> "Lettre M"
            GestureType.N -> "Lettre N"
            GestureType.O -> "Lettre O"
            GestureType.P -> "Lettre P"
            GestureType.Q -> "Lettre Q"
            GestureType.R -> "Lettre R"
            GestureType.S -> "Lettre S"
            GestureType.T -> "Lettre T"
            GestureType.U -> "Lettre U"
            GestureType.V -> "Lettre V"
            GestureType.W -> "Lettre W"
            GestureType.X -> "Lettre X"
            GestureType.Y -> "Lettre Y"
            GestureType.Z -> "Lettre Z"
            else -> "Geste non reconnu"
        }
        
        onResult(message)
    }

    private fun resetDetection() {
        lastDetectedGesture = GestureType.NONE
        consecutiveDetections = 0
    }

    private fun isGestureRecognized(score: Float): Boolean {
        return score >= GESTURE_THRESHOLD
    }

    fun close() {
        gestureRecognizer?.close()
    }

    private fun isIndexPointing(landmarks: List<NormalizedLandmark>): Boolean {
        // Points de l'index
        val indexTip = landmarks[8]
        val indexPip = landmarks[7]
        val indexMcp = landmarks[5]

        // Points des autres doigts
        val thumbTip = landmarks[4]
        val middleTip = landmarks[12]
        val ringTip = landmarks[16]
        val pinkyTip = landmarks[20]
        
        // Vérifier si l'index est tendu et les autres doigts sont pliés
        val isIndexStraight = indexTip.y() < indexPip.y() && indexPip.y() < indexMcp.y()
        
        // Assouplir la condition pour les autres doigts
        val areOtherFingersBent = middleTip.y() > indexPip.y() && 
                                 ringTip.y() > indexPip.y() && 
                                 pinkyTip.y() > indexPip.y()

        return isIndexStraight && areOtherFingersBent
    }
} 