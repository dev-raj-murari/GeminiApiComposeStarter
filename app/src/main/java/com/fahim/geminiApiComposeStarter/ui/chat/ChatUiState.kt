package com.fahim.geminiApiComposeStarter.ui.chat

import com.fahim.geminiApiComposeStarter.data.local.entity.ChatMessageEntity

/** Immutable UI state for the conversation screen. */
data class ChatUiState(
    val messages: List<ChatMessageEntity> = emptyList(),
    val prompt: String = "",
    val response: String = "",
    val isLoading: Boolean = false,
    val promptError: PromptError? = null,
    val errorMessage: String? = null,
    val systemInstruction: String = "You are a helpful, concise, and knowledgeable AI assistant.",
    val temperature: Float = 0.7f,
    val isSettingsOpen: Boolean = false
)

enum class PromptError { EMPTY }
