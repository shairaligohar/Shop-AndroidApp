package com.godeliveryservices.shop.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.godeliveryservices.shop.R


class PlaceOrderFragment : Fragment() {

    private lateinit var placeOrderViewModel: PlaceOrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        placeOrderViewModel =
            ViewModelProviders.of(this).get(PlaceOrderViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_place_order, container, false)

//        val spinner: Spinner = root.findViewById(R.id.dropdown_branch)
// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter.createFromResource(
//            requireActivity(),
//            R.array.braches,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            // Specify the layout to use when the list of choices appears
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            // Apply the adapter to the spinner
//            spinner.adapter = adapter
//        }

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.branches,
            android.R.layout.simple_spinner_dropdown_item
        )

        val editTextFilledExposedDropdown: AutoCompleteTextView =
            root.findViewById(R.id.dropdown_branch_choice)
        editTextFilledExposedDropdown.setAdapter(adapter)

//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        placeOrderViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        return root
    }
}