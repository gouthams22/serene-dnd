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
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.ui.activity.HomeActivity
import io.github.gouthams22.serenednd.ui.receiver.DNDStateReceiver

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var rootView: View
    private lateinit var dndStateReceiver: DNDStateReceiver
    private lateinit var notificationManager: NotificationManager

    private val logTag = "HomeFragment"
    private val onColorId = android.R.color.holo_green_dark
    private val offColorId = android.R.color.holo_red_dark
    private val Int.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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

        //DND button solid color for Versions above S(31)
        if (VERSION.SDK_INT >= VERSION_CODES.S) {
            setButtonSolidColor(android.R.color.system_accent1_200)
        } else {
            setButtonSolidColor(R.color.light_green_700)
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
        //        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment HomeFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            HomeFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}