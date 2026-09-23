# Gemini API Compose Starter

## Summary
Implemented the Gemini chat assignment using Kotlin and Jetpack Compose for Devraj Murari (N066).

## Features
- Material 3 chat bubbles with LazyColumn, stable IDs, and auto-scroll.
- StateFlow-based ViewModel and lifecycle-aware state collection (`collectAsStateWithLifecycle`).
- Adaptive layout using WindowSizeClass for compact and expanded displays.
- Loading indicators, input validation, and error states.
- Voice input modality using Android RecognizerIntent.
- Preferences DataStore for customizable system prompt and temperature settings.
- Persistent conversation history surviving process recreation and app restarts.
- Quick prompt suggestion chips for rapid interaction.

## API-Key Handling
- Local configuration in `local.properties` with environment-variable fallback for CI.
- Placeholder template in `local.properties.example`.
- Hardware-backed AES-256-GCM encryption using Android Keystore.
- Plaintext API key decrypted strictly in-memory upon GenerativeModel creation.
- Release build obfuscation enabled via R8 (`isMinifyEnabled = true`).

## Know the Limits (Production Security Analysis)
Client-side encryption raises the bar against static analysis but cannot fully conceal an API key from a determined attacker on a rooted device using dynamic instrumentation (e.g., Frida or Xposed). In a production deployment:
1. Gemini API calls should be routed through a backend proxy server (such as Ktor, Cloud Functions, or Cloud Run).
2. The backend server manages user authentication and securely accesses the API key via Google Cloud Secret Manager.
3. Requests from the Android client are validated using Firebase App Check and Google Play Integrity.

## Verification & Testing
- Unit tests: `./gradlew testDebugUnitTest` (All tests passed)
- Debug APK build: `./gradlew assembleDebug`
- Verified live response execution on Android Emulator (Pixel 8, API 37.1) using `gemini-3.6-flash`.
- Verified local offline persistence, system prompt customization, and history clearing.

<img width="1080" height="2400" alt="welcome_screen" src="https://github.com/user-attachments/assets/ec8ce314-792a-4869-b05d-12189f861ed9" />

<img width="1080" height="2400" alt="light" src="https://github.com/user-attachments/assets/228057a7-0350-4537-a9ae-678c2a7e9bff" />

<img width="1080" height="2400" alt="dark" src="https://github.com/user-attachments/assets/27d8bec0-e4d9-4b54-b483-31ae6d263a09" />





