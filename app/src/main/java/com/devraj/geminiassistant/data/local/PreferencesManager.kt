package com.devraj.geminiassistant.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "devraj_user_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        val SYSTEM_INSTRUCTION_KEY = stringPreferencesKey("system_instruction")
        val TEMPERATURE_KEY = floatPreferencesKey("temperature")

        const val DEFAULT_SYSTEM_INSTRUCTION = "You are an intelligent, concise, and helpful AI assistant."
        const val DEFAULT_TEMPERATURE = 0.7f
    }

    val systemInstruction: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SYSTEM_INSTRUCTION_KEY] ?: DEFAULT_SYSTEM_INSTRUCTION
    }

    val temperature: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[TEMPERATURE_KEY] ?: DEFAULT_TEMPERATURE
    }

    suspend fun savePreferences(systemInstruction: String, temperature: Float) {
        context.dataStore.edit { preferences ->
            preferences[SYSTEM_INSTRUCTION_KEY] = systemInstruction
            preferences[TEMPERATURE_KEY] = temperature
        }
    }
}
