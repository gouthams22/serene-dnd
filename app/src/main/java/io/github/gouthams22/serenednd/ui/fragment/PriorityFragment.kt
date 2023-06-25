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
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.serenednd.R

/**
 * A simple [Fragment] subclass.
 * Use the [PriorityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PriorityFragment : Fragment() {

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

        val remindersSwitch: MaterialSwitch = view.findViewById(R.id.switch_reminders)
        val eventsSwitch: MaterialSwitch = view.findViewById(R.id.switch_events)
        val alarmsSwitch: MaterialSwitch = view.findViewById(R.id.switch_alarms)
        val mediaSwitch: MaterialSwitch = view.findViewById(R.id.switch_media)
        val systemSwitch: MaterialSwitch = view.findViewById(R.id.switch_system)

        val notificationManager: NotificationManager =
            view.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Priority categories: https://developer.android.com/reference/android/app/NotificationManager.Policy#priorityCategories
        //notificationPolicy.priorityCategories contains the sum of selected categories corresponding to their bits 1+10+1000=1011 (1+2+8=11)
        val policyPriorityCategories = notificationManager.notificationPolicy.priorityCategories
        val policyPriorityCalls = notificationManager.notificationPolicy.priorityCallSenders
        val policyPriorityMessages = notificationManager.notificationPolicy.priorityMessageSenders
        // CALLS Settings

        if (policyPriorityCategories and 8 == 8)
            callsStatusText.text = when (policyPriorityCalls) {
                0 -> statusCalls[0]
                1 -> statusCalls[1]
                2 -> statusCalls[2]
                else -> "Error! Report to developer"
            }
        else
            callsStatusText.text = statusCalls[3]

        callsCard.setOnClickListener {
            it.isEnabled = false
            val popupMenu = PopupMenu(view.context, it)
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

                when (item.itemId) {
                    R.id.item_anyone -> callsStatusText.text = statusCalls[0]
                    R.id.item_contacts -> callsStatusText.text = statusCalls[1]
                    R.id.item_starred -> callsStatusText.text = statusCalls[2]
                    R.id.item_none -> callsStatusText.text = statusCalls[3]
                }
                true
            }
            popupMenu.menuInflater.inflate(R.menu.contacts_popup_menu, popupMenu.menu)
            popupMenu.show()
            it.isEnabled = true
        }

        //MESSAGES Settings
        if (policyPriorityCategories and 4 == 4)
            messagesStatusText.text = when (policyPriorityMessages) {
                0 -> statusCalls[0]
                1 -> statusCalls[1]
                2 -> statusCalls[2]
                else -> "Error! Report to developer"
            }
        else
            callsStatusText.text = statusCalls[3]

        messagesCard.setOnClickListener {
            it.isEnabled = false
            val popupMenu = PopupMenu(view.context, it)
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

                when (item.itemId) {
                    R.id.item_anyone -> callsStatusText.text = statusCalls[0]
                    R.id.item_contacts -> callsStatusText.text = statusCalls[1]
                    R.id.item_starred -> callsStatusText.text = statusCalls[2]
                    R.id.item_none -> callsStatusText.text = statusCalls[3]
                }
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

            if (policyPriorityCategories and 256 == 256)
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
                val popupMenu = PopupMenu(view.context, it)
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

                    when (item.itemId) {
                        R.id.menu_conversations_all -> conversationsStatusText.text =
                            statusConversations[0]

                        R.id.menu_conversations_priority -> conversationsStatusText.text =
                            statusConversations[1]

                        R.id.menu_conversations_none -> conversationsStatusText.text =
                            statusConversations[2]
                    }
                    true
                }
                popupMenu.menuInflater.inflate(R.menu.conversations_popup_menu, popupMenu.menu)
                popupMenu.show()
                it.isEnabled = true
            }

        }

        // PRIORITY_CATEGORY_REMINDERS
        if (policyPriorityCategories and 1 == 1) {
            Log.d(TAG, "onCreateView: Priority: Reminders")
            remindersSwitch.isChecked = true
        }
        // PRIORITY_CATEGORY_EVENTS
        if (policyPriorityCategories and 2 == 2) {
            Log.d(TAG, "onCreateView: Priority: Events")
            eventsSwitch.isChecked = true
        }
        // PRIORITY_CATEGORY_CALLS
        if (policyPriorityCategories and 8 != 8) {
            Log.d(TAG, "onCreateView: Priority: Calls")
            callsStatusText.text = statusCalls[3]
        }
        // PRIORITY_CATEGORY_ALARMS
        if (policyPriorityCategories and 32 == 32) {
            Log.d(TAG, "onCreateView: Priority: Alarms")
            alarmsSwitch.isChecked = true
        }
        // PRIORITY_CATEGORY_MEDIA
        if (policyPriorityCategories and 64 == 64) {
            Log.d(TAG, "onCreateView: Priority: Media")
            mediaSwitch.isChecked = true
        }
        // PRIORITY_CATEGORY_SYSTEM
        if (policyPriorityCategories and 128 == 128) {
            Log.d(TAG, "onCreateView: Priority: System")
            systemSwitch.isChecked = true
        }

        //Priority number display Temporary
        view.findViewById<MaterialButton>(R.id.button_priority_num).setOnClickListener {
            it.isEnabled = false
            Snackbar.make(
                view,
                Integer.toBinaryString(policyPriorityCategories),
                Snackbar.LENGTH_SHORT
            ).show()
            it.isEnabled = true
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