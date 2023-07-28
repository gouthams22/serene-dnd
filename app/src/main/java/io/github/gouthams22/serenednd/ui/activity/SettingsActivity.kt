package io.github.gouthams22.serenednd.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import coil.load
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.preferences.SettingsPreferences
import io.github.gouthams22.serenednd.util.DimensionConverter
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
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

        // Get Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance()

        // User profile picture
        Log.d(TAG, "onCreate: User pfp:${firebaseAuth.currentUser?.photoUrl}")
        val imageView: ImageView = findViewById(R.id.image_user_logo)
        imageView.load(firebaseAuth.currentUser?.photoUrl) {
            placeholder(R.drawable.ic_outline_account_circle_72)
            error(R.drawable.ic_outline_account_circle_72)
            size(
                DimensionConverter.toPx(applicationContext, 72f).toInt(),
                DimensionConverter.toPx(applicationContext, 72f).toInt()
            )
        }

        // User account email ID
        Log.d(TAG, "onCreate: User EmailID: ${firebaseAuth.currentUser?.email ?: "error"}")
        val emailAccountTextView: MaterialTextView = findViewById(R.id.text_email_account)
        emailAccountTextView.text = firebaseAuth.currentUser?.email ?: "error"

        val logoutButton: MaterialButton = findViewById(R.id.button_logout_settings)
        logoutButton.setOnClickListener {
            it.isEnabled = false
            Log.d(TAG, "onCreate: Log out button clicked")
            firebaseAuth.signOut()
            Log.d(TAG, if (firebaseAuth.currentUser != null) "Still signed in" else "Nope")
            finish()
            it.isEnabled = true
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

    companion object {
        private const val TAG = "SettingsActivity"
    }
}