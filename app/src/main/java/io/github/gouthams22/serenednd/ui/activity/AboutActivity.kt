package io.github.gouthams22.serenednd.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.serenednd.BuildConfig
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.SereneCustomTabs
import kotlinx.coroutines.launch


/*
Tasks:
1. Research Deep links
 */
class AboutActivity : AppCompatActivity() {

    private class AboutAdapter(list: ArrayList<String>) :
        RecyclerView.Adapter<AboutAdapter.ViewHolder>() {
        val contentList: ArrayList<String> = list
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_text_row, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.contentTextView.text = HtmlCompat.fromHtml(
                contentList[position],
                HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
            )
        }

        override fun getItemCount(): Int {
            return contentList.size
        }

        private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val contentTextView: MaterialTextView

            init {
                contentTextView = itemView.findViewById(R.id.text_item_content)
                contentTextView.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Resizing window to fit on system window size
        WindowCompat.setDecorFitsSystemWindows(window, false)

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

        // Github
        findViewById<ImageView>(R.id.logo_github).setOnClickListener {
            it.isEnabled = false
            openLink(gitHubLink)
            it.isEnabled = true
        }

        // LinkedIn
        findViewById<ImageView>(R.id.logo_linkedin).setOnClickListener {
            it.isEnabled = false
            openLink(linkedInLink)
            it.isEnabled = true
        }

        // Email
        findViewById<ImageView>(R.id.logo_mail).setOnClickListener {
            it.isEnabled = false
            openLink(developerMailId, true)
            it.isEnabled = true
        }

        // Privacy Policy
        findViewById<MaterialButton>(R.id.button_privacy_policy).setOnClickListener {
            it.isEnabled = false
            lifecycleScope.launch {
                setPrivacyPolicy()
            }
            it.isEnabled = true
        }

        // Open source license
        findViewById<MaterialButton>(R.id.button_open_source).setOnClickListener {
            it.isEnabled = false
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            it.isEnabled = true
        }

        // Set Layout Manager for Recycler View
        val aboutRecyclerView: RecyclerView = findViewById(R.id.recycler_view_about)
        findViewById<MaterialCardView>(R.id.card_privacy_policy).visibility = View.GONE
        val linearLayoutManager = LinearLayoutManager(this)
        aboutRecyclerView.layoutManager = linearLayoutManager
    }

    /**
     * Handles links appropriately
     * @param url Url of the website/mail to launch
     * @param isMailId whether the given url is a mail id or not
     */
    private fun openLink(url: String, isMailId: Boolean = false) {
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

    /**
     * Opens/closes Privacy Policy
     */
    private fun setPrivacyPolicy() {
        val policyList: ArrayList<String> = arrayListOf("<strong>Privacy Policy</strong><br>")

        var content = resources.getString(R.string.privacy_policy_txt)

        // Splits content string into array list
        while (content.isNotBlank() && content.isNotEmpty()) {
            var divIndex = content.indexOf("<div>")
            var ulIndex = content.indexOf("<ul>")
            var pIndex = content.indexOf("<p>")

            // to tackle -1 for indexOf()
            val maxVal = maxOf(divIndex, ulIndex, pIndex) + 1

            if (divIndex == -1 && ulIndex == -1 && pIndex == -1)
                break
            if (divIndex == -1) divIndex = maxVal
            if (ulIndex == -1) ulIndex = maxVal
            if (pIndex == -1) pIndex = maxVal

            val minVal = minOf(divIndex, ulIndex, pIndex)

            fun doIt(tag: String, start: Int, offset: Int): String = run {
                val end = content.indexOf(tag) + offset
                val smallContent = content.substring(start..end)
                content = content.substring(end)
                return smallContent
            }

            when (minVal) {
                divIndex -> policyList.add(doIt("</div>", divIndex, 6))
                ulIndex -> policyList.add(doIt("</ul>", ulIndex, 5))
                pIndex -> policyList.add(doIt("</p>", pIndex, 4))
            }
        }

        // sets recycler view
        val privacyCardView: MaterialCardView = findViewById(R.id.card_privacy_policy)
        val aboutRecyclerView: RecyclerView = findViewById(R.id.recycler_view_about)
        if (privacyCardView.visibility == View.VISIBLE)
            privacyCardView.visibility = View.GONE
        else
            privacyCardView.visibility = View.VISIBLE
        aboutRecyclerView.adapter = AboutAdapter(policyList)
    }

    companion object {

        private const val linkedInLink: String = "https://linkedin.com/in/gouthams0922"
        private const val gitHubLink: String = "https://github.com/gouthams22"
        private const val developerMailId: String = "goutham22dev@gmail.com"
    }
}