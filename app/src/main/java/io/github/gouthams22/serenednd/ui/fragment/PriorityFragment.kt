package io.github.gouthams22.serenednd.ui.fragment

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.serenednd.R

/**
 * A simple [Fragment] subclass.
 * Use the [PriorityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PriorityFragment : Fragment() {

    /**
     * Class which contains the options of Notification Policy
     */
    private class PriorityOptions {

        /**
         * Notification categories to prioritize. Bitmask of PRIORITY_CATEGORY_* constants.
         */
        var priorityCategories = 0

        /**
         * Notification senders to prioritize for calls. One of: PRIORITY_SENDERS_ANY, PRIORITY_SENDERS_CONTACTS, PRIORITY_SENDERS_STARRED
         */
        var priorityCallSenders = -1
            get() = if (field == -1) 0 else field
            set(value) {
                field = when (value) {
                    NotificationManager.Policy.PRIORITY_SENDERS_ANY,
                    NotificationManager.Policy.PRIORITY_SENDERS_CONTACTS,
                    NotificationManager.Policy.PRIORITY_SENDERS_STARRED -> value

                    else -> -1
                }
            }

        /**
         * Notification senders to prioritize for messages. One of: PRIORITY_SENDERS_ANY, PRIORITY_SENDERS_CONTACTS, PRIORITY_SENDERS_STARRED
         */
        var priorityMessageSenders = -1
            get() = if (field == -1) 0 else field
            set(value) {
                field = when (value) {
                    NotificationManager.Policy.PRIORITY_SENDERS_ANY,
                    NotificationManager.Policy.PRIORITY_SENDERS_CONTACTS,
                    NotificationManager.Policy.PRIORITY_SENDERS_STARRED -> value

                    else -> -1
                }
            }

        /**
         * Notification senders to prioritize for conversations. One of: CONVERSATION_SENDERS_NONE, CONVERSATION_SENDERS_IMPORTANT, CONVERSATION_SENDERS_ANYONE.
         */
        var priorityConversationSenders = -1
            set(value) {
                field = when (value) {
                    NotificationManager.Policy.CONVERSATION_SENDERS_ANYONE,
                    NotificationManager.Policy.CONVERSATION_SENDERS_IMPORTANT,
                    NotificationManager.Policy.CONVERSATION_SENDERS_NONE -> value

                    else -> -1
                }
            }

        /**
         * Updates the notification policy by setting a new policy
         */
        fun updateNotificationPolicy(
            notificationManager: NotificationManager
        ) {
            notificationManager.notificationPolicy =
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) NotificationManager.Policy(
                    priorityCategories,
                    priorityCallSenders,
                    priorityMessageSenders,
                    notificationManager.notificationPolicy.suppressedVisualEffects,
                    priorityConversationSenders
                ) else NotificationManager.Policy(
                    priorityCategories,
                    priorityCallSenders,
                    priorityMessageSenders,
                    notificationManager.notificationPolicy.suppressedVisualEffects
                )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_priority, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // String options for calls and message
        val statusCalls =
            arrayOf(
                getString(R.string.from_anyone),
                getString(R.string.from_contacts),
                getString(R.string.from_starred_contacts),
                getString(R.string.none)
            )

        // String options for conversations
        val statusConversations = arrayOf(
            getString(R.string.all_conversations),
            getString(R.string.priority_conversations),
            getString(R.string.none)
        )

        val callsCard: MaterialCardView = view.findViewById(R.id.calls_card)
        val callsStatusText: MaterialTextView = view.findViewById(R.id.calls_status)

        val messagesCard: MaterialCardView = view.findViewById(R.id.messages_card)
        val messagesStatusText: MaterialTextView = view.findViewById(R.id.messages_status)

        val conversationsCard: MaterialCardView = view.findViewById(R.id.conversations_card)
        val conversationsStatusText: MaterialTextView = view.findViewById(R.id.conversations_status)

        //Switches
        val remindersSwitch: MaterialSwitch = view.findViewById(R.id.switch_reminders)
        val eventsSwitch: MaterialSwitch = view.findViewById(R.id.switch_events)
        val alarmsSwitch: MaterialSwitch = view.findViewById(R.id.switch_alarms)
        val mediaSwitch: MaterialSwitch = view.findViewById(R.id.switch_media)
        val systemSwitch: MaterialSwitch = view.findViewById(R.id.switch_system)

        val notificationManager: NotificationManager =
            view.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Priority categories: https://developer.android.com/reference/android/app/NotificationManager.Policy#priorityCategories
        val priorityOptions = PriorityOptions()
        //notificationPolicy.priorityCategories contains the sum of selected categories corresponding to their bits 1+10+1000=1011 (1+2+8=11)
        priorityOptions.priorityCategories =
            notificationManager.notificationPolicy.priorityCategories
        priorityOptions.priorityCallSenders =
            notificationManager.notificationPolicy.priorityCallSenders
        priorityOptions.priorityMessageSenders =
            notificationManager.notificationPolicy.priorityMessageSenders

        // CALLS Settings
        if (priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_CALLS == NotificationManager.Policy.PRIORITY_CATEGORY_CALLS)
            callsStatusText.text = when (priorityOptions.priorityCallSenders) {
                NotificationManager.Policy.PRIORITY_SENDERS_ANY -> statusCalls[0]
                NotificationManager.Policy.PRIORITY_SENDERS_CONTACTS -> statusCalls[1]
                NotificationManager.Policy.PRIORITY_SENDERS_STARRED -> statusCalls[2]
                else -> "Error! Report to developer"
            }
        else
            callsStatusText.text = statusCalls[3]

        callsCard.setOnClickListener {
            it.isEnabled = false
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.setOnMenuItemClickListener { item ->
                Log.d(
                    TAG, "onViewCreated: Calls: ${
                        when (item.itemId) {
                            R.id.item_anyone -> statusCalls[0]
                            R.id.item_contacts -> statusCalls[1]
                            R.id.item_starred -> statusCalls[2]
                            R.id.item_none -> statusCalls[3]
                            else -> "Error!"
                        }
                    }"
                )

                // Ensuring calls category bit is always turned on, so that calls category bit can be turned off (at succeeding "when" block) only when option "none" is clicked
                if (priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_CALLS != NotificationManager.Policy.PRIORITY_CATEGORY_CALLS)
                    priorityOptions.priorityCategories += NotificationManager.Policy.PRIORITY_CATEGORY_CALLS

                when (item.itemId) {
                    R.id.item_anyone -> {
                        callsStatusText.text = statusCalls[0]
                        priorityOptions.priorityCallSenders =
                            NotificationManager.Policy.PRIORITY_SENDERS_ANY
                    }

                    R.id.item_contacts -> {
                        callsStatusText.text = statusCalls[1]
                        priorityOptions.priorityCallSenders =
                            NotificationManager.Policy.PRIORITY_SENDERS_CONTACTS
                    }

                    R.id.item_starred -> {
                        callsStatusText.text = statusCalls[2]
                        priorityOptions.priorityCallSenders =
                            NotificationManager.Policy.PRIORITY_SENDERS_STARRED
                    }

                    R.id.item_none -> {
                        callsStatusText.text = statusCalls[3]
                        priorityOptions.priorityCategories -= NotificationManager.Policy.PRIORITY_CATEGORY_CALLS
                    }
                }
                priorityOptions.updateNotificationPolicy(notificationManager)
                true
            }
            popupMenu.menuInflater.inflate(R.menu.contacts_popup_menu, popupMenu.menu)
            popupMenu.show()
            it.isEnabled = true
        }

        //MESSAGES Settings
        if (priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES == NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES)
            messagesStatusText.text = when (priorityOptions.priorityMessageSenders) {
                0 -> statusCalls[0]
                1 -> statusCalls[1]
                2 -> statusCalls[2]
                else -> "Error! Report to developer"
            }
        else
            messagesStatusText.text = statusCalls[3]

        messagesCard.setOnClickListener {
            it.isEnabled = false
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.setOnMenuItemClickListener { item ->
                Log.d(
                    TAG, "onViewCreated: Messages: ${
                        when (item.itemId) {
                            R.id.item_anyone -> statusCalls[0]
                            R.id.item_contacts -> statusCalls[1]
                            R.id.item_starred -> statusCalls[2]
                            R.id.item_none -> statusCalls[3]
                            else -> "Error!"
                        }
                    }"
                )

                // Ensuring messages category bit is always turned on, so that messages category bit can be turned off (at succeeding "when" block) only when option "none" is clicked
                if (priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES != NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES)
                    priorityOptions.priorityCategories += NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES

                when (item.itemId) {
                    R.id.item_anyone -> {
                        messagesStatusText.text = statusCalls[0]
                        priorityOptions.priorityMessageSenders =
                            NotificationManager.Policy.PRIORITY_SENDERS_ANY
                    }

                    R.id.item_contacts -> {
                        messagesStatusText.text = statusCalls[1]
                        priorityOptions.priorityMessageSenders =
                            NotificationManager.Policy.PRIORITY_SENDERS_CONTACTS
                    }

                    R.id.item_starred -> {
                        messagesStatusText.text = statusCalls[2]
                        priorityOptions.priorityMessageSenders =
                            NotificationManager.Policy.PRIORITY_SENDERS_STARRED
                    }

                    R.id.item_none -> {
                        messagesStatusText.text = statusCalls[3]
                        priorityOptions.priorityCategories -= NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES
                    }
                }
                priorityOptions.updateNotificationPolicy(notificationManager)
                true
            }
            popupMenu.menuInflater.inflate(R.menu.contacts_popup_menu, popupMenu.menu)
            popupMenu.show()
            it.isEnabled = true
        }

        //CONVERSATIONS Settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val policyPriorityConversations =
                notificationManager.notificationPolicy.priorityConversationSenders

            // Store conversation sender option to PriorityOptions class
            priorityOptions.priorityConversationSenders = policyPriorityConversations

            if (priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_CONVERSATIONS == NotificationManager.Policy.PRIORITY_CATEGORY_CONVERSATIONS)
                conversationsStatusText.text = when (policyPriorityConversations) {
                    1 -> statusConversations[0]
                    2 -> statusConversations[1]
                    3 -> statusConversations[2]
                    else -> "Error! Report to developer"
                }
            else
                conversationsStatusText.text = statusConversations[2]

            conversationsCard.setOnClickListener {
                it.isEnabled = false
                val popupMenu = PopupMenu(it.context, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    Log.d(
                        TAG, "onViewCreated: Conversations: ${
                            when (item.itemId) {
                                R.id.menu_conversations_all -> statusConversations[0]
                                R.id.menu_conversations_priority -> statusConversations[1]
                                R.id.menu_conversations_none -> statusConversations[2]
                                else -> "Error!"
                            }
                        }"
                    )

                    if (priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_CONVERSATIONS != NotificationManager.Policy.PRIORITY_CATEGORY_CONVERSATIONS)
                        priorityOptions.priorityCategories += NotificationManager.Policy.PRIORITY_CATEGORY_CONVERSATIONS

                    when (item.itemId) {
                        R.id.menu_conversations_all -> {
                            conversationsStatusText.text = statusConversations[0]
                            priorityOptions.priorityConversationSenders =
                                NotificationManager.Policy.CONVERSATION_SENDERS_ANYONE
                        }

                        R.id.menu_conversations_priority -> {
                            conversationsStatusText.text = statusConversations[1]
                            priorityOptions.priorityConversationSenders =
                                NotificationManager.Policy.CONVERSATION_SENDERS_IMPORTANT
                        }

                        R.id.menu_conversations_none -> {
                            conversationsStatusText.text = statusConversations[2]
                            priorityOptions.priorityConversationSenders =
                                NotificationManager.Policy.CONVERSATION_SENDERS_NONE
                            priorityOptions.priorityCategories -= NotificationManager.Policy.PRIORITY_CATEGORY_CONVERSATIONS
                        }
                    }
                    priorityOptions.updateNotificationPolicy(notificationManager)
                    true
                }
                popupMenu.menuInflater.inflate(R.menu.conversations_popup_menu, popupMenu.menu)
                popupMenu.show()
                it.isEnabled = true
            }

        }

        // PRIORITY_CATEGORY_REMINDERS
        if (priorityOptions.priorityCategories and 1 == 1) {
            Log.d(TAG, "onCreateView: Priority: Reminders")
            remindersSwitch.isChecked = true
        }
        // PRIORITY_CATEGORY_EVENTS
        if (priorityOptions.priorityCategories and 2 == 2) {
            Log.d(TAG, "onCreateView: Priority: Events")
            eventsSwitch.isChecked = true
        }
        // PRIORITY_CATEGORY_ALARMS
        if (priorityOptions.priorityCategories and 32 == 32) {
            Log.d(TAG, "onCreateView: Priority: Alarms")
            alarmsSwitch.isChecked = true
        }
        // PRIORITY_CATEGORY_MEDIA
        if (priorityOptions.priorityCategories and 64 == 64) {
            Log.d(TAG, "onCreateView: Priority: Media")
            mediaSwitch.isChecked = true
        }
        // PRIORITY_CATEGORY_SYSTEM
        if (priorityOptions.priorityCategories and 128 == 128) {
            Log.d(TAG, "onCreateView: Priority: System")
            systemSwitch.isChecked = true
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PriorityFragment.
         */
        @JvmStatic
        fun newInstance() = PriorityFragment()

        private const val TAG = "PriorityFragment"

    }
}
/*

SUPPRESSED EFFECT

1. 4 - Don't turn on screen = SUPPRESSED_EFFECT_FULL_SCREEN_INTENT
2. 128 - Don't wake for notifications = SUPPRESSED_EFFECT_AMBIENT

3. 64 - Hide notification dots on app icons = SUPPRESSED_EFFECT_BADGE
4. 32 - Hide status bar icons at top of screen = SUPPRESSED_EFFECT_STATUS_BAR
5. 18=16+2 - Don't pop notifications on screen = SUPPRESSED_EFFECT_PEEK + SUPPRESSED_EFFECT_SCREEN_ON
6. 288=256+32 - Hide from pull-down = SUPPRESSED_EFFECT_NOTIFICATION_LIST + SUPPRESSED_EFFECT_STATUS_BAR


1 2 4 8 16 32 64 128 256
 */