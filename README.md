# Gemini AI Assistant (Android Jetpack Compose)
### SVKM's NMIMS University — School of Technology Management & Engineering
**Course:** Mobile Application Development (`702AI0E002`)  
**Lab Assignment:** 1  
**Student Name:** Devraj Murari  
**Roll Number:** N066  
**Repository:** [https://github.com/dev-raj-murari/GeminiApiComposeStarter](https://github.com/dev-raj-murari/GeminiApiComposeStarter)  
**Package:** `com.devraj.geminiassistant`  

---

## 📌 Project Overview

An advanced, production-grade Android application developed by **Devraj Murari (N066)** utilizing **Jetpack Compose**, **Material 3**, **Room Database**, **Preferences DataStore**, and Google's **Gemini AI SDK** (`com.google.ai.client.generativeai`). The application implements secure API key management with hardware-backed **AES-256-GCM Keystore encryption**, persistent offline chat history, prompt suggestion chips, multimodal **Speech-to-Text (Voice) Input**, and responsive multi-window layouts with **WindowSizeClass**.

---

## 🚀 Getting Started & API Key Configuration

### 1. Obtain a Gemini API Key
1. Visit [Google AI Studio](https://aistudio.google.com/).
2. Create and copy your Gemini API key.

### 2. Configure Local Properties (Zero-Leakage Policy)
The API key is strictly separated from version control:
1. Open `local.properties` in the project root directory (included in `.gitignore`).
2. Add your API key:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```
3. A `local.properties.example` template is provided for reference.

### 3. CI/CD Environment Variable Fallback
For Continuous Integration pipelines, `app/build.gradle.kts` automatically falls back to system environment variables:
```kotlin
val geminiApiKey: String = localProperties.getProperty("GEMINI_API_KEY")
    ?: System.getenv("GEMINI_API_KEY") ?: ""
```

---

## 🔐 Security & Encryption-at-Rest Architecture

### Keystore Hardware-Backed Encryption Flow
```
+---------------------------+       +----------------------------+
|  BuildConfig (Build Time) | ----> |   SecureKeyStorage (Init)  |
+---------------------------+       +----------------------------+
                                                  |
                                                  v
                                    +----------------------------+
                                    | KeystoreHelper (AES-256)   |
                                    | - KeyGenParameterSpec      |
                                    | - BlockMode: GCM           |
                                    | - Padding: NoPadding       |
                                    +----------------------------+
                                                  |
                                                  v
                                    +----------------------------+
                                    | Encrypted Storage (Prefs)  |
                                    | Stores: Base64(IV + Cipher)|
                                    +----------------------------+
                                                  |
                                                  v (Only on API call)
                                    +----------------------------+
                                    | In-Memory Decryption       |
                                    | GenerativeModel Instance   |
                                    +----------------------------+
```

1. **Key Generation:** On first startup, `KeystoreHelper` generates an AES-256 key inside `AndroidKeyStore` with `PURPOSE_ENCRYPT | PURPOSE_DECRYPT`, `BLOCK_MODE_GCM`, and `ENCRYPTION_PADDING_NONE`.
2. **Encryption:** `BuildConfig.GEMINI_API_KEY` is encrypted with a randomized 12-byte IV and 128-bit authentication tag.
3. **Storage:** Only the resulting ciphertext and IV are persisted in private storage.
4. **On-Demand Decryption:** The plaintext key is decrypted into volatile memory **only** at the precise instant `GenerativeModel` is instantiated, and is never logged, toasted, or persisted.
5. **R8 Code Obfuscation:** Release builds enable `isMinifyEnabled = true` with ProGuard rules to obfuscate decompiled bytecode.

---

## 🛡️ Production Security: "Know the Limits"

While client-side Keystore encryption mitigates static inspection and file-extraction threats, client-side secrets can never be 100% hidden from a rooted device or determined reverse engineer with memory dumping tools (e.g., Frida, Xposed).

### Production Best Practices:
1. **Backend Proxy Architecture (Recommended):**
   - Route all Gemini queries through a backend server (e.g., Firebase Cloud Functions, Go/Ktor microservice).
   - The mobile client authenticates with Firebase Authentication (JWT token), and the backend server holds the Gemini API key securely in Secret Manager.
2. **Firebase App Check & Play Integrity:**
   - Enforce **Firebase App Check** with the **Play Integrity API** to verify that incoming requests originate solely from genuine, untampered instances of your app.
3. **Google Cloud Key Restrictions:**
   - Restrict the API key in Google Cloud Console by **Android App Package Name** (`com.devraj.geminiassistant`) and **SHA-256 Certificate Fingerprint**.

---

## 📱 Features & Jetpack Compose UI

### 1. Unidirectional Data Flow (State Hoisting)
- **`ChatUiState`**: Immutable state model encompassing messages, loading state, error states, and configuration.
- **`ChatViewModel`**: Exposes `StateFlow<ChatUiState>` using Kotlin Coroutines `stateIn()`.
- **`collectAsStateWithLifecycle()`**: Lifecycle-aware state consumption preventing background recompositions.

### 2. Material 3 Chat Experience
- **`LazyColumn`**: Message bubbles with stable item keys (`key = { message.id }`) for optimal recomposition performance.
- **Auto-scroll**: `rememberLazyListState()` smoothly animates to new messages with `LaunchedEffect`.
- **Material 3 Theming**: Automatic Light/Dark mode switching with `isSystemInDarkTheme()`.
- **Prompt Suggestion Chips:** One-tap prompt chips for quick exploration.

### 3. Multimodal Voice Input
- Integrated `RecognizerIntent.ACTION_RECOGNIZE_SPEECH` launched seamlessly using Compose's `rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult())`.

### 4. Offline Persistence & Customization
- **Room Database**: All chat dialogues (user prompts, AI responses, timestamps, and error flags) are saved in an offline SQLite database (`AppDatabase`).
- **Preferences DataStore**: Users can configure custom system instructions and adjust model temperature (creativity) via an in-app settings dialog.

### 5. Adaptive Responsive Layouts
- Utilizes `WindowSizeClass` (`WindowWidthSizeClass.Compact`, `Medium`, `Expanded`).
- **Compact (Phones)**: Full-screen chat layout with bottom input bar.
- **Medium / Expanded (Tablets / Landscape)**: Adaptive two-pane layout displaying AI system configurations and parameters on the left pane, with active chat on the right pane.

---

## 🧪 Testing Suite

### Running Unit Tests
```bash
./gradlew testDebugUnitTest
```

### Running Compose UI Tests
```bash
./gradlew connectedAndroidTest
```

---

## 📜 Submission Checklist
- [x] Branch name format: `N066_assignment_1`
- [x] Student: Devraj Murari (N066)
- [x] API key isolated from version control (`local.properties` in `.gitignore`)
- [x] `local.properties.example` template provided
- [x] Hardware-backed AES-256-GCM Keystore encryption at rest implemented
- [x] In-memory decryption at `GenerativeModel` creation
- [x] R8 release minification configured (`isMinifyEnabled = true`)
- [x] Jetpack Compose Material 3 UI with `LazyColumn` and stable keys
- [x] State hoisting with `ChatUiState` & `StateFlow`
- [x] Responsive layout with `WindowSizeClass`
- [x] Speech-to-text voice input implemented
- [x] Offline Room database persistence for chat history
- [x] Preferences DataStore for AI system instruction and temperature
- [x] Full unit test suite with `kotlinx-coroutines-test`
- [x] Full Compose UI test suite with `createComposeRule()`
- [x] Comprehensive security explanation in README
