package io.github.gouthams22.serenednd.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.preferences.SettingsPreferences

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_fragment_container, SettingsFragment()).commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        companion object {
            private const val TAG = "SettingsFragment"
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore =
                context?.let { SettingsPreferences(lifecycleScope, it) }
            setPreferencesFromResource(R.xml.preferences_settings, rootKey)
            //TODO implement settings related features
            val nightModeTheme = resources.getStringArray(R.array.night_mode_theme)
            val nightModeCode = arrayOf(
                AppCompatDelegate.MODE_NIGHT_NO,
                AppCompatDelegate.MODE_NIGHT_YES,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
            val nightModeValues = arrayOf(
                AppCompatDelegate.MODE_NIGHT_NO.toString(),
                AppCompatDelegate.MODE_NIGHT_YES.toString(),
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString()
            )
            val nightModePreference = preferenceScreen.findPreference<ListPreference>("app_theme")
            nightModePreference?.entries = nightModeTheme
            nightModePreference?.entryValues = nightModeValues
            nightModePreference?.setOnPreferenceChangeListener { _, newValue ->
                // Uncomment when feature is complete
//                Log.d(TAG, "onCreatePreferences: $newValue")
//                AppCompatDelegate.setDefaultNightMode(newValue.toString().toInt())
                true
            }
        }
    }
}