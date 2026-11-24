# Sign Language Translation System Architecture

Technical documentation of the real-time sign language recognition, voice-to-sign translation, and sequence synthesis architecture.

## Pipeline Architecture

```
                    ┌─────────────────────────┐
                    │ Camera Feed / CameraX   │
                    └────────────┬────────────┘
                                 │ Live frames
                                 ▼
                    ┌─────────────────────────┐
                    │ MediaPipe Hand Landmarker│
                    │ (21 3D hand coordinates) │
                    └────────────┬────────────┘
                                 │ Landmark vectors
                                 ▼
                    ┌─────────────────────────┐
                    │ HandGestureAnalyzer     │
                    │ & GestureRecognizer     │
                    └────────────┬────────────┘
                                 │ Recognized signs
                                 ▼
                    ┌─────────────────────────┐
                    │ Text & Voice Feedback   │
                    │ (Android TTS Output)    │
                    └─────────────────────────┘

                    ┌─────────────────────────┐
                    │ Spoken Voice / Text     │
                    └────────────┬────────────┘
                                 │ Natural language utterance
                                 ▼
                    ┌─────────────────────────┐
                    │ SignTranslationEngine   │
                    │ - Normalization & Regex │
                    │ - Greedy Compound Match │
                    │ - Multilingual Synonyms │
                    │ - Fingerspelling (LSF)  │
                    └────────────┬────────────┘
                                 │ Structured tokens
                                 ▼
                    ┌─────────────────────────┐
                    │ SignSequencePlayer      │
                    │ (State Machine, Timing, │
                    │  Speed, Looping, Stepper)│
                    └────────────┬────────────┘
                                 │ Timed sequence
                                 ▼
                    ┌─────────────────────────┐
                    │ Animated Sign Player UI │
                    │ (ExoPlayer Video / Res) │
                    └─────────────────────────┘
```

## Core Modules

### 1. Vision & Gesture Recognition Pipeline
- **Camera Feed:** CameraX streams live frames in real-time to the analyzer (`CameraActivity.kt`).
- **Landmark Extraction:** `GestureRecognizerHelper.kt` feeds frames to Google MediaPipe Hand Landmarker to extract 21 3D hand coordinates.
- **Gesture Classification:** `HandGestureAnalyzer.kt` normalizes coordinate landmarks and matches them against sign language vectors.
- **Text & Voice Feedback:** Classified gestures are translated into textual representations and spoken aloud via Android Text-to-Speech (TTS).

### 2. Natural Language Translation & Synthesis Engine
- **`SignTranslationEngine`:** Pure Kotlin domain engine that converts complete natural language sentences (French, English, Arabic) into LSF sign sequences.
  - **Greedy Compound Phrase Matching:** Resolves multi-word expressions (e.g., *"au revoir"*, *"s'il te plait"*, *"c'est bon"*, *"je n'ai pas compris"*, *"de rien"*) before individual words.
  - **Synonym Dictionary:** Multilingual lookup mapped across French, English, and Arabic terms.
  - **Fingerspelling (Dactylologie) Fallback:** Automatically decomposes unrecognized terms and proper nouns into LSF alphabet letter signs (`lettre a` through `lettre z`).
  - **`SignSequencePlayer`:** Pure state machine managing multi-sign playback, progress tracking, looping, navigation (next/prev/seek), and variable playback speeds (0.5x to 2.0x).
