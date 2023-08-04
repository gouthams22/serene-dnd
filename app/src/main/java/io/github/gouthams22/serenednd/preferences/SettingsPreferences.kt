package io.github.gouthams22.serenednd.preferences

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val USER_SETTINGS_PREFERENCES_NAME = "user_settings_preferences"
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = USER_SETTINGS_PREFERENCES_NAME)

class SettingsPreferences(lifecycleCoroutineScope: LifecycleCoroutineScope, context: Context) :
    PreferenceDataStore() {
    companion object {
        private const val TAG = "SettingsPreferences"
        val APP_THEME = stringPreferencesKey("night_theme")
    }

    private val scope = lifecycleCoroutineScope
    private val currentContext = context

    /**
     * store value to given key in [SettingsPreferences]
     * @param key key for which [value] is to be stored
     * @param value value to be stored for the given key
     */
    override fun putString(key: String?, value: String?) {
        Log.d(TAG, "putString: $value")
        scope
            .launch {
                currentContext.settingsDataStore.edit {
                    it[stringPreferencesKey(key.toString())] = value ?: ""
                }
            }
            .invokeOnCompletion {
                Log.d(TAG, "putString: ${it?.stackTrace ?: "$value stored successfully"}")
            }
    }

    /**
     * store Day/Night theme preference
     * @param theme theme preference to be stored
     */
    suspend fun storeTheme(theme: String) {
        currentContext.settingsDataStore.edit {
            it[APP_THEME] = theme
        }
    }

    /**
     * retrieve Day/Night theme preference
     * @return Day/Night theme preference
     */
    suspend fun getTheme(): String {
        Log.d(TAG, "getTheme: invoked")
        return currentContext.settingsDataStore.data.first()[APP_THEME]
            ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString()
    }

}