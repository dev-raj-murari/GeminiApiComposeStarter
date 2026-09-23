## 🎓 Student Details
- **Student Name:** Devraj Murari
- **Roll Number:** N066
- **Subject:** Mobile Application Development (Subject Code: `702AI0E002`)
- **Branch:** `N066_assignment_1`
- **Fork Repository:** https://github.com/dev-raj-murari/GeminiApiComposeStarter

---

## 📌 PR Title
`N066: Lab Assignment 1 - Gemini AI Compose App Enhancements`

---

## 📝 Overview of Changes
This pull request implements all mandatory requirements and enhancements outlined in **Lab Assignment-1: Mobile Application Development**:

### 1. 🔐 API Key Security & Encryption-at-Rest
- **Zero-Leakage Policy:** API keys are never hardcoded or committed to git. Stored in `local.properties` (git-ignored) with a `local.properties.example` template provided.
- **CI/CD Fallback:** Added `System.getenv("GEMINI_API_KEY")` in `app/build.gradle.kts` for CI build support.
- **Android Keystore AES-256-GCM Encryption:** Hardware-backed encryption via `KeystoreHelper` and `SecureKeyStorage`. The API key is encrypted on first launch and stored as ciphertext at rest.
- **In-Memory Decryption:** Decrypted in RAM strictly at the moment `GenerativeModel` is instantiated. Decrypted values are never logged or toasted.
- **R8 Release Obfuscation:** Enabled `isMinifyEnabled = true` with ProGuard rules to protect the release APK.

### 2. 🎨 Jetpack Compose & Material 3 UI Enhancements
- **LazyColumn with Stable Keys:** Messages rendered as Material 3 chat bubbles with unique item keys (`key = { message.id }`) for optimal recomposition performance.
- **Animated Auto-Scroll:** Auto-scrolls to the latest message on arrival using `rememberLazyListState()` and `LaunchedEffect`.
- **State Hoisting (UDF):** UI state managed via `ChatUiState` and exposed as a `StateFlow` from `ChatViewModel`, collected with `collectAsStateWithLifecycle()`.
- **Quick Suggestion Chips:** Horizontal scrollable chips for one-tap prompt insertion.
- **Adaptive Responsive Layout:** Supports phones (single pane) and tablets/landscape (two-pane master-detail) using `WindowSizeClass`.
- **Dark Mode Support:** Material 3 dynamic color theming respecting `isSystemInDarkTheme()`.
- **Loading & Error Feedback:** `CircularProgressIndicator` during generation and `SnackbarHost` on errors.

### 3. 🎤 Advanced Modalities & Offline Persistence
- **Speech-to-Text (Voice Input):** Integrated `RecognizerIntent.ACTION_RECOGNIZE_SPEECH` launched with `rememberLauncherForActivityResult`.
- **Room Database Persistence:** Conversation history persists offline across app restarts using `AppDatabase`, `ChatMessageDao`, and `ChatMessageEntity`.
- **Preferences DataStore:** Customizable system instructions and temperature settings via `PreferencesManager` and `SettingsDialog`.

### 4. 🧪 Automated Testing
- **Unit Tests (`ChatViewModelTest.kt`):** Built with `kotlinx-coroutines-test` and `FakeGeminiRepository` verifying initial states, voice transcriptions, input mutations, failure fallbacks, chat clearing, and preference updates.
- **Compose UI Tests (`ChatScreenTest.kt`):** Built with `createComposeRule()` verifying UI components, bubble rendering, text input interactions, and voice click events.

---

## 📸 Screenshots / Proof of Work

| Mobile View | Suggestion Chips & Input | AI Settings Dialog |
| :---: | :---: | :---: |
| *(Attach screenshot)* | *(Attach screenshot)* | *(Attach screenshot)* |

| Tablet / Landscape Two-Pane | Voice Input | Unit Test Results |
| :---: | :---: | :---: |
| *(Attach screenshot)* | *(Attach screenshot)* | *(Attach screenshot)* |

---

## ✅ Submission Checklist
- [x] Branch name starts with roll number (`N066_assignment_1`)
- [x] No API keys or `local.properties` staged or present in commit history
- [x] `local.properties.example` template provided
- [x] Hardware Keystore AES-256-GCM encryption implemented
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
- [x] Comprehensive documentation in `README.md` and `N066_MAD_ASSIGNMENT01_GEMINICHATBOT.md`
