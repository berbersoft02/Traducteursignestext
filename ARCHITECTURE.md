# Sign Language Translation System Architecture

Technical documentation of the real-time sign language recognition and synthesis architecture.

## Pipeline Flow

1. **Camera Feed:** CameraX streams live frames in real-time to the analyzer (`CameraActivity.kt`).
2. **Landmark Extraction:** `GestureRecognizerHelper.kt` feeds frames to Google MediaPipe Hand Landmarker to extract 21 3D hand coordinates.
3. **Gesture Classification:** `HandGestureAnalyzer.kt` normalizes coordinate landmarks and matches them against sign language vectors.
4. **Text & Voice Feedback:** Classified gestures are translated into textual representations and spoken aloud via Android Text-to-Speech (TTS).
