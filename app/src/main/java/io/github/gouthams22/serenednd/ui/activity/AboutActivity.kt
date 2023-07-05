package io.github.gouthams22.serenednd.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.serenednd.BuildConfig
import io.github.gouthams22.serenednd.R

/*
Tasks:
1. Research Custom Tabs
2. Research Deep links
 */
class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        findViewById<MaterialTextView>(R.id.text_version_name).text = BuildConfig.VERSION_NAME

        findViewById<ImageView>(R.id.logo_github).setOnClickListener {
            openLink(gitHubLink)
        }

        findViewById<ImageView>(R.id.logo_linkedin).setOnClickListener {
            openLink(linkedInLink)
        }

        findViewById<ImageView>(R.id.logo_mail).setOnClickListener {
            openLink(developerMailId, true)

        }

    }

    private fun openLink(url: String, isMailId: Boolean = false) {
        Log.d(TAG, "openLink: $url isvalidHttps? ${URLUtil.isHttpsUrl(url)}")
        val intent: Intent =
            if (isMailId) {
                Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(developerMailId))
                    putExtra(Intent.EXTRA_SUBJECT, "Serene DND Feedback")
                }
            } else {
                if (URLUtil.isHttpsUrl(url)) {
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                    )
                } else Intent()
            }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    companion object {

        private const val linkedInLink: String = "https://linkedin.com/in/gouthams0922"
        private const val gitHubLink: String = "https://github.com/gouthams22"
        private const val developerMailId = "goutham22dev@gmail.com"

        private const val TAG: String = "AboutActivity"
    }
}