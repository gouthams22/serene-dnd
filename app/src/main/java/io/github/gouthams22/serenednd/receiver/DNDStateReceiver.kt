package io.github.gouthams22.serenednd.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.gouthams22.serenednd.ui.fragment.HomeFragment

class DNDStateReceiver(private val homeFragment: HomeFragment) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Catches DND changes
        // intent?.action.equals(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)

        // Update DND in Home page
        homeFragment.updateDnd()
    }
}