package io.github.gouthams22.serenednd.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val USER_DND_PREFERENCES_NAME = "user_dnd_preferences"
private val Context.dndDataStore: DataStore<Preferences> by preferencesDataStore(name = USER_DND_PREFERENCES_NAME)

class DNDPreference(val context: Context) {

    companion object {
        val TYPE = stringPreferencesKey("TYPE")
        val DURATION = stringPreferencesKey("DURATION")
        val LOCATION_STATUS = stringPreferencesKey("LOCATION_STATUS")
    }

    /**
     * Set DND type preference
     */
    suspend fun storeTypePreference(type: String) {
        context.dndDataStore.edit {
            it[TYPE] = type
        }
    }

    /**
     * Set DND duration preference
     */
    suspend fun storeDurationPreference(type: String) {
        context.dndDataStore.edit {
            it[DURATION] = type
        }
    }

    /**
     * Set Location status preference
     */
    suspend fun storeLocationStatusPreference(type: String) {
        context.dndDataStore.edit {
            it[LOCATION_STATUS] = type
        }
    }

    val typePreference: Flow<String> = context.dndDataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[TYPE] ?: "None"
        }

    val durationPreference: Flow<String> = context.dndDataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[DURATION] ?: "None"
        }

    val locationStatusPreference: Flow<String> = context.dndDataStore.data.catch { exception ->
        // dataStore.data throws an IOException when an error is encountered when reading data
        if (exception is IOException) {
            exception.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }
        .map { preferences ->
            preferences[LOCATION_STATUS] ?: ""
        }

    /**
     * Retrieve Location permission status preference once
     * @return Status of Location permission
     */
    suspend fun getLocationStatus(): String {
        return context.dndDataStore.data.first()[LOCATION_STATUS] ?: ""
    }
}