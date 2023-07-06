package io.github.gouthams22.serenednd.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.serenednd.BuildConfig
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.SereneCustomTabs

/*
Tasks:
1. Research Custom Tabs
2. Research Deep links
 */
class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val aboutToolbar: MaterialToolbar = findViewById(R.id.about_toolbar)
        // Set back button
        aboutToolbar.navigationIcon =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_outline_arrow_back_24, theme)
        aboutToolbar.setNavigationOnClickListener {
            finish()
        }

        // Set current version to version TextView
        "v${BuildConfig.VERSION_NAME}".also {
            findViewById<MaterialTextView>(R.id.text_version_name).text = it
        }

        findViewById<ImageView>(R.id.logo_github).setOnClickListener {
            it.isEnabled = false
            openLink(gitHubLink)
            it.isEnabled = true
        }

        findViewById<ImageView>(R.id.logo_linkedin).setOnClickListener {
            it.isEnabled = false
            openLink(linkedInLink)
            it.isEnabled = true
        }

        findViewById<ImageView>(R.id.logo_mail).setOnClickListener {
            it.isEnabled = false
            openLink(developerMailId, true)
            it.isEnabled = true
        }
    }

    /**
     * Handles links appropriately
     * @param url Url of the website/mail to launch
     * @param isMailId whether the given url is a mail id or not
     */
    private fun openLink(url: String, isMailId: Boolean = false) {
        Log.d(TAG, "openLink: $url isValidHttpsUrl? ${URLUtil.isHttpsUrl(url)}")

        if (isMailId) {
            // Mail intent
            val mailIntent =
                Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(developerMailId))
                    putExtra(Intent.EXTRA_SUBJECT, "Serene DND Feedback")
                }
            if (mailIntent.resolveActivity(packageManager) != null) {
                startActivity(mailIntent)
            }
        } else {
            // Custom Tabs intent
            if (URLUtil.isHttpsUrl(url)) {
                SereneCustomTabs(this).launchUrl(url, lifecycleScope)
            }
        }
    }

    companion object {

        private const val linkedInLink: String = "https://linkedin.com/in/gouthams0922"
        private const val gitHubLink: String = "https://github.com/gouthams22"
        private const val developerMailId = "goutham22dev@gmail.com"

        private const val TAG: String = "AboutActivity"
    }
}