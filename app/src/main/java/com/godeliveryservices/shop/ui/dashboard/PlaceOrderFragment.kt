package com.godeliveryservices.shop.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.godeliveryservices.shop.R
import kotlinx.android.synthetic.main.fragment_place_order.*


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

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.branches,
            android.R.layout.simple_spinner_dropdown_item
        )

        val editTextFilledExposedDropdown: AutoCompleteTextView =
            root.findViewById(R.id.dropdown_branch_choice)
        editTextFilledExposedDropdown.setAdapter(adapter)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        proceed_button.setOnClickListener {
            placeOrder()
        }
    }

    private fun setupObservers() {
        placeOrderViewModel.showLoading.observe(viewLifecycleOwner, Observer { flag ->
            loading.visibility = if (flag) View.VISIBLE else View.GONE
        })

        placeOrderViewModel.placeOrderSuccess.observe(viewLifecycleOwner, Observer {
            if (it) parentFragmentManager.popBackStack()
        })
    }

    private fun placeOrder() {
        placeOrderViewModel.placeOrder(
            customerName = customer_name_text.text.toString(),
            customerNumber = customer_phone_text.text.toString(),
            customerAddress = customer_address_text.text.toString(),
            orderDetails = order_details_text.text.toString(),
            amount = order_amount_text.text.toString(),
            instructions = order_instructions_text.text.toString()
        )
    }

    override fun onPause() {
        super.onPause()

//        disposable?.dispose()
    }
}