# PR: Enhanced Gemini AI Assistant with Hardware Keystore AES-256 Encryption, Room Persistence & Voice STT

**Author:** Devraj Murari ([@dev-raj-murari](https://github.com/dev-raj-murari))  
**Source Branch:** `dev-raj-murari:devraj_murari_assignment_1` (or `<RollNumber>_assignment_1`)  
**Target Repository:** `ifahimkhan/GeminiApiComposeStarter:master`  
**Course:** Mobile Application Development (`702AI0E002`) — SVKM's NMIMS STME  
**Assignment:** Lab Assignment 1  

---

## 📋 Summary of Changes

This pull request completes all requirements specified in **Lab Assignment-1**, significantly enhancing the Gemini Jetpack Compose starter app into a secure, reactive, persistent, and multimodal Android application.

---

## ✨ Key Features & Enhancements

### 1. 🔐 API Key Security & Hardware Encryption-at-Rest
- **Zero-Leakage Policy:** API keys are never hardcoded or committed. Managed via `local.properties` (git-ignored) with a template in `local.properties.example`.
- **CI/CD Fallback:** Added dynamic environment variable fallback `System.getenv("GEMINI_API_KEY")` in `app/build.gradle.kts`.
- **Hardware-Backed AES-256-GCM Encryption:** Implemented `KeystoreHelper` utilizing `AndroidKeyStore` (`KeyGenParameterSpec`) with `AES/GCM/NoPadding`. On initial app run, the key is encrypted and only the ciphertext + IV are saved in private storage.
- **In-Memory Decryption:** Plaintext API key is decrypted strictly into volatile memory at the instant `GenerativeModel` is instantiated. Decrypted values are never logged, toasted, or persisted.
- **R8 Obfuscation:** Enabled `isMinifyEnabled = true` in release builds with custom ProGuard rules.

### 2. 🎨 UI/UX Enhancements (Jetpack Compose & Material 3)
- **LazyColumn with Stable Keys:** Messages rendered as Material 3 chat bubbles with unique item keys (`key = { message.id }`) for zero redundant recompositions.
- **Animated Auto-Scroll:** Synchronized auto-scrolling to the latest message using `rememberLazyListState()` and `LaunchedEffect`.
- **State Hoisting:** Unidirectional Data Flow (UDF) with `ChatUiState` exposed as a `StateFlow` from `ChatViewModel` and consumed using `collectAsStateWithLifecycle()`.
- **Responsive / Adaptive Layout:** Utilizes `WindowSizeClass` to render single-pane layout on mobile phones and adaptive two-pane master-detail view on tablets and landscape orientations.
- **Dark Mode Support:** Implemented Material 3 dynamic color theming respecting `isSystemInDarkTheme()`.
- **Loading & Error Feedback:** Animated progress indicator during API streaming and `SnackbarHost` on network or API failures.

### 3. 🎤 Advanced Multimodal Functionality & Persistence
- **Speech-to-Text (Voice Input):** Integrated `RecognizerIntent.ACTION_RECOGNIZE_SPEECH` using Compose's `rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult())`.
- **Room Database Persistence:** Offline storage with `AppDatabase`, `ChatMessageDao`, and `ChatMessageEntity`. Chat dialogues survive app restarts and process recreation.
- **Preferences DataStore:** Integrated `PreferencesManager` and `SettingsDialog` allowing users to customize AI system instructions and model creativity (temperature).

### 4. 🧪 Comprehensive Automated Testing
- **Unit Tests (`ChatViewModelTest.kt`):** Built with `kotlinx-coroutines-test` and `FakeGeminiRepository` verifying initial states, voice transcriptions, input mutations, failure fallbacks, chat clearing, and preference updates.
- **Compose UI Tests (`ChatScreenTest.kt`):** Built with `createComposeRule()` verifying UI components, bubble rendering, text input interactions, and voice click events.

---

## 🛡️ Production Security Discussion ("Know the Limits")
In accordance with the lab assignment:
- Client-side encryption with Keystore elevates security against file extraction and static decompilation, but cannot prevent memory inspection on rooted devices.
- For production releases, Gemini calls should be proxied through a backend service (e.g. Firebase Cloud Functions / Ktor) holding the secret in Cloud Secret Manager, protected by **Firebase App Check** (Play Integrity API) and Google Cloud API key restrictions (Package name + SHA-256).

---

## ✅ Submission Checklist
- [x] Branch name starts with roll number / student identifier
- [x] No API keys or `local.properties` staged or present in commit history
- [x] `local.properties.example` template provided
- [x] Hardware Keystore AES-256-GCM encryption implemented
- [x] R8 release minification configured
- [x] Jetpack Compose Material 3 UI with LazyColumn and auto-scroll
- [x] State hoisting with `ChatUiState` & `StateFlow`
- [x] Responsive layout with `WindowSizeClass`
- [x] Speech-to-text voice input implemented
- [x] Room database offline persistence
- [x] Preferences DataStore configuration
- [x] Unit and Compose UI test suites written and passing
- [x] Full documentation in `README.md`
