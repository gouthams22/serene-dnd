package io.github.gouthams22.serenednd.ui.fragment

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.serenednd.R

/*
To be implemented
SUPPRESSED EFFECT

1. 4 - Don't turn on screen = SUPPRESSED_EFFECT_FULL_SCREEN_INTENT
2. 128 - Don't wake for notifications = SUPPRESSED_EFFECT_AMBIENT

3. 64 - Hide notification dots on app icons = SUPPRESSED_EFFECT_BADGE
4. 32 - Hide status bar icons at top of screen = SUPPRESSED_EFFECT_STATUS_BAR
5. 18=16+2 - Don't pop notifications on screen = SUPPRESSED_EFFECT_PEEK + SUPPRESSED_EFFECT_SCREEN_ON
6. 288=256+32 - Hide from pull-down = SUPPRESSED_EFFECT_NOTIFICATION_LIST + SUPPRESSED_EFFECT_STATUS_BAR


1 2 4 8 16 32 64 128 256
 */

/**
 * A simple [Fragment] subclass.
 * Use the [PriorityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PriorityFragment : Fragment() {

    private lateinit var notificationManager: NotificationManager

    // String options for calls and message
    private lateinit var statusCalls: Array<String>

    // String options for conversations
    private lateinit var statusConversations: Array<String>

    private lateinit var priorityOptions: PriorityOptions

    private lateinit var callsStatusText: MaterialTextView
    private lateinit var messagesStatusText: MaterialTextView
    private lateinit var conversationsStatusText: MaterialTextView

    private lateinit var remindersSwitch: MaterialSwitch
    private lateinit var eventsSwitch: MaterialSwitch
    private lateinit var alarmsSwitch: MaterialSwitch
    private lateinit var mediaSwitch: MaterialSwitch
    private lateinit var systemSwitch: MaterialSwitch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_priority, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusCalls = arrayOf(
            getString(R.string.from_anyone),
            getString(R.string.from_contacts),
            getString(R.string.from_starred_contacts),
            getString(R.string.none)
        )

        statusConversations = arrayOf(
            getString(R.string.all_conversations),
            getString(R.string.priority_conversations),
            getString(R.string.none)
        )

        val callsCard: MaterialCardView = view.findViewById(R.id.calls_card)
        callsStatusText = view.findViewById(R.id.calls_status)

        val messagesCard: MaterialCardView = view.findViewById(R.id.messages_card)
        messagesStatusText = view.findViewById(R.id.messages_status)

        val conversationsCard: MaterialCardView = view.findViewById(R.id.conversations_card)
        conversationsStatusText = view.findViewById(R.id.conversations_status)

        //Switches
        remindersSwitch = view.findViewById(R.id.switch_reminders)
        eventsSwitch = view.findViewById(R.id.switch_events)
        alarmsSwitch = view.findViewById(R.id.switch_alarms)
        mediaSwitch = view.findViewById(R.id.switch_media)
        systemSwitch = view.findViewById(R.id.switch_system)

        notificationManager =
            view.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Priority categories: https://developer.android.com/reference/android/app/NotificationManager.Policy#priorityCategories
        priorityOptions = PriorityOptions()
        //notificationPolicy.priorityCategories contains the sum of selected categories corresponding to their bits 1+10+1000=1011 (1+2+8=11)
        priorityOptions.priorityCategories =
            notificationManager.notificationPolicy.priorityCategories
        priorityOptions.priorityCallSenders =
            notificationManager.notificationPolicy.priorityCallSenders
        priorityOptions.priorityMessageSenders =
            notificationManager.notificationPolicy.priorityMessageSenders

        // CALLS CardView settings
        callsCard.setOnClickListener {
            it.isEnabled = false
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.setOnMenuItemClickListener { item ->

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

        //MESSAGES CardView settings
        messagesCard.setOnClickListener {
            it.isEnabled = false
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.setOnMenuItemClickListener { item ->

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

        //CONVERSATIONS CardView settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            // Store conversation sender option to PriorityOptions class
            priorityOptions.priorityConversationSenders =
                notificationManager.notificationPolicy.priorityConversationSenders

            conversationsCard.setOnClickListener {
                it.isEnabled = false
                val popupMenu = PopupMenu(it.context, it)
                popupMenu.setOnMenuItemClickListener { item ->

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

        remindersSwitch.setOnCheckedChangeListener { _, b ->
            priorityOptions.priorityCategories +=
                (if (b && priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_REMINDERS != NotificationManager.Policy.PRIORITY_CATEGORY_REMINDERS)
                    1
                else if (!b && priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_REMINDERS == NotificationManager.Policy.PRIORITY_CATEGORY_REMINDERS)
                    -1
                else 0) * NotificationManager.Policy.PRIORITY_CATEGORY_REMINDERS
            priorityOptions.updateNotificationPolicy(notificationManager)
        }

        eventsSwitch.setOnCheckedChangeListener { _, b ->
//            priorityOptions.priorityCategories += (if (b) 1 else -1) * NotificationManager.Policy.PRIORITY_CATEGORY_EVENTS
            priorityOptions.priorityCategories +=
                (if (b && priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_EVENTS != NotificationManager.Policy.PRIORITY_CATEGORY_EVENTS)
                    1
                else if (!b && priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_EVENTS == NotificationManager.Policy.PRIORITY_CATEGORY_EVENTS)
                    -1
                else 0) * NotificationManager.Policy.PRIORITY_CATEGORY_EVENTS
            priorityOptions.updateNotificationPolicy(notificationManager)
        }

        // These 3 features support from Version P onwards
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            alarmsSwitch.setOnCheckedChangeListener { _, b ->
//                priorityOptions.priorityCategories += (if (b) 1 else -1) * NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS
                priorityOptions.priorityCategories +=
                    (if (b && priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS != NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS)
                        1
                    else if (!b && priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS == NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS)
                        -1
                    else 0) * NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS
                priorityOptions.updateNotificationPolicy(notificationManager)
            }

            mediaSwitch.setOnCheckedChangeListener { _, b ->
//                priorityOptions.priorityCategories += (if (b) 1 else -1) * NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA
                priorityOptions.priorityCategories +=
                    (if (b && priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA != NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA)
                        1
                    else if (!b && priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA == NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA)
                        -1
                    else 0) * NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA
                priorityOptions.updateNotificationPolicy(notificationManager)
            }

            systemSwitch.setOnCheckedChangeListener { _, b ->
//                priorityOptions.priorityCategories += (if (b) 1 else -1) * NotificationManager.Policy.PRIORITY_CATEGORY_SYSTEM
                priorityOptions.priorityCategories +=
                    (if (b && priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_SYSTEM != NotificationManager.Policy.PRIORITY_CATEGORY_SYSTEM)
                        1
                    else if (!b && priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_SYSTEM == NotificationManager.Policy.PRIORITY_CATEGORY_SYSTEM)
                        -1
                    else 0) * NotificationManager.Policy.PRIORITY_CATEGORY_SYSTEM
                priorityOptions.updateNotificationPolicy(notificationManager)
            }

        } else {
            // Switches disabled for these features doesn't exist before sdk 28 P
            alarmsSwitch.isEnabled = false
            mediaSwitch.isEnabled = false
            systemSwitch.isEnabled = false
        }

    }

    override fun onResume() {
        super.onResume()
        priorityOptions.priorityCategories =
            notificationManager.notificationPolicy.priorityCategories
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

        //MESSAGES Settings
        if (priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES == NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES)
            messagesStatusText.text = when (priorityOptions.priorityMessageSenders) {
                NotificationManager.Policy.PRIORITY_SENDERS_ANY -> statusCalls[0]
                NotificationManager.Policy.PRIORITY_SENDERS_CONTACTS -> statusCalls[1]
                NotificationManager.Policy.PRIORITY_SENDERS_STARRED -> statusCalls[2]
                else -> "Error! Report to developer"
            }
        else
            messagesStatusText.text = statusCalls[3]

        //CONVERSATIONS Settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_CONVERSATIONS == NotificationManager.Policy.PRIORITY_CATEGORY_CONVERSATIONS)
                conversationsStatusText.text = when (priorityOptions.priorityConversationSenders) {
                    NotificationManager.Policy.CONVERSATION_SENDERS_ANYONE -> statusConversations[0]
                    NotificationManager.Policy.CONVERSATION_SENDERS_IMPORTANT -> statusConversations[1]
                    NotificationManager.Policy.CONVERSATION_SENDERS_NONE -> statusConversations[2]
                    else -> "Error! Report to developer"
                }
            else
                conversationsStatusText.text = statusConversations[2]
        }

        // PRIORITY_CATEGORY_REMINDERS
        remindersSwitch.isChecked =
            priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_REMINDERS == NotificationManager.Policy.PRIORITY_CATEGORY_REMINDERS

        // PRIORITY_CATEGORY_EVENTS
        eventsSwitch.isChecked =
            priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_EVENTS == NotificationManager.Policy.PRIORITY_CATEGORY_EVENTS

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // PRIORITY_CATEGORY_ALARMS
            alarmsSwitch.isChecked =
                priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS == NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS

            // PRIORITY_CATEGORY_MEDIA
            mediaSwitch.isChecked =
                (priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA) == NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA

            // PRIORITY_CATEGORY_SYSTEM
            systemSwitch.isChecked =
                priorityOptions.priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_SYSTEM == NotificationManager.Policy.PRIORITY_CATEGORY_SYSTEM
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

    }

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
         * @param notificationManager Notification Manager
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

}