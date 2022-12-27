package io.github.gouthams22.serenednd.ui.fragment

import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.serenednd.DNDPreference
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.ui.activity.HomeActivity
import io.github.gouthams22.serenednd.ui.receiver.DNDStateReceiver
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var rootView: View
    private lateinit var dndStateReceiver: DNDStateReceiver
    private lateinit var notificationManager: NotificationManager
    private lateinit var dndPreference: DNDPreference

    private val dndType = arrayListOf("Total Silence", "Priority Only", "Calls Only")
    private val dndTypeId =
        arrayListOf(R.id.total_silence_button, R.id.priority_only_button, R.id.calls_only_button)
    private var currentType = "None"
    private val logTag = "HomeFragment"
    private val onColorId = R.color.dnd_button_on
    private val offColorId = R.color.dnd_button_off
    private val Int.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initializing rootView
        rootView = view

        // Get Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance()

        notificationManager =
            view.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Receiver to listen to the state of DND
        dndStateReceiver = DNDStateReceiver()
        // passing supportFragmentManager's last fragment(assuming it as HomeFragment) to Receiver
        dndStateReceiver.setHomeFragment((view.context as HomeActivity).supportFragmentManager.fragments.last() as HomeFragment)

        val welcomeText: TextView = view.findViewById(R.id.welcome_text)
        welcomeText.text = firebaseAuth.currentUser?.email.toString()

        // MaterialButtonToggleGroup
        val typeToggle: MaterialButtonToggleGroup = view.findViewById(R.id.type_toggle)
        typeToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked)
                when (checkedId) {
                    dndTypeId[0] -> {
                        setTypePreferences(dndType[0])
                    }
                    dndTypeId[1] -> {
                        setTypePreferences(dndType[1])
                    }
                    dndTypeId[2] -> {
                        setTypePreferences(dndType[2])
                    }
                }
        }

        // Preferences Data Store
        dndPreference = DNDPreference(view.context)
        //TODO: Update Preference and MaterialToggleButtonGroup
        dndPreference.typePreference.asLiveData().observe(viewLifecycleOwner) { value ->
            Log.d(logTag, "onCreateView: DataStore Type Preferences: $value")
            currentType = value
            when (value) {
                "None" -> setTypePreferences(dndType[0])
                dndType[0] ->
                    if (typeToggle.checkedButtonId != dndTypeId[0])
                        typeToggle.check(dndTypeId[0])

                dndType[1] ->
                    if (typeToggle.checkedButtonId != dndTypeId[1])
                        typeToggle.check(dndTypeId[1])
                dndType[2] ->
                    if (typeToggle.checkedButtonId != dndTypeId[2])
                        typeToggle.check(dndTypeId[2])
            }
        }

        // Duration Selection field
        val durationTextView: MaterialAutoCompleteTextView =
            view.findViewById(R.id.duration_auto_complete)

        durationTextView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                // None
                0 -> {
                    //TODO: Implement none
                    Toast.makeText(view.context, "None", Toast.LENGTH_SHORT).show()
                }
                // Time based DND
                1 -> {
                    //TODO: Implement time
                    Toast.makeText(view.context, "Time", Toast.LENGTH_SHORT).show()
                }
                // Location based DND
                2 -> {
                    //TODO: Implement location
                    Toast.makeText(view.context, "Location", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // DND Image Button to toggle
        val dndButton: ImageButton = view.findViewById(R.id.button_dnd)
        dndButton.setOnClickListener {
            val isDndOn = isDndTurnedOn()
            if (isDndOn) {
                // Turn off
                turnDndOff(view)
            } else {
                //Turn on
                turnDndOn(view)
            }
        }

        //DND button solid color for Versions above S(31)
        if (VERSION.SDK_INT >= VERSION_CODES.S) {
            setButtonSolidColor(android.R.color.system_accent1_200)
        } else {
            setButtonSolidColor(android.R.color.transparent)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        rootView.context.registerReceiver(
            dndStateReceiver,
            IntentFilter(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)
        )
        setDndButtonColor()
        Log.d(logTag, "onStart: DND state: ${notificationManager.currentInterruptionFilter}")
    }

    override fun onStop() {
        super.onStop()
        rootView.context.unregisterReceiver(dndStateReceiver)
    }

    private fun turnDndOn(view: View) {
        //TODO: Update DND based on preferences
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
    }

    private fun turnDndOff(view: View) {
        //TODO: Update DND based on preferences
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
    }

    private fun setTypePreferences(type: String) {
        lifecycleScope.launch {
            dndPreference.storeTypePreference(type)
        }
    }


    /**
     * Function to change the stroke color of dnd button
     * @param colorId: Id of color to be assigned to dnd button
     */
    private fun setButtonStrokeColor(colorId: Int) {
        val dndButton: ImageButton = rootView.findViewById(R.id.button_dnd)
        val rippleDrawable: RippleDrawable = dndButton.background as RippleDrawable
        val shapeDrawable = rippleDrawable.getDrawable(0) as GradientDrawable
        shapeDrawable.setStroke(
            16.px,
            resources.getColor(colorId, (rootView.context as Context).theme)
        )
    }

    /**
     * Function to change the solid color of dnd button
     * @param colorId: Id of color to be assigned to dnd button
     */
    private fun setButtonSolidColor(colorId: Int) {
        val dndButton: ImageButton = rootView.findViewById(R.id.button_dnd)
        val rippleDrawable: RippleDrawable = dndButton.background as RippleDrawable
        val shapeDrawable = rippleDrawable.getDrawable(0) as GradientDrawable
        shapeDrawable.color = ColorStateList.valueOf(
            resources.getColor(
                colorId,
                (rootView.context as Context).theme
            )
        )
    }

    fun setDndButtonColor() {
        if (isDndTurnedOn()) {
            setButtonStrokeColor(onColorId)
        } else {
            setButtonStrokeColor(offColorId)
        }
    }

    private fun isDndTurnedOn() =
        notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}