package io.github.gouthams22.serenednd.ui.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.gouthams22.serenednd.ui.fragment.HomeFragment

class DNDStateReceiver : BroadcastReceiver() {
    private val logTag = "DNDStateReceiver"
    private lateinit var homeFragment: HomeFragment
    fun setHomeFragment(fragment: HomeFragment) {
        homeFragment = fragment
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val isDndStateChanged: Boolean =
            intent?.action.equals(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)
        if (isDndStateChanged) {
            Log.d(logTag, "onReceive: DND state changed")
        }
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        homeFragment.setDndButtonColor()
        Log.d(logTag, "onReceive: ${notificationManager.currentInterruptionFilter}")
    }
}