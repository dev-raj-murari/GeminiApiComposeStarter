package com.devraj.geminiassistant.data

import com.devraj.geminiassistant.data.local.PreferencesManager
import com.devraj.geminiassistant.data.local.dao.ChatMessageDao
import com.devraj.geminiassistant.data.local.entity.ChatMessageEntity
import com.devraj.geminiassistant.security.SecureKeyStorage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private const val DEFAULT_MODEL = "gemini-3.6-flash"

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

    override fun getSelectedModelFlow(): Flow<String> {
        return preferencesManager.selectedModel
    }

    override suspend fun updatePreferences(instruction: String, temperature: Float, model: String) {
        preferencesManager.savePreferences(instruction, temperature, model)
    }

    override suspend fun clearHistory() = withContext(Dispatchers.IO) {
        chatMessageDao.clearAllMessages()
    }

    override fun generateStream(prompt: String): Flow<String> = kotlinx.coroutines.flow.flow {
        val trimmedPrompt = prompt.trim()
        if (trimmedPrompt.isEmpty()) {
            throw IllegalArgumentException("Prompt cannot be empty")
        }

        // 1. Persist User message
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
            throw IllegalStateException(errorMsg)
        }

        val systemInstruction = preferencesManager.systemInstruction.first()
        val temperature = preferencesManager.temperature.first()
        val activeModelName = preferencesManager.selectedModel.first().ifBlank { modelName }

        val config = generationConfig {
            this.temperature = temperature
        }

        val model = GenerativeModel(
            modelName = activeModelName,
            apiKey = apiKey,
            generationConfig = config,
            systemInstruction = content { text(systemInstruction) }
        )

        val fullResponseBuilder = StringBuilder()
        try {
            val responseStream = model.generateContentStream(trimmedPrompt)
            responseStream.collect { chunk ->
                val chunkText = chunk.text ?: ""
                fullResponseBuilder.append(chunkText)
                emit(chunkText)
            }

            val finalReply = fullResponseBuilder.toString().ifBlank { "No response from Gemini." }
            chatMessageDao.insertMessage(
                ChatMessageEntity(
                    text = finalReply,
                    isUser = false,
                    timestamp = System.currentTimeMillis()
                )
            )
        } catch (e: CancellationException) {
            val partialReply = fullResponseBuilder.toString()
            if (partialReply.isNotBlank()) {
                chatMessageDao.insertMessage(
                    ChatMessageEntity(
                        text = "$partialReply\n\n[Generation stopped by user]",
                        isUser = false,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
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
            throw e
        }
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
            val activeModelName = preferencesManager.selectedModel.first().ifBlank { modelName }

            val config = generationConfig {
                this.temperature = temperature
            }

            val model = GenerativeModel(
                modelName = activeModelName,
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

