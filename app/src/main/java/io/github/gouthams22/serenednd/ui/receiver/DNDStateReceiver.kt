package io.github.gouthams22.serenednd.ui.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import io.github.gouthams22.serenednd.ui.fragment.HomeFragment

class DNDStateReceiver(_homeFragment: HomeFragment, _homeFragmentView: View) : BroadcastReceiver() {
    companion object {
        private const val TAG = "DNDStateReceiver"
    }

    private val homeFragment = _homeFragment
    private val homeFragmentView = _homeFragmentView

    override fun onReceive(context: Context?, intent: Intent?) {
        // Catches DND changes
        val isDndStateChanged: Boolean =
            intent?.action.equals(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)

        if (isDndStateChanged) {
            Log.d(TAG, "onReceive: DND state changed")
        }
        homeFragment.updateDnd(homeFragmentView)

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d(TAG, "onReceive: ${notificationManager.currentInterruptionFilter}")
    }
}