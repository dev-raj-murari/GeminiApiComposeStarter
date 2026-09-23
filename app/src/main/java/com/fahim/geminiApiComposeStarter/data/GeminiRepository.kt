package com.fahim.geminiApiComposeStarter.data

import com.fahim.geminiApiComposeStarter.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction over Gemini Generative AI operations and persistent chat storage.
 */
interface GeminiRepository {
    fun getMessagesFlow(): Flow<List<ChatMessageEntity>>
    suspend fun generateText(prompt: String): Result<String>
    suspend fun clearHistory()
    fun getSystemInstructionFlow(): Flow<String>
    fun getTemperatureFlow(): Flow<Float>
    suspend fun updatePreferences(instruction: String, temperature: Float)
}
