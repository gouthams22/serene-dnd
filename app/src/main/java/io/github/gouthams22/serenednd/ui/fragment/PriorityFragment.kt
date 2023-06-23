package io.github.gouthams22.serenednd.ui.fragment

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
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
        val view: View = inflater.inflate(R.layout.fragment_priority, container, false)

        val remindersSwitch: SwitchMaterial = view.findViewById(R.id.switch_reminders)
        val eventsSwitch: SwitchMaterial = view.findViewById(R.id.switch_events)
        val alarmsSwitch: SwitchMaterial = view.findViewById(R.id.switch_alarms)

        val notificationManager: NotificationManager =
            view.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Priority categories: https://developer.android.com/reference/android/app/NotificationManager.Policy#priorityCategories
        //notificationPolicy.priorityCategories contains the sum of selected categories corresponding to their bits 1+10+1000=1011 (1+2+8=11)
        val policyPriorityCategories = notificationManager.notificationPolicy.priorityCategories
        //TODO complete categories
        if (policyPriorityCategories and 1 == 1) {
            remindersSwitch.isChecked = true
        }
        if (policyPriorityCategories and 10 == 10) {
            eventsSwitch.isChecked = true
        }
//        if (policyPriorityCategories and 1000 == 1000) {
//
//        }
//        if (policyPriorityCategories and 10000 == 10000) {
//
//        }
        if (policyPriorityCategories and 100000 == 100000) {
            alarmsSwitch.isChecked = true
        }

        return view
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
}