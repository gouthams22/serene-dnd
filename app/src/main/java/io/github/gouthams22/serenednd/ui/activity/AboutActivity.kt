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
import kotlinx.coroutines.launch


/*
Tasks:
1. Research Custom Tabs
2. Research Deep links
 */
class AboutActivity : AppCompatActivity() {

    private class AboutAdapter(list: ArrayList<Spanned>, formatHtml: Boolean = false) :
        RecyclerView.Adapter<AboutAdapter.ViewHolder>() {
        val contentList: ArrayList<Spanned> = list
        val htmlFormat = formatHtml
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
                setRecyclerView(policyList, formatHtml = true)
                flagState[1]
            }

            flagState[1] -> {
                if (findViewById<RecyclerView>(R.id.recycler_view_about).visibility == View.VISIBLE)
                    setRecyclerView(policyList, isVisible = false, formatHtml = true)
                else
                    setRecyclerView(
                        policyList,
                        isVisible = true,
                        changeList = false,
                        formatHtml = true
                    )
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
        //TODO implement open source
        var content: String = "\n" +
                "                                 Apache License\n" +
                "                           Version 2.0, January 2004\n" +
                "                        http://www.apache.org/licenses/\n" +
                "\n" +
                "   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n" +
                "\n" +
                "   1. Definitions.\n" +
                "\n" +
                "      \"License\" shall mean the terms and conditions for use, reproduction,\n" +
                "      and distribution as defined by Sections 1 through 9 of this document.\n" +
                "\n" +
                "      \"Licensor\" shall mean the copyright owner or entity authorized by\n" +
                "      the copyright owner that is granting the License.\n" +
                "\n" +
                "      \"Legal Entity\" shall mean the union of the acting entity and all\n" +
                "      other entities that control, are controlled by, or are under common\n" +
                "      control with that entity. For the purposes of this definition,\n" +
                "      \"control\" means (i) the power, direct or indirect, to cause the\n" +
                "      direction or management of such entity, whether by contract or\n" +
                "      otherwise, or (ii) ownership of fifty percent (50%) or more of the\n" +
                "      outstanding shares, or (iii) beneficial ownership of such entity.\n" +
                "\n" +
                "      \"You\" (or \"Your\") shall mean an individual or Legal Entity\n" +
                "      exercising permissions granted by this License.\n" +
                "\n" +
                "      \"Source\" form shall mean the preferred form for making modifications,\n" +
                "      including but not limited to software source code, documentation\n" +
                "      source, and configuration files.\n" +
                "\n" +
                "      \"Object\" form shall mean any form resulting from mechanical\n" +
                "      transformation or translation of a Source form, including but\n" +
                "      not limited to compiled object code, generated documentation,\n" +
                "      and conversions to other media types.\n" +
                "\n" +
                "      \"Work\" shall mean the work of authorship, whether in Source or\n" +
                "      Object form, made available under the License, as indicated by a\n" +
                "      copyright notice that is included in or attached to the work\n" +
                "      (an example is provided in the Appendix below).\n" +
                "\n" +
                "      \"Derivative Works\" shall mean any work, whether in Source or Object\n" +
                "      form, that is based on (or derived from) the Work and for which the\n" +
                "      editorial revisions, annotations, elaborations, or other modifications\n" +
                "      represent, as a whole, an original work of authorship. For the purposes\n" +
                "      of this License, Derivative Works shall not include works that remain\n" +
                "      separable from, or merely link (or bind by name) to the interfaces of,\n" +
                "      the Work and Derivative Works thereof.\n" +
                "\n" +
                "      \"Contribution\" shall mean any work of authorship, including\n" +
                "      the original version of the Work and any modifications or additions\n" +
                "      to that Work or Derivative Works thereof, that is intentionally\n" +
                "      submitted to Licensor for inclusion in the Work by the copyright owner\n" +
                "      or by an individual or Legal Entity authorized to submit on behalf of\n" +
                "      the copyright owner. For the purposes of this definition, \"submitted\"\n" +
                "      means any form of electronic, verbal, or written communication sent\n" +
                "      to the Licensor or its representatives, including but not limited to\n" +
                "      communication on electronic mailing lists, source code control systems,\n" +
                "      and issue tracking systems that are managed by, or on behalf of, the\n" +
                "      Licensor for the purpose of discussing and improving the Work, but\n" +
                "      excluding communication that is conspicuously marked or otherwise\n" +
                "      designated in writing by the copyright owner as \"Not a Contribution.\"\n" +
                "\n" +
                "      \"Contributor\" shall mean Licensor and any individual or Legal Entity\n" +
                "      on behalf of whom a Contribution has been received by Licensor and\n" +
                "      subsequently incorporated within the Work.\n" +
                "\n" +
                "   2. Grant of Copyright License. Subject to the terms and conditions of\n" +
                "      this License, each Contributor hereby grants to You a perpetual,\n" +
                "      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
                "      copyright license to reproduce, prepare Derivative Works of,\n" +
                "      publicly display, publicly perform, sublicense, and distribute the\n" +
                "      Work and such Derivative Works in Source or Object form.\n" +
                "\n" +
                "   3. Grant of Patent License. Subject to the terms and conditions of\n" +
                "      this License, each Contributor hereby grants to You a perpetual,\n" +
                "      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
                "      (except as stated in this section) patent license to make, have made,\n" +
                "      use, offer to sell, sell, import, and otherwise transfer the Work,\n" +
                "      where such license applies only to those patent claims licensable\n" +
                "      by such Contributor that are necessarily infringed by their\n" +
                "      Contribution(s) alone or by combination of their Contribution(s)\n" +
                "      with the Work to which such Contribution(s) was submitted. If You\n" +
                "      institute patent litigation against any entity (including a\n" +
                "      cross-claim or counterclaim in a lawsuit) alleging that the Work\n" +
                "      or a Contribution incorporated within the Work constitutes direct\n" +
                "      or contributory patent infringement, then any patent licenses\n" +
                "      granted to You under this License for that Work shall terminate\n" +
                "      as of the date such litigation is filed.\n" +
                "\n" +
                "   4. Redistribution. You may reproduce and distribute copies of the\n" +
                "      Work or Derivative Works thereof in any medium, with or without\n" +
                "      modifications, and in Source or Object form, provided that You\n" +
                "      meet the following conditions:\n" +
                "\n" +
                "      (a) You must give any other recipients of the Work or\n" +
                "          Derivative Works a copy of this License; and\n" +
                "\n" +
                "      (b) You must cause any modified files to carry prominent notices\n" +
                "          stating that You changed the files; and\n" +
                "\n" +
                "      (c) You must retain, in the Source form of any Derivative Works\n" +
                "          that You distribute, all copyright, patent, trademark, and\n" +
                "          attribution notices from the Source form of the Work,\n" +
                "          excluding those notices that do not pertain to any part of\n" +
                "          the Derivative Works; and\n" +
                "\n" +
                "      (d) If the Work includes a \"NOTICE\" text file as part of its\n" +
                "          distribution, then any Derivative Works that You distribute must\n" +
                "          include a readable copy of the attribution notices contained\n" +
                "          within such NOTICE file, excluding those notices that do not\n" +
                "          pertain to any part of the Derivative Works, in at least one\n" +
                "          of the following places: within a NOTICE text file distributed\n" +
                "          as part of the Derivative Works; within the Source form or\n" +
                "          documentation, if provided along with the Derivative Works; or,\n" +
                "          within a display generated by the Derivative Works, if and\n" +
                "          wherever such third-party notices normally appear. The contents\n" +
                "          of the NOTICE file are for informational purposes only and\n" +
                "          do not modify the License. You may add Your own attribution\n" +
                "          notices within Derivative Works that You distribute, alongside\n" +
                "          or as an addendum to the NOTICE text from the Work, provided\n" +
                "          that such additional attribution notices cannot be construed\n" +
                "          as modifying the License.\n" +
                "\n" +
                "      You may add Your own copyright statement to Your modifications and\n" +
                "      may provide additional or different license terms and conditions\n" +
                "      for use, reproduction, or distribution of Your modifications, or\n" +
                "      for any such Derivative Works as a whole, provided Your use,\n" +
                "      reproduction, and distribution of the Work otherwise complies with\n" +
                "      the conditions stated in this License.\n" +
                "\n" +
                "   5. Submission of Contributions. Unless You explicitly state otherwise,\n" +
                "      any Contribution intentionally submitted for inclusion in the Work\n" +
                "      by You to the Licensor shall be under the terms and conditions of\n" +
                "      this License, without any additional terms or conditions.\n" +
                "      Notwithstanding the above, nothing herein shall supersede or modify\n" +
                "      the terms of any separate license agreement you may have executed\n" +
                "      with Licensor regarding such Contributions.\n" +
                "\n" +
                "   6. Trademarks. This License does not grant permission to use the trade\n" +
                "      names, trademarks, service marks, or product names of the Licensor,\n" +
                "      except as required for reasonable and customary use in describing the\n" +
                "      origin of the Work and reproducing the content of the NOTICE file.\n" +
                "\n" +
                "   7. Disclaimer of Warranty. Unless required by applicable law or\n" +
                "      agreed to in writing, Licensor provides the Work (and each\n" +
                "      Contributor provides its Contributions) on an \"AS IS\" BASIS,\n" +
                "      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\n" +
                "      implied, including, without limitation, any warranties or conditions\n" +
                "      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A\n" +
                "      PARTICULAR PURPOSE. You are solely responsible for determining the\n" +
                "      appropriateness of using or redistributing the Work and assume any\n" +
                "      risks associated with Your exercise of permissions under this License.\n" +
                "\n" +
                "   8. Limitation of Liability. In no event and under no legal theory,\n" +
                "      whether in tort (including negligence), contract, or otherwise,\n" +
                "      unless required by applicable law (such as deliberate and grossly\n" +
                "      negligent acts) or agreed to in writing, shall any Contributor be\n" +
                "      liable to You for damages, including any direct, indirect, special,\n" +
                "      incidental, or consequential damages of any character arising as a\n" +
                "      result of this License or out of the use or inability to use the\n" +
                "      Work (including but not limited to damages for loss of goodwill,\n" +
                "      work stoppage, computer failure or malfunction, or any and all\n" +
                "      other commercial damages or losses), even if such Contributor\n" +
                "      has been advised of the possibility of such damages.\n" +
                "\n" +
                "   9. Accepting Warranty or Additional Liability. While redistributing\n" +
                "      the Work or Derivative Works thereof, You may choose to offer,\n" +
                "      and charge a fee for, acceptance of support, warranty, indemnity,\n" +
                "      or other liability obligations and/or rights consistent with this\n" +
                "      License. However, in accepting such obligations, You may act only\n" +
                "      on Your own behalf and on Your sole responsibility, not on behalf\n" +
                "      of any other Contributor, and only if You agree to indemnify,\n" +
                "      defend, and hold each Contributor harmless for any liability\n" +
                "      incurred by, or claims asserted against, such Contributor by reason\n" +
                "      of your accepting any such warranty or additional liability.\n" +
                "\n" +
                "   END OF TERMS AND CONDITIONS\n" +
                "\n" +
                "   APPENDIX: How to apply the Apache License to your work.\n" +
                "\n" +
                "      To apply the Apache License to your work, attach the following\n" +
                "      boilerplate notice, with the fields enclosed by brackets \"[]\"\n" +
                "      replaced with your own identifying information. (Don't include\n" +
                "      the brackets!)  The text should be enclosed in the appropriate\n" +
                "      comment syntax for the file format. We also recommend that a\n" +
                "      file or class name and description of purpose be included on the\n" +
                "      same \"printed page\" as the copyright notice for easier\n" +
                "      identification within third-party archives.\n" +
                "\n" +
                "   Copyright [yyyy] [name of copyright owner]\n" +
                "\n" +
                "   Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "   you may not use this file except in compliance with the License.\n" +
                "   You may obtain a copy of the License at\n" +
                "\n" +
                "       http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "   Unless required by applicable law or agreed to in writing, software\n" +
                "   distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "   See the License for the specific language governing permissions and\n" +
                "   limitations under the License."
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
        changeList: Boolean = true,
        formatHtml: Boolean = false
    ) {
        val aboutRecyclerView: RecyclerView = findViewById(R.id.recycler_view_about)
        if (isVisible) {
            aboutRecyclerView.visibility = View.VISIBLE
            if (changeList)
                aboutRecyclerView.adapter = AboutAdapter(list, formatHtml)
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