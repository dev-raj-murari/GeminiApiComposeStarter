package com.devraj.geminiassistant

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.devraj.geminiassistant.data.local.entity.ChatMessageEntity
import com.devraj.geminiassistant.ui.chat.ChatScreen
import com.devraj.geminiassistant.ui.chat.ChatUiState
import com.devraj.geminiassistant.ui.theme.GeminiAssistantTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun chatScreen_displaysWelcomePlaceholder_whenEmpty() {
        composeTestRule.setContent {
            GeminiAssistantTheme {
                ChatScreen(
                    state = ChatUiState(messages = emptyList()),
                    windowWidthSizeClass = WindowWidthSizeClass.Compact,
                    onPromptChange = {},
                    onSend = {},
                    onVoiceInputClick = {},
                    onClearChat = {},
                    onOpenSettings = {},
                    onCloseSettings = {},
                    onSaveSettings = { _, _ -> },
                    onDismissError = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Hello Devraj (N066)! How can I help you today?")
            .assertIsDisplayed()
    }

    @Test
    fun chatScreen_rendersMessages() {
        val messages = listOf(
            ChatMessageEntity(id = 1L, text = "Hello Gemini!", isUser = true),
            ChatMessageEntity(id = 2L, text = "Hello Devraj! How can I help you today?", isUser = false)
        )

        composeTestRule.setContent {
            GeminiAssistantTheme {
                ChatScreen(
                    state = ChatUiState(messages = messages),
                    windowWidthSizeClass = WindowWidthSizeClass.Compact,
                    onPromptChange = {},
                    onSend = {},
                    onVoiceInputClick = {},
                    onClearChat = {},
                    onOpenSettings = {},
                    onCloseSettings = {},
                    onSaveSettings = { _, _ -> },
                    onDismissError = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Hello Gemini!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello Devraj! How can I help you today?").assertIsDisplayed()
    }

    @Test
    fun chatScreen_voiceButton_isClickable() {
        var voiceClicked = false

        composeTestRule.setContent {
            GeminiAssistantTheme {
                ChatScreen(
                    state = ChatUiState(),
                    windowWidthSizeClass = WindowWidthSizeClass.Compact,
                    onPromptChange = {},
                    onSend = {},
                    onVoiceInputClick = { voiceClicked = true },
                    onClearChat = {},
                    onOpenSettings = {},
                    onCloseSettings = {},
                    onSaveSettings = { _, _ -> },
                    onDismissError = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Voice Input")
            .performClick()

        assert(voiceClicked)
    }
}
