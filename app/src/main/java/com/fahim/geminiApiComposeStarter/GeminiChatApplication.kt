package com.fahim.geminiApiComposeStarter

import android.app.Application
import com.fahim.geminiApiComposeStarter.data.GeminiRepository
import com.fahim.geminiApiComposeStarter.data.GeminiRepositoryImpl
import com.fahim.geminiApiComposeStarter.data.local.AppDatabase
import com.fahim.geminiApiComposeStarter.data.local.PreferencesManager
import com.fahim.geminiApiComposeStarter.security.SecureKeyStorage

class GeminiChatApplication : Application() {

    lateinit var secureKeyStorage: SecureKeyStorage
        private set

    lateinit var database: AppDatabase
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var repository: GeminiRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // 1. Hardware Keystore initialization & API Key encryption
        secureKeyStorage = SecureKeyStorage(this)

        // 2. Room Database initialization
        database = AppDatabase.getDatabase(this)

        // 3. Preferences DataStore
        preferencesManager = PreferencesManager(this)

        // 4. Repository with in-memory decryption
        repository = GeminiRepositoryImpl(
            secureKeyStorage = secureKeyStorage,
            chatMessageDao = database.chatMessageDao(),
            preferencesManager = preferencesManager
        )
    }
}
