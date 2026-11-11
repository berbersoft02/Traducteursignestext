# Sign Language Translator (Android)

Native Android app that translates sign language into text and speech in real time using the phone camera. Built as my Master's project in Embedded Systems Electronics (UMMTO, 2025). Awarded the national **"Innovative Project"** label (19/20).

## Highlights

- Real-time hand gesture recognition on-device (camera -> text -> speech)
- Custom-trained MediaPipe Gesture Recognizer, **>90% accuracy**
- Custom dataset of **~3,000 images**, collected and prepared with Python scripts
- Algerian and French sign language alphabets (largely similar)
- Voice/text to sign: video library for letters, numbers and common phrases
- Gesture training mode, UI in French, English and Kabyle

## Demo

A demo video is included in the app (see the Voice to Sign and Gesture Training screens).

## How it works

1. CameraX streams frames from the phone camera.
2. MediaPipe Gesture Recognizer (custom-trained model) detects hand landmarks and classifies the gesture.
3. HandGestureAnalyzer turns recognised gestures into text.
4. Android Text-to-Speech reads the text aloud.

## Dataset and training

- ~3,000 labelled hand-gesture images gathered and processed with Python scripts
- Model trained on Google Colab (Python), then exported for on-device inference in the Android app
- Accuracy above 90% on our evaluation set

## Tech stack

Kotlin, Jetpack Compose, CameraX, MediaPipe, Material Design 3, Android TTS, MVVM. Python and Google Colab for dataset preparation and training. Min SDK 26, target SDK 34.

## Run it

1. Clone the repository
2. Open it in Android Studio and sync Gradle
3. Run on a physical device (camera and microphone permissions required)

## Author

Said Ahrikenchikh - Embedded Systems & AI Engineer - [GitHub](https://github.com/berbersoft02)
