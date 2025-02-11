package com.example.examine.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.examine.utils.UserPreferences.Companion.userPreferencesName
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = userPreferencesName)

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    fun getAccessToken() = dataStore.data.map { it[PREF_ACCESS_TOKEN] ?: preferenceDefaultValue }
    fun getUsername() = dataStore.data.map { it[PREF_USERNAME] ?: preferenceDefaultValue }
    fun getEmail() = dataStore.data.map { it[PREF_EMAIL] ?: preferenceDefaultValue }
    fun getImei() = dataStore.data.map { it[PREF_IMEI] ?: preferenceDefaultValue }

    suspend fun savePreferences(
        accessToken: String,
        username: String,
        imei: String,
        email: String,
    ) {
        dataStore.edit { prefs ->
            prefs[PREF_ACCESS_TOKEN] = accessToken
            prefs[PREF_USERNAME] = username
            prefs[PREF_IMEI] = imei
            prefs[PREF_EMAIL] = email
        }
    }

    suspend fun clearPreferences() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>) = INSTANCE ?: synchronized(this) {
            val instance = UserPreferences(dataStore)
            INSTANCE = instance
            instance
        }

        const val userPreferencesName = "userPreferences"

        val PREF_ACCESS_TOKEN = stringPreferencesKey("pref_access_token")
        val PREF_USERNAME = stringPreferencesKey("pref_username")
        val PREF_IMEI = stringPreferencesKey("pref_imei")
        val PREF_EMAIL = stringPreferencesKey("pref_email")

        const val preferenceDefaultValue: String = "preferences_default_value"
    }
}