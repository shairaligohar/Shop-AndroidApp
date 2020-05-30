package com.godeliveryservices.shop.ui.dashboard

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.godeliveryservices.shop.R
import com.godeliveryservices.shop.repository.PreferenceRepository
import com.godeliveryservices.shop.ui.orders_history.BranchesSpinnerAdapter
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
            proceed_button.isEnabled = flag.not()
        })

        placeOrderViewModel.responseMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        })

        placeOrderViewModel.placeOrderSuccess.observe(viewLifecycleOwner, Observer { orderNumber ->
            showOrderSuccessDialog(orderNumber)
        })

        placeOrderViewModel.branches.observe(viewLifecycleOwner, Observer { branches ->
            adapter.addItems(branches)
        })
    }

    private fun showOrderSuccessDialog(orderNumber: String) {
        AlertDialog.Builder(context)
            .setTitle("Order Placed Successfully")
            .setMessage("Order number is $orderNumber")
            .setPositiveButton(
                android.R.string.ok
            ) { _, _ ->
                parentFragmentManager.popBackStack()
            }
            .setCancelable(false)
            .show()
    }

    private fun placeOrder() {
        if (dropdown_branch_choice.text.toString().isEmpty()) {
            Toast.makeText(context, "Please select branch", Toast.LENGTH_LONG).show()
            return
        }
        placeOrderViewModel.placeOrder(
            shopName = PreferenceRepository(requireContext()).getShopName(),
            branchName = dropdown_branch_choice.text.toString(),
            customerName = rider_name_text.text.toString(),
            customerNumber = customer_phone_text.text.toString(),
            customerAddress = customer_address_text.text.toString(),
            orderDetails = order_details_text.text.toString(),
            amount = order_amount_text.text.toString(),
            instructions = order_instructions_text.text.toString()
        )
    }
}