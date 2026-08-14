# Sign Language Translator App 🤟

## About This Project

I developed this Android application to bridge the communication gap between sign language users and non-signers. This app translates sign language gestures into text and converts voice/text into visual sign language representations, making communication more accessible for the deaf and hard-of-hearing community.

## My Motivation

I created this app because I believe communication should be accessible to everyone. By leveraging modern mobile technology and machine learning, I wanted to build a tool that empowers sign language users and helps others learn and communicate in sign language.

## What I Built

### Core Features

**1. Sign Language to Text Translation**
- I implemented real-time hand gesture recognition using the device camera
- I integrated MediaPipe's Gesture Recognizer for accurate hand tracking
- I built a custom gesture analyzer that interprets hand movements and converts them to text
- I added support for multiple sign language alphabets and common phrases

**2. Voice/Text to Sign Language**
- I developed a voice-to-sign feature that captures spoken words and displays corresponding sign language animations
- I created a comprehensive library of sign language videos for French sign language (LSF)
- I included sign representations for:
  - Alphabet (A-Z)
  - Numbers (0-100+)
  - Common phrases and greetings
  - Days of the week
  - Essential vocabulary

**3. Interactive Learning Mode**
- I designed a gesture training system where users can practice and improve their signing
- I added real-time feedback to help users perfect their hand shapes and movements
- I built progress tracking to monitor learning advancement

**4. Multilingual Support**
- I implemented support for French, English, and Kabyle (Berber) languages
- I created a language selection system for seamless switching between interfaces

### Technical Implementation

**Technologies I Used:**
- **Kotlin** - I chose Kotlin for its modern syntax and Android-first approach
- **Jetpack Compose** - I built the entire UI using Compose for a modern, reactive interface
- **CameraX** - I integrated CameraX for efficient camera handling and preview
- **MediaPipe** - I utilized Google's MediaPipe for robust hand gesture recognition
- **Material Design 3** - I implemented Material You design principles for a polished look
- **Text-to-Speech** - I added TTS functionality for voice output

**Architecture:**
- I followed the MVVM (Model-View-ViewModel) pattern for clean separation of concerns
- I created reusable UI components for consistent design
- I implemented custom animation utilities for smooth transitions
- I built a theme system supporting multiple color schemes

## My Development Process

### Challenges I Overcame

1. **Real-time Gesture Recognition**: I optimized the ML model to run efficiently on mobile devices without draining battery
2. **Large Media Files**: I managed 60+ video files for sign demonstrations while keeping the app size reasonable
3. **Multilingual UI**: I created a flexible localization system supporting RTL languages
4. **Accessibility**: I ensured the app itself is accessible to users with different abilities

### Features I'm Proud Of

- **Smooth Camera Integration**: I built a robust camera system that handles permissions gracefully and provides clear visual feedback
- **Beautiful Animations**: I crafted engaging particle effects and transitions that make the app delightful to use
- **Comprehensive Sign Library**: I curated and integrated an extensive collection of sign language demonstrations
- **Intuitive UX**: I designed an interface that's easy to navigate for both experienced signers and beginners

## Project Structure

```
app/src/main/java/com/berbersoft/traducteursignestext/
├── CameraActivity.kt              # Camera and gesture recognition
├── GestureRecognizerHelper.kt     # ML model integration
├── HandGestureAnalyzer.kt         # Gesture interpretation logic
├── VoiceToSignActivity.kt         # Voice input and sign display
├── VoiceToSignManager.kt          # Voice recognition management
├── GestureTrainingActivity.kt     # Interactive learning mode
├── HomeActivity.kt                # Main navigation hub
├── LanguageSelectionActivity.kt   # Language switching
├── ui/
│   ├── theme/                     # Custom theming
│   └── components/                # Reusable UI components
├── models/
│   └── GestureType.kt            # Gesture data models
└── utils/
    ├── AnimationUtils.kt         # Animation helpers
    └── ThemeHelper.kt            # Theme management
```

## How to Use

### Prerequisites
- Android 8.0 (API 26) or higher
- Camera permissions for sign language recognition
- Microphone permissions for voice input

### Installation
1. Clone this repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run on an emulator or physical device

### Usage

**Sign to Text Mode:**
1. I designed it so you tap the "Sign Language to Text" button
2. Grant camera permissions when prompted
3. Show sign language gestures to the camera
4. The app displays recognized text in real-time

**Voice/Text to Sign Mode:**
1. Tap the "Voice to Sign" button
2. Speak or type your message
3. The app displays corresponding sign language animations
4. Play videos to see proper hand movements

## Technical Specifications

- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **ML Framework**: MediaPipe Gesture Recognition
- **Architecture**: MVVM

## What I Learned

Through building this app, I:
- Gained expertise in integrating ML models into Android apps
- Mastered Jetpack Compose for building complex UIs
- Learned to optimize camera processing for real-time performance
- Developed skills in accessibility-focused design
- Improved my understanding of sign language and deaf culture

## Future Improvements I'm Planning

- [ ] Add more sign language dialects (ASL, BSL, etc.)
- [ ] Implement sentence-level translation instead of word-by-word
- [ ] Add social features for practicing with others
- [ ] Create offline mode with downloaded sign language packs
- [ ] Integrate AI for more natural gesture interpretation
- [ ] Add gamification elements to make learning more engaging

## Contributing

I welcome contributions! If you'd like to improve this app:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

This project is open source. Feel free to use and modify it for your own purposes.

## Contact

I'd love to hear your feedback or discuss collaboration opportunities!

**Developer**: Said Ahrikenchikh  
**GitHub**: [@berbersoft02](https://github.com/berbersoft02)

---

*I built this app with the goal of making communication more inclusive. I hope it helps bridge gaps and fosters understanding between sign language users and the broader community.*
