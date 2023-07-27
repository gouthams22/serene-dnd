package io.github.gouthams22.serenednd.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpanned
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.serenednd.BuildConfig
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.SereneCustomTabs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL


/*
Tasks:
1. Research Custom Tabs
2. Research Deep links
 */
class AboutActivity : AppCompatActivity() {

    private class AboutAdapter(list: ArrayList<Spanned>) :
        RecyclerView.Adapter<AboutAdapter.ViewHolder>() {
        val contentList: ArrayList<Spanned> = list
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_text_row, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.contentTextView.text = contentList[position]
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

        // Flag for Privacy Policy and Open source license
        var flagRecycler = flagState[0]

        // Privacy Policy
        findViewById<MaterialButton>(R.id.button_privacy_policy).setOnClickListener {
            it.isEnabled = false
            lifecycleScope.launch {
                flagRecycler = setPrivacyPolicy(flagRecycler)
            }
            it.isEnabled = true
        }

        // Open source license
        findViewById<MaterialButton>(R.id.button_open_source).setOnClickListener {
            it.isEnabled = false
            lifecycleScope.launch {
                flagRecycler = setOpenSource(flagRecycler)
            }
            it.isEnabled = true
        }

        // Set Layout Manager for Recycler View
        val aboutRecyclerView: RecyclerView = findViewById(R.id.recycler_view_about)
        val linearLayoutManager = LinearLayoutManager(this)
        aboutRecyclerView.layoutManager = linearLayoutManager

        //Download license files if not in cache
        val licenseFile = File(cacheDir.path + "/apache_license.txt")
        if (!licenseFile.exists()) {
            lifecycleScope.launch {
                downloadLicenses()
            }
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

    /**
     * Opens/closes Privacy Policy
     * @param flag status of recycler view
     * @return new flag
     */
    private fun setPrivacyPolicy(flag: String): String {
        val policyList: ArrayList<Spanned> = arrayListOf(
            HtmlCompat.fromHtml(
                "<strong>Privacy Policy</strong><br>",
                HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
            )
        )

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

            fun doIt(tag: String, start: Int, offset: Int): Spanned = run {
                val end = content.indexOf(tag) + offset
                val smallContent = content.substring(start..end)
                content = content.substring(end)
                return HtmlCompat.fromHtml(
                    smallContent,
                    HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                )
            }

            when (minVal) {
                divIndex -> policyList.add(doIt("</div>", divIndex, 6))
                ulIndex -> policyList.add(doIt("</ul>", ulIndex, 5))
                pIndex -> policyList.add(doIt("</p>", pIndex, 4))
            }
        }

        // sets recycler view and returns new flag
        return when (flag) {
            flagState[0], flagState[2] -> {
                setRecyclerView(policyList)
                flagState[1]
            }

            flagState[1] -> {
                if (findViewById<RecyclerView>(R.id.recycler_view_about).visibility == View.VISIBLE)
                    setRecyclerView(policyList, isVisible = false)
                else
                    setRecyclerView(policyList, isVisible = true, changeList = false)
                flagState[1]
            }

            else -> flagState[0]
        }
    }

    /**
     * Opens/closes Privacy Policy
     * @param flag status of recycler view
     * @return new flag
     */
    private fun setOpenSource(flag: String): String {
        var content = ""

        //Retrieve content of Apache License v2
        try {
            val myInputStream: InputStream =
                FileInputStream(File(cacheDir.path + "/apache_license.txt"))
            val size: Int = myInputStream.available()
            val buffer = ByteArray(size)
            myInputStream.read(buffer)
            content += String(buffer)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Separate paragraphs into ArrayList
        val apacheList: ArrayList<Spanned> = ArrayList()
        while (content.contains("\n\n")) {
            apacheList.add(content.substring(0..content.indexOf("\n\n")).toSpanned())
            content = content.substring(content.indexOf("\n\n") + 2)
        }

        val licensesMap: Map<String, Int> = mapOf(
            "androidx.core:core-ktx" to R.string.apache_license_txt,
            "androidx.appcompat:appcompat" to R.string.apache_license_txt,
            "com.google.android.material:material" to R.string.apache_license_txt,
            "com.google.firebase:firebase-bom" to R.string.apache_license_txt,
            "androidx.preference:preference-ktx" to R.string.apache_license_txt,
            "io.coil-kt:coil" to R.string.apache_license_txt
        )
        val openSourceList: ArrayList<Spanned> =
            arrayListOf(
                HtmlCompat.fromHtml(
                    "<strong>Open source licenses</strong><br>",
                    HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                )
            )

        for (item in licensesMap) {
            openSourceList.add("\n${"=".repeat(40)}\n${item.key}\n${"=".repeat(40)}".toSpanned())
//            openSourceList.add(resources.getString(item.value).toSpanned())
//            openSourceList.add(content.toSpanned())
            openSourceList.addAll(apacheList)
        }

        // sets recycler view and returns new flag
        return when (flag) {
            flagState[0], flagState[1] -> {
                setRecyclerView(openSourceList)
                flagState[2]
            }

            flagState[2] -> {
                if (findViewById<RecyclerView>(R.id.recycler_view_about).visibility == View.VISIBLE)
                    setRecyclerView(openSourceList, isVisible = false)
                else
                    setRecyclerView(openSourceList, isVisible = true, changeList = false)
                flagState[2]
            }

            else -> flagState[0]
        }
    }

    /**
     * Sets/hides recycler view
     * @param list list of contents to display on recycler view
     * @param isVisible whether to set recycler view to visible or gone
     */
    private fun setRecyclerView(
        list: ArrayList<Spanned>,
        isVisible: Boolean = true,
        changeList: Boolean = true
    ) {
        val aboutRecyclerView: RecyclerView = findViewById(R.id.recycler_view_about)
        if (isVisible) {
            aboutRecyclerView.visibility = View.VISIBLE
            if (changeList)
                aboutRecyclerView.adapter = AboutAdapter(list)
        } else aboutRecyclerView.visibility = View.GONE
    }

    /**
     * Download License of open source projects used to cache
     */
    private suspend fun downloadLicenses() {
        withContext(Dispatchers.IO) {
            try {
                // establish url connection
                val url = URL(apacheLicenseLink)
                val urlConn = url.openConnection()
                urlConn.readTimeout = 5000
                urlConn.connectTimeout = 10000

                val inputStream = urlConn.getInputStream()
                val inStream = BufferedInputStream(inputStream, 1024 * 5)

                val file = File(cacheDir.path + "/apache_license.txt")
                file.createNewFile()

                val outStream = FileOutputStream(file)
                val buffer = ByteArray(5 * 1024)
                var len: Int
                while (inStream.read(buffer).also { len = it } != -1) {
                    outStream.write(buffer, 0, len)
                }

                // Close all io streams
                outStream.flush()
                outStream.close()
                inStream.close()
                inputStream.close()

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("HtmlActivity", "downloadFile: ${e.printStackTrace()}")
            }
        }
    }

    companion object {

        private const val linkedInLink: String = "https://linkedin.com/in/gouthams0922"
        private const val gitHubLink: String = "https://github.com/gouthams22"
        private const val developerMailId: String = "goutham22dev@gmail.com"

        private const val apacheLicenseLink: String =
            "https://www.apache.org/licenses/LICENSE-2.0.txt"

        private val flagState: ArrayList<String> =
            arrayListOf("none", "privacy_policy", "open_source")

        private const val TAG: String = "AboutActivity"
    }
}