package com.devraj.geminiassistant.ui.chat

import com.devraj.geminiassistant.data.local.entity.ChatMessageEntity

data class ChatUiState(
    val messages: List<ChatMessageEntity> = emptyList(),
    val prompt: String = "",
    val response: String = "",
    val isLoading: Boolean = false,
    val promptError: PromptError? = null,
    val errorMessage: String? = null,
    val systemInstruction: String = "You are an intelligent, concise, and helpful AI assistant.",
    val temperature: Float = 0.7f,
    val isSettingsOpen: Boolean = false
)

enum class PromptError { EMPTY }
