package com.fahim.geminiApiComposeStarter.data

import com.fahim.geminiApiComposeStarter.data.local.PreferencesManager
import com.fahim.geminiApiComposeStarter.data.local.dao.ChatMessageDao
import com.fahim.geminiApiComposeStarter.data.local.entity.ChatMessageEntity
import com.fahim.geminiApiComposeStarter.security.SecureKeyStorage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private const val DEFAULT_MODEL = "gemini-1.5-flash"

class GeminiRepositoryImpl(
    private val secureKeyStorage: SecureKeyStorage,
    private val chatMessageDao: ChatMessageDao,
    private val preferencesManager: PreferencesManager,
    private val modelName: String = DEFAULT_MODEL
) : GeminiRepository {

    override fun getMessagesFlow(): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getAllMessagesFlow()
    }

    override fun getSystemInstructionFlow(): Flow<String> {
        return preferencesManager.systemInstruction
    }

    override fun getTemperatureFlow(): Flow<Float> {
        return preferencesManager.temperature
    }

    override suspend fun updatePreferences(instruction: String, temperature: Float) {
        preferencesManager.savePreferences(instruction, temperature)
    }

    override suspend fun clearHistory() = withContext(Dispatchers.IO) {
        chatMessageDao.clearAllMessages()
    }

    override suspend fun generateText(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        val trimmedPrompt = prompt.trim()
        if (trimmedPrompt.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Prompt cannot be empty"))
        }

        // 1. Persist User message to Room database
        chatMessageDao.insertMessage(
            ChatMessageEntity(
                text = trimmedPrompt,
                isUser = true,
                timestamp = System.currentTimeMillis()
            )
        )

        // 2. Decrypt API key strictly in-memory
        val apiKey = secureKeyStorage.getDecryptedApiKey()
        if (apiKey.isBlank()) {
            val errorMsg = "GEMINI_API_KEY is missing. Please add it to local.properties."
            chatMessageDao.insertMessage(
                ChatMessageEntity(
                    text = errorMsg,
                    isUser = false,
                    isError = true
                )
            )
            return@withContext Result.failure(IllegalStateException(errorMsg))
        }

        try {
            val systemInstruction = preferencesManager.systemInstruction.first()
            val temperature = preferencesManager.temperature.first()

            val config = generationConfig {
                this.temperature = temperature
            }

            // Create GenerativeModel on demand
            val model = GenerativeModel(
                modelName = modelName,
                apiKey = apiKey,
                generationConfig = config,
                systemInstruction = content { text(systemInstruction) }
            )

            val response = model.generateContent(trimmedPrompt)
            val replyText = response.text ?: "No response from Gemini."

            // 3. Persist AI response to Room database
            chatMessageDao.insertMessage(
                ChatMessageEntity(
                    text = replyText,
                    isUser = false,
                    timestamp = System.currentTimeMillis()
                )
            )

            Result.success(replyText)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Failed to generate content"
            chatMessageDao.insertMessage(
                ChatMessageEntity(
                    text = "Error: $errorMsg",
                    isUser = false,
                    isError = true
                )
            )
            Result.failure(e)
        }
    }
}
