package io.github.gouthams22.serenednd.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.model.LocationDND

/**
 * A simple [Fragment] subclass.
 * Use the [LocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LocationFragment : Fragment() {

    private class LocationAdapter(val locationList: ArrayList<LocationDND>) :
        Adapter<LocationAdapter.ViewHolder>() {
        // TODO remove item animation not working
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_location_card, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return locationList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.locationTitleTextView.text = locationList[position].name
            holder.coordinatesTextView.text = locationList[position].coordinateText
            holder.deleteLocationButton.setOnClickListener {
                locationList.removeAt(holder.bindingAdapterPosition)
                notifyItemRemoved(holder.bindingAdapterPosition)
            }
        }

        private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val locationTitleTextView: MaterialTextView
            val coordinatesTextView: MaterialTextView
            val editLocationButton: MaterialButton
            val deleteLocationButton: MaterialButton

            init {
                locationTitleTextView = itemView.findViewById(R.id.text_location_title)
                coordinatesTextView = itemView.findViewById(R.id.text_coordinates)
                editLocationButton = itemView.findViewById(R.id.button_location_edit)
                deleteLocationButton = itemView.findViewById(R.id.button_location_delete)
            }
        }
    }

    //TODO remove sample locations version
    private val locations: ArrayList<LocationDND> = arrayListOf(
        LocationDND(32.998452, -96.7386956, "Think Alive"),
        LocationDND(33.998452, -97.7386956, "Think Alone"),
        LocationDND(35.998452, -99.7386956, "Think Aloud"),
        LocationDND(38.998452, -91.7386956, "Think Apart"),
        LocationDND(32.988627, -96.747877, "Cecil Green Hall")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO Implement the features
        val locationRecyclerView: RecyclerView = view.findViewById(R.id.recycler_view_location)
        val linearLayoutManager = LinearLayoutManager(view.context)
        locationRecyclerView.layoutManager = linearLayoutManager
        val locationAdapter = LocationAdapter(locations)
        locationRecyclerView.adapter = locationAdapter

        val addLocationButton: ExtendedFloatingActionButton =
            view.findViewById(R.id.fab_add_location)
        addLocationButton.setOnClickListener {
            it.isEnabled = false
            openLocationDialog(view)
            it.isEnabled = true
        }

    }

    private fun openLocationDialog(view: View) {
        val locationAlertDialog = MaterialAlertDialogBuilder(view.context)
            .setTitle(R.string.add_location)
            .setMessage(R.string.add_location_description)
            .setView(R.layout.dialog_location_coordinates)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.add) { _, _ -> }
            .create()

        locationAlertDialog.show()

        locationAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            toggleDialogInput(locationAlertDialog, false)
            val name =
                locationAlertDialog.findViewById<TextInputEditText>(R.id.name_field)?.text.toString()
            val lat =
                locationAlertDialog.findViewById<TextInputEditText>(R.id.latitude_field)?.text.toString()
            val long =
                locationAlertDialog.findViewById<TextInputEditText>(R.id.longitude_field)?.text.toString()
            if (lat.isEmpty() || lat.isBlank() || long.isEmpty() || long.isBlank()) {
                Snackbar.make(
                    view,
                    getString(R.string.location_field_empty_error_message),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                locations.add(LocationDND(lat.toDouble(), long.toDouble(), name))
                view.findViewById<RecyclerView>(R.id.recycler_view_location)
                    .adapter?.notifyItemInserted(locations.size)
            }
            toggleDialogInput(locationAlertDialog, true)
            locationAlertDialog.dismiss()
        }
    }

    /**
     * Enables or disables inputs of fields in Alert Dialog
     * @param alertDialog alert dialog being used
     * @param isEnabled enable or disable inputs
     */
    private fun toggleDialogInput(alertDialog: AlertDialog, isEnabled: Boolean) {
        alertDialog.findViewById<TextInputEditText>(R.id.name_field)?.isEnabled = isEnabled
        alertDialog.findViewById<TextInputEditText>(R.id.latitude_field)?.isEnabled = isEnabled
        alertDialog.findViewById<TextInputEditText>(R.id.longitude_field)?.isEnabled = isEnabled
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = isEnabled
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment LocationFragment.
         */
        @JvmStatic
        fun newInstance() = LocationFragment()
    }
}