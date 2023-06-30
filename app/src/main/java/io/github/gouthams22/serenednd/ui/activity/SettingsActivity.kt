package io.github.gouthams22.serenednd.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.appbar.MaterialToolbar
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.preferences.SettingsPreferences
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_fragment_container, SettingsFragment()).commit()
        }

        val settingsToolbar: MaterialToolbar = findViewById(R.id.settings_toolbar)
        // Set back button
        settingsToolbar.navigationIcon =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_outline_arrow_back_24, theme)
        settingsToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        companion object {
            private const val TAG = "SettingsFragment"
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            val settingsPreferences = context?.let { SettingsPreferences(lifecycleScope, it) }
            preferenceManager.preferenceDataStore = settingsPreferences

            setPreferencesFromResource(R.xml.preferences_settings, rootKey)

            // Night Mode codes
            val nightModeTheme = resources.getStringArray(R.array.night_mode_theme)
            val nightModeValues = arrayOf(
                AppCompatDelegate.MODE_NIGHT_NO.toString(),
                AppCompatDelegate.MODE_NIGHT_YES.toString(),
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString()
            )

            // Night Mode ListPreferences
            val nightModePreference = preferenceScreen.findPreference<ListPreference>("night_theme")
            nightModePreference?.entries = nightModeTheme
            nightModePreference?.entryValues = nightModeValues

            // Set default value from preferences
            lifecycleScope.launch {
                nightModePreference?.value = settingsPreferences?.getTheme()
            }

            // Handle Night Mode ListPreference click
            nightModePreference?.setOnPreferenceChangeListener { _, newValue ->
                // Uncomment when feature is complete
                Log.d(TAG, "onCreatePreferences: $newValue")
                lifecycleScope
                    .launch {
                        settingsPreferences?.storeTheme(newValue.toString())
                    }
                    .invokeOnCompletion {
                        if (it == null) {
                            // Job successful, no error; update night mode
                            AppCompatDelegate.setDefaultNightMode(newValue.toString().toInt())
                            Log.d(TAG, "onCreatePreferences: Updated night mode theme preferences")
                        } else {
                            // Job unsuccessful, handle error
                            Log.d(
                                TAG,
                                "onCreatePreferences: couldn't update theme preference; Stacktrace: ${it.stackTrace}"
                            )
                        }
                    }
                true
            }

        }
    }
}