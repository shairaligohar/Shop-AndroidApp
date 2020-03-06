package com.godeliveryservices.shop.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.godeliveryservices.shop.R
import com.godeliveryservices.shop.repository.PreferenceRepository
import com.godeliveryservices.shop.ui.orders_history.BranchesSpinnerAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_place_order.*


class PlaceOrderFragment : Fragment() {

    private lateinit var placeOrderViewModel: PlaceOrderViewModel
    private val adapter by lazy { BranchesSpinnerAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        placeOrderViewModel =
            ViewModelProviders.of(this).get(PlaceOrderViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_place_order, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()
        setupViews()
        setupObservers()
    }

    private fun fetchData() {
        placeOrderViewModel.fetchBranches(PreferenceRepository(requireContext()).getShopId())
    }

    private fun setupViews() {
        proceed_button.setOnClickListener {
            placeOrder()
        }
        dropdown_branch_choice.setAdapter(adapter)
    }

    private fun setupObservers() {
        placeOrderViewModel.showLoading.observe(viewLifecycleOwner, Observer { flag ->
            loading.visibility = if (flag) View.VISIBLE else View.GONE
        })

        placeOrderViewModel.responseMessage.observe(viewLifecycleOwner, Observer { message ->
            Snackbar.make(place_order_content, message, Snackbar.LENGTH_LONG).show()
        })

        placeOrderViewModel.placeOrderSuccess.observe(viewLifecycleOwner, Observer {
            if (it) parentFragmentManager.popBackStack()
        })

        placeOrderViewModel.branches.observe(viewLifecycleOwner, Observer { branches ->
            adapter.addItems(branches)
        })
    }

    private fun placeOrder() {
        placeOrderViewModel.placeOrder(
            branchName = dropdown_branch_choice.text.toString(),
            customerName = rider_name_text.text.toString(),
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