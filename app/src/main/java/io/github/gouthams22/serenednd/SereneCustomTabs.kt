package io.github.gouthams22.serenednd

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.LifecycleCoroutineScope
import io.github.gouthams22.serenednd.preferences.SettingsPreferences
import kotlinx.coroutines.launch

class SereneCustomTabs(context: Context) {
    private val tabContext: Context = context

    /**
     * Custom Tabs builder
     */
    private var builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        .apply {
            setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder().apply {
                setToolbarColor(
                    tabContext.resources.getColor(
                        R.color.black,
                        tabContext.theme
                    )
                )
            }
                .build())

            setColorSchemeParams(
                CustomTabsIntent.COLOR_SCHEME_DARK,
                CustomTabColorSchemeParams.Builder().apply {
                    setToolbarColor(
                        tabContext.resources.getColor(
                            R.color.teal_200,
                            tabContext.theme
                        )
                    )
                }.build()
            )

            setColorSchemeParams(
                CustomTabsIntent.COLOR_SCHEME_LIGHT,
                CustomTabColorSchemeParams.Builder().apply {
                    setToolbarColor(
                        tabContext.resources.getColor(
                            R.color.teal_700,
                            tabContext.theme
                        )
                    )
                }.build()
            )

            setUrlBarHidingEnabled(true)
            setShowTitle(true)
            setCloseButtonIcon(
                BitmapFactory.decodeResource(
                    tabContext.resources,
                    R.drawable.ic_outline_arrow_back_24
                )
            )
        }

    /**
     * Launches the Url to Custom Tab
     * @param url Url of the website to launch
     * @param packageName Specify package name for custom tab of that browser to launch
     */
    fun launchUrl(
        url: String,
        lifecycleCoroutineScope: LifecycleCoroutineScope,
        packageName: String = "invalid"
    ) {
        var theme: String

        lifecycleCoroutineScope.launch {
            // Get theme
            theme = SettingsPreferences(lifecycleCoroutineScope, tabContext).getTheme()
            // Set theme to builder
            builder.setColorScheme(
                when (theme) {
                    AppCompatDelegate.MODE_NIGHT_NO.toString() -> CustomTabsIntent.COLOR_SCHEME_LIGHT
                    AppCompatDelegate.MODE_NIGHT_YES.toString() -> CustomTabsIntent.COLOR_SCHEME_DARK
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString() -> CustomTabsIntent.COLOR_SCHEME_SYSTEM
                    else -> CustomTabsIntent.COLOR_SCHEME_SYSTEM
                }
            )

            val tabsIntent: CustomTabsIntent = builder.build()

            // Ensures the intent is not kept in the history stack, which makes
            // sure navigating away from it will close it
            tabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

            when (packageName) {
                DEFAULT_CHROME_PACKAGE -> tabsIntent.intent.`package` = DEFAULT_CHROME_PACKAGE
                DEFAULT_EDGE_PACKAGE -> tabsIntent.intent.`package` = DEFAULT_EDGE_PACKAGE
            }
            tabsIntent.launchUrl(tabContext, Uri.parse(url))
        }
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