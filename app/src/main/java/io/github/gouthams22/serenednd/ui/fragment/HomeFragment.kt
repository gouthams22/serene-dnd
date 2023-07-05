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
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.preferences.DNDPreference
import io.github.gouthams22.serenednd.ui.activity.HomeActivity
import io.github.gouthams22.serenednd.ui.receiver.DNDStateReceiver
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

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
    private lateinit var dndDuration: Array<String>

    private val dndType = arrayListOf("Total Silence", "Priority Only", "Calls Only")
    private val dndTypeId =
        arrayListOf(R.id.total_silence_button, R.id.priority_only_button, R.id.calls_only_button)
    private val timeDivisions =
        arrayListOf("15 m", "30 m", "45 m", "1 h", "1 h 30 m", "2 h", "3 h", "4 h", "5 h", "6 h")
    private val timeDivisionValueInMinutes =
        arrayListOf<Long>(15, 30, 45, 60, 90, 120, 180, 240, 300, 360)

    private var currentType = "None"
    private var currentDuration = "Always"
    private val onColorId = R.color.dnd_button_on
    private val offColorId = R.color.dnd_button_off
    private val Int.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    class TimeWorker(
        appContext: Context,
        params: WorkerParameters
    ) : CoroutineWorker(appContext, params) {
        private val currentContext = appContext
        override suspend fun doWork(): Result {
            Log.d(TAG, "doWork: Doing Work")
            return try {
                val notificationManager: NotificationManager =
                    currentContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                Result.success()
            } catch (e: Exception) {
                Log.e(TAG, "doWork: ${e.printStackTrace()}")
                Result.failure()
            }
        }

        override suspend fun getForegroundInfo(): ForegroundInfo {
            return super.getForegroundInfo()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        Log.d(TAG, "onCreateView: ${VERSION.SDK_INT}")

        // Initializing rootView
        rootView = view

        dndDuration = resources.getStringArray(R.array.dnd_duration)

        // Get Firebase Auth instance
        firebaseAuth = FirebaseAuth.getInstance()

        notificationManager =
            view.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Receiver to listen to the state of DND
        // Passing supportFragmentManager's last fragment(assuming it as HomeFragment) to Receiver
        dndStateReceiver = DNDStateReceiver(
            (view.context as HomeActivity).supportFragmentManager.fragments.last() as HomeFragment,
            view
        )

        Log.d(TAG, "onCreateView: ${firebaseAuth.currentUser != null}")

        val timeRootView: ConstraintLayout = view.findViewById(R.id.layout_time)
        val timeSlider: Slider = view.findViewById(R.id.time_slider)
        timeSlider.valueFrom = 0F
        timeSlider.valueTo = timeDivisions.size.toFloat() - 1
        timeSlider.stepSize = 1F
        timeSlider.value = 0F
        timeSlider.setLabelFormatter { value -> timeDivisions[value.toInt()] }
        Log.d(TAG, "timeSlider: ${timeSlider.value}")
        timeSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                return
            }

            override fun onStopTrackingTouch(slider: Slider) {
                Log.d(TAG, "timeSlider: ${timeSlider.value}")
            }

        })

        // MaterialButtonToggleGroup
        val typeToggle: MaterialButtonToggleGroup = view.findViewById(R.id.type_toggle)
        typeToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            Log.d(TAG, "onButtonClick: ToggleGroup: ${dndType[dndTypeId.indexOf(checkedId)]}")
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

        // Duration MaterialAutoCompleteTextView Field
        val durationTextView: MaterialAutoCompleteTextView =
            view.findViewById(R.id.duration_auto_complete)
        durationTextView.setOnItemClickListener { _, _, position, _ ->
            Log.d(TAG, "onCreateView: Duration: ${dndDuration[position]}")
            when (position) {
                // Always
                0 -> {
                    setDurationPreferences(dndDuration[0])
                    Toast.makeText(view.context, dndDuration[0], Toast.LENGTH_SHORT).show()
                }
                // Time based DND
                1 -> {
                    setDurationPreferences(dndDuration[1])
                    Toast.makeText(view.context, dndDuration[1], Toast.LENGTH_SHORT).show()
                }
                // Location based DND
                2 -> {
                    //TODO: Implement location
                    setDurationPreferences(dndDuration[2])
                    Toast.makeText(view.context, dndDuration[2], Toast.LENGTH_SHORT).show()
                }
            }
        }

        // DND Image Button to toggle
        val dndButton: ImageButton = view.findViewById(R.id.button_dnd)
        dndButton.setOnClickListener {
            Log.d(TAG, "onClick: dndButton")
            val isDndOn = isDndTurnedOn()
            if (isDndOn) {
                // Turn off
                turnDndOff(view)
            } else {
                //Turn on
                turnDndOn(view)
            }
        }

        // Preferences Data Store
        dndPreference = DNDPreference(view.context)
        dndPreference.typePreference.asLiveData().observe(viewLifecycleOwner) { value ->
            Log.d(TAG, "DNDPreference: Type Preferences: $value")
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
        dndPreference.durationPreference.asLiveData().observe(viewLifecycleOwner) { value ->
            Log.d(TAG, "dndPreference: Duration Preferences: $value")
            currentDuration = value
            timeRootView.visibility = View.GONE
            when (value) {
                "None" -> setDurationPreferences(dndDuration[0])
                // Always
                dndDuration[0] ->
                    if (durationTextView.text.toString() != dndDuration[0])
                        durationTextView.setText(dndDuration[0], false)
                // Time Based DND
                dndDuration[1] -> {
                    timeRootView.visibility = View.VISIBLE
                    if (durationTextView.text.toString() != dndDuration[1]) {
                        durationTextView.setText(dndDuration[1], false)
                    }
                }
                // Location Based DND
                dndDuration[2] ->
                    if (durationTextView.text.toString() != dndDuration[2])
                        durationTextView.setText(dndDuration[2], false)

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
        Log.d(TAG, "onStop: dndStateReceiver started")
        rootView.context.registerReceiver(
            dndStateReceiver,
            IntentFilter(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)
        )
        updateDnd(rootView)
        Log.d(TAG, "onStart: DND state: ${notificationManager.currentInterruptionFilter}")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: dndStateReceiver stopped")
        rootView.context.unregisterReceiver(dndStateReceiver)
    }

    private fun updateInputAccessibility(view: View, isEnabled: Boolean) {
        view.findViewById<Slider>(R.id.time_slider).isEnabled = isEnabled
        for (i in view.findViewById<MaterialButtonToggleGroup>(R.id.type_toggle).allViews) {
            i.isEnabled = isEnabled
        }
        view.findViewById<TextInputLayout>(R.id.duration_text_input_layout).isEnabled = isEnabled
    }

    private fun turnDndOn(view: View) {
        // TODO: Update DND based on preferences
        Log.d(TAG, "turnDndOn: turning on")
        // Disable Inputs
        updateInputAccessibility(view, false)
        when (currentDuration) {
            // Always
            dndDuration[0] ->
                when (currentType) {
                    dndType[0] -> notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
                    dndType[1] -> notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                    dndType[2] -> notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS)
                }
            // Time
            dndDuration[1] -> {
                when (currentType) {
                    dndType[0] -> notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
                    dndType[1] -> notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                    dndType[2] -> notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS)
                }

                // Create Work Request for time based schedule
                val timeWorkRequest: OneTimeWorkRequest =
                    OneTimeWorkRequestBuilder<TimeWorker>()
                        .setInitialDelay(
                            timeDivisionValueInMinutes[view.findViewById<Slider>(R.id.time_slider).value.toInt()],
                            TimeUnit.MINUTES
                        )
                        .addTag("serene_time").build()

                // Add the request to WorkManager
                WorkManager.getInstance(view.context).enqueue(timeWorkRequest)
            }
            // Location
            dndDuration[2] -> notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
        }
    }

    private fun turnDndOff(view: View) {
        // TODO: Update DND based on preferences
        Log.d(TAG, "turnDndOn: turning off")
        // Enable Inputs
        updateInputAccessibility(view, true)
        WorkManager.getInstance(view.context).cancelAllWorkByTag("serene_time")
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
    }

    private fun setTypePreferences(type: String) {
        Log.d(TAG, "setTypePreferences: $type")
        lifecycleScope.launch {
            dndPreference.storeTypePreference(type)
        }
    }

    private fun setDurationPreferences(duration: String) {
        Log.d(TAG, "setDurationPreferences: $duration")
        lifecycleScope.launch {
            dndPreference.storeDurationPreference(duration)
        }
    }


    /**
     * Function to change the stroke color of dnd button
     * @param colorId Id of color to be assigned to dnd button
     */
    private fun setButtonStrokeColor(colorId: Int) {
        Log.d(TAG, "setButtonStrokeColor: $colorId")
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
     * @param colorId Id of color to be assigned to dnd button
     */
    private fun setButtonSolidColor(colorId: Int) {
        Log.d(TAG, "setButtonSolidColor: $colorId")
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

    fun updateDnd(view: View) {
        if (isDndTurnedOn()) {
            setButtonStrokeColor(onColorId)
            updateInputAccessibility(view, false)
        } else {
            updateInputAccessibility(view, true)
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
        private const val TAG = "HomeFragment"
    }
}