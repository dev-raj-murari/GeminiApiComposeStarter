## Summary
Implemented the Gemini chat assignment using Kotlin and Jetpack Compose for **Devraj Murari (N066)**.

## Features
- Material 3 chat bubbles with LazyColumn, stable IDs, and auto-scroll.
- StateFlow-based ViewModel and lifecycle-aware state collection.
- Adaptive layout using WindowSizeClass.
- Loading indicators, validation, and error messages.
- Voice input using RecognizerIntent.
- System, Light, and Dark theme selection.
- DataStore preferences and persistent SQLite/Room conversation history.
- Quick suggestion prompt chips.

## API-Key Handling
- Local configuration with an environment-variable fallback.
- Placeholder-only local.properties.example.
- AES-256-GCM encryption using Android Keystore.
- Encrypted key storage excluded from backup.
- R8 enabled for release builds.
- README explains client-side key limitations and production alternatives.

## Verification
- ViewModel unit tests passed (100% green).
- Release build and debug APK assembled successfully.
- Live responses verified on Android Emulator with `gemini-3.6-flash`.
- Phone, landscape, and tablet layouts checked.
- Theme and history persistence verified.

## Screenshots
Formal report and screenshots are included in `N066_MAD_ASSIGNMENT01_GEMINICHATBOT.docx` and the repository.
