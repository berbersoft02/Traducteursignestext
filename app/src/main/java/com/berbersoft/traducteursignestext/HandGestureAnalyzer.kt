package com.berbersoft.traducteursignestext

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import kotlin.math.abs

class HandGestureAnalyzer(
    private val onGestureDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private var lastDetectionTime = 0L
    private val minDetectionInterval = 500L // 500ms entre chaque détection
    private val confidenceThreshold = 0.5f // Seuil de confiance minimum

    private val poseDetector: PoseDetector = PoseDetection.getClient(
        PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
    )

    private fun countVisibleFingers(pose: com.google.mlkit.vision.pose.Pose): Int {
        var count = 0
        
        // Points de repère pour les doigts de la main droite
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
        val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
        val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)

        // Points de repère pour les doigts de la main gauche
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
        val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
        val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)

        // Vérification des doigts visibles de la main droite
        if (rightWrist != null && rightWrist.inFrameLikelihood > confidenceThreshold) {
            if (rightThumb != null && isFingerExtended(rightWrist, rightThumb)) count++
            if (rightIndex != null && isFingerExtended(rightWrist, rightIndex)) count++
            if (rightPinky != null && isFingerExtended(rightWrist, rightPinky)) {
                // Si le petit doigt est levé, on compte aussi l'annulaire et le majeur
                count += 3
            }
        }

        // Vérification des doigts visibles de la main gauche
        if (leftWrist != null && leftWrist.inFrameLikelihood > confidenceThreshold) {
            if (leftThumb != null && isFingerExtended(leftWrist, leftThumb)) count++
            if (leftIndex != null && isFingerExtended(leftWrist, leftIndex)) count++
            if (leftPinky != null && isFingerExtended(leftWrist, leftPinky)) {
                // Si le petit doigt est levé, on compte aussi l'annulaire et le majeur
                count += 3
            }
        }

        // Ajouter la détection du petit doigt pour les toilettes
        if (count == 1 && isOnlyPinkyRaised(pose)) {
            onGestureDetected("Toilettes")
            return 1
        }

        return minOf(count, 10) // On limite à 10 doigts maximum
    }

    private fun isFingerExtended(wrist: PoseLandmark, finger: PoseLandmark): Boolean {
        // Calcul de la distance verticale entre le poignet et le doigt
        val verticalDistance = abs(finger.position.y - wrist.position.y)
        // Si le doigt est plus haut que le poignet d'une certaine distance, on considère qu'il est levé
        return verticalDistance > 50 && finger.inFrameLikelihood > confidenceThreshold
    }

    // Ajouter cette fonction pour détecter spécifiquement le petit doigt
    private fun isOnlyPinkyRaised(pose: com.google.mlkit.vision.pose.Pose): Boolean {
        val landmarks = pose.allPoseLandmarks
        // Vérifier si seul le petit doigt est levé
        // Cette logique dépend de votre modèle de détection
        return true // À adapter selon votre modèle de détection
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastDetectionTime < minDetectionInterval) {
            imageProxy.close()
            return
        }

        imageProxy.image?.let { mediaImage ->
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            
            poseDetector.process(image)
                .addOnSuccessListener { pose ->
                    if (pose == null) {
                        onGestureDetected("Aucune pose détectée")
                    } else {
                        val fingerCount = countVisibleFingers(pose)
                        val message = when (fingerCount) {
                            0 -> "Aucun doigt détecté"
                            1 -> "Un doigt"
                            2 -> "Deux doigts"
                            3 -> "Trois doigts"
                            4 -> "Quatre doigts"
                            5 -> "Cinq doigts"
                            6 -> "Six doigts"
                            7 -> "Sept doigts"
                            8 -> "Huit doigts"
                            9 -> "Neuf doigts"
                            10 -> "Dix doigts"
                            else -> "Positionnez vos mains dans le champ"
                        }
                        onGestureDetected(message)
                    }
                    lastDetectionTime = currentTime
                }
                .addOnFailureListener { e ->
                    onGestureDetected("Erreur de détection: ${e.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } ?: imageProxy.close()
    }
} 