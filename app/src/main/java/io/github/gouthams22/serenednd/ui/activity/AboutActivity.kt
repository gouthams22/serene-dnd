package io.github.gouthams22.serenednd.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.serenednd.BuildConfig
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.SereneCustomTabs
import kotlinx.coroutines.launch

/*
Tasks:
1. Research Custom Tabs
2. Research Deep links
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

        // Set Layout Manager for Recycler View
        val aboutRecyclerView: RecyclerView = findViewById(R.id.recycler_view_about)
        val linearLayoutManager = LinearLayoutManager(this)
        aboutRecyclerView.layoutManager = linearLayoutManager
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
        val policyList: ArrayList<String> = arrayListOf("<strong>Privacy Policy</strong><br>")

        var content: String = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <meta charset='utf-8'>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <strong>Privacy Policy</strong> \n" +
                "        <p>\n" +
                "            Goutham Subramani built the Serene DND (Do Not Disturb) app as a Free app. This SERVICE is provided by Goutham Subramani at no cost and is intended for use as is.\n\n\n\n\n\n\n\n" +
                "        </p>" +
                "        <p>" +
                "            This page is used to inform visitors regarding my\n" +
                "            policies with the collection, use, and disclosure of Personal\n" +
                "            Information if anyone decided to use my Service.\n" +
                "        </p>\n" +
                "        <p>\n" +
                "            If you choose to use my Service, then you agree to\n" +
                "            the collection and use of information in relation to this\n" +
                "            policy. The Personal Information that I collect is\n" +
                "            used for providing and improving the Service. I will not use or share your information with\n" +
                "            anyone except as described in this Privacy Policy.\n" +
                "        </p>\n" +
                "        <p>\n" +
                "            The terms used in this Privacy Policy have the same meanings\n" +
                "            as in our Terms and Conditions, which are accessible at\n" +
                "            Serene DND (Do Not Disturb) unless otherwise defined in this Privacy Policy.\n" +
                "        </p>\n" +
                "        <p><strong>Information Collection and Use</strong></p>\n" +
                "        <p>\n" +
                "            For a better experience, while using our Service, I\n" +
                "            may require you to provide us with certain personally\n" +
                "            identifiable information, including but not limited to e-mail. The information that\n" +
                "            I request will be retained on your device and is not collected by me in any way.\n" +
                "        </p>\n" +
                "        <div>\n" +
                "            <p>\n" +
                "                The app does use third-party services that may collect\n" +
                "                information used to identify you.\n" +
                "            </p>\n" +
                "            <p>\n" +
                "                Link to the privacy policy of third-party service providers used\n" +
                "                by the app\n" +
                "            </p>\n" +
                "            <ul>\n" +
                "                <li><a href=\"https://www.google.com/policies/privacy/\" >Google Play Services</a></li>\n" +
                "                <!---->\n" +
                "                <li><a href=\"https://firebase.google.com/policies/analytics\" target=\"_blank\" rel=\"noopener noreferrer\">Google Analytics for Firebase</a></li>\n" +
                "                <li><a href=\"https://firebase.google.com/support/privacy/\" target=\"_blank\" rel=\"noopener noreferrer\">Firebase Crashlytics</a></li>\n" +
                "                <!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!---->\n" +
                "            </ul>\n" +
                "        </div>\n" +
                "        <p><strong>Log Data</strong></p>\n" +
                "        <p>\n" +
                "            I want to inform you that whenever you\n" +
                "            use my Service, in a case of an error in the app\n" +
                "            I collect data and information (through third-party\n" +
                "            products) on your phone called Log Data. This Log Data may\n" +
                "            include information such as your device Internet Protocol\n" +
                "            (“IP”) address, device name, operating system version, the\n" +
                "            configuration of the app when utilizing my Service,\n" +
                "            the time and date of your use of the Service, and other\n" +
                "            statistics.\n" +
                "        </p>\n" +
                "        <p><strong>Cookies</strong></p>\n" +
                "        <p>\n" +
                "            Cookies are files with a small amount of data that are\n" +
                "            commonly used as anonymous unique identifiers. These are sent\n" +
                "            to your browser from the websites that you visit and are\n" +
                "            stored on your device's internal memory.\n" +
                "        </p>\n" +
                "        <p>\n" +
                "            This Service does not use these “cookies” explicitly. However,\n" +
                "            the app may use third-party code and libraries that use\n" +
                "            “cookies” to collect information and improve their services.\n" +
                "            You have the option to either accept or refuse these cookies\n" +
                "            and know when a cookie is being sent to your device. If you\n" +
                "            choose to refuse our cookies, you may not be able to use some\n" +
                "            portions of this Service.\n" +
                "        </p>\n" +
                "        <p><strong>Service Providers</strong></p>\n" +
                "        <p>\n" +
                "            I may employ third-party companies and\n" +
                "            individuals due to the following reasons:\n" +
                "        </p>\n" +
                "        <ul>\n" +
                "            <li>To facilitate our Service;</li>\n" +
                "            <li>To provide the Service on our behalf;</li>\n" +
                "            <li>To perform Service-related services; or</li>\n" +
                "            <li>To assist us in analyzing how our Service is used.</li>\n" +
                "        </ul>\n" +
                "        <p>\n" +
                "            I want to inform users of this Service\n" +
                "            that these third parties have access to their Personal\n" +
                "            Information. The reason is to perform the tasks assigned to\n" +
                "            them on our behalf. However, they are obligated not to\n" +
                "            disclose or use the information for any other purpose.\n" +
                "        </p>\n" +
                "        <p><strong>Security</strong></p>\n" +
                "        <p>\n" +
                "            I value your trust in providing us your\n" +
                "            Personal Information, thus we are striving to use commercially\n" +
                "            acceptable means of protecting it. But remember that no method\n" +
                "            of transmission over the internet, or method of electronic\n" +
                "            storage is 100% secure and reliable, and I cannot\n" +
                "            guarantee its absolute security.\n" +
                "        </p>\n" +
                "        <p><strong>Links to Other Sites</strong></p>\n" +
                "        <p>\n" +
                "            This Service may contain links to other sites. If you click on\n" +
                "            a third-party link, you will be directed to that site. Note\n" +
                "            that these external sites are not operated by me.\n" +
                "            Therefore, I strongly advise you to review the\n" +
                "            Privacy Policy of these websites. I have\n" +
                "            no control over and assume no responsibility for the content,\n" +
                "            privacy policies, or practices of any third-party sites or\n" +
                "            services.\n" +
                "        </p>\n" +
                "        <p><strong>Children’s Privacy</strong></p>\n" +
                "        <div>\n" +
                "            <p>\n" +
                "                These Services do not address anyone under the age of 13.\n" +
                "                I do not knowingly collect personally\n" +
                "                identifiable information from children under 13 years of age. In the case\n" +
                "                I discover that a child under 13 has provided\n" +
                "                me with personal information, I immediately\n" +
                "                delete this from our servers. If you are a parent or guardian\n" +
                "                and you are aware that your child has provided us with\n" +
                "                personal information, please contact me so that\n" +
                "                I will be able to do the necessary actions.\n" +
                "            </p>\n" +
                "        </div>\n" +
                "        <!----> \n" +
                "        <p><strong>Changes to This Privacy Policy</strong></p>\n" +
                "        <p>\n" +
                "            I may update our Privacy Policy from\n" +
                "            time to time. Thus, you are advised to review this page\n" +
                "            periodically for any changes. I will\n" +
                "            notify you of any changes by posting the new Privacy Policy on\n" +
                "            this page.\n" +
                "        </p>\n" +
                "        <p>This policy is effective as of 2023-07-22</p>\n" +
                "        <p><strong>Contact Us</strong></p>\n" +
                "        <p>\n" +
                "            If you have any questions or suggestions about my\n" +
                "            Privacy Policy, do not hesitate to contact me at goutham22dev@gmail.com.\n" +
                "        </p>\n" +
                "    </body>\n" +
                "</html>"

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

        // sets recycler view and returns new flag
        return when (flag) {
            flagState[0], flagState[2] -> {
                setRecyclerView(policyList)
                flagState[1]
            }

            flagState[1] -> {
                setRecyclerView(policyList, false)
                flagState[0]
            }

            else -> flagState[0]
        }
    }

    /**
     * Sets/hides recycler view
     * @param list list of contents to display on recycler view
     * @param isVisible whether to set recycler view to visible or gone
     */
    private fun setRecyclerView(list: ArrayList<String>, isVisible: Boolean = true) {
        val aboutRecyclerView: RecyclerView = findViewById(R.id.recycler_view_about)
        if (isVisible) {
            aboutRecyclerView.visibility = View.VISIBLE
            aboutRecyclerView.adapter = AboutAdapter(list)
        } else aboutRecyclerView.visibility = View.GONE
    }

    companion object {

        private const val linkedInLink: String = "https://linkedin.com/in/gouthams0922"
        private const val gitHubLink: String = "https://github.com/gouthams22"
        private const val developerMailId: String = "goutham22dev@gmail.com"

        private val flagState: ArrayList<String> =
            arrayListOf("none", "privacy_policy", "open_source")

        private const val TAG: String = "AboutActivity"
    }
}