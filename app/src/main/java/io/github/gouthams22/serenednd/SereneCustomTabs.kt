package io.github.gouthams22.serenednd

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent

class SereneCustomTabs(context: Context) {
    private val tabContext: Context = context

    /**
     * Custom Tabs builder
     */
    private var builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        .apply {
            setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder()
                .apply {
                    setToolbarColor(
                        tabContext.resources.getColor(
                            R.color.green_700,
                            tabContext.theme
                        )
                    )
                }
                .build())
        }

    /**
     * Launches the Url to Custom Tab
     * @param url Url of the website to launch
     * @param packageName Specify package name for custom tab of that browser to launch
     */
    fun launchUrl(url: String, packageName: String = "invalid") {
        val tabsIntent: CustomTabsIntent = builder.build()
        when (packageName) {
            DEFAULT_CHROME_PACKAGE -> tabsIntent.intent.`package` = DEFAULT_CHROME_PACKAGE
            DEFAULT_EDGE_PACKAGE -> tabsIntent.intent.`package` = DEFAULT_EDGE_PACKAGE
        }
        tabsIntent.launchUrl(tabContext, Uri.parse(url))
    }

    companion object {
        /**
         * Package name of Google Chrome browser
         */
        const val DEFAULT_CHROME_PACKAGE = "com.android.chrome"

        /**
         * Package name of Microsoft Edge browser
         */
        const val DEFAULT_EDGE_PACKAGE = "com.microsoft.emmx"
    }
}