package io.github.gouthams22.serenednd.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.serenednd.R
import io.github.gouthams22.serenednd.model.LocationDND

/**
 * A simple [Fragment] subclass.
 * Use the [LocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LocationFragment : Fragment() {

    private class LocationAdapter(val locationList: List<LocationDND>) :
        Adapter<LocationAdapter.ViewHolder>() {
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

        private const val TAG = "LocationFragment"
    }
}