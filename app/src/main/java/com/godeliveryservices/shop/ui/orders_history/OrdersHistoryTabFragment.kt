package com.godeliveryservices.shop.ui.orders_history

import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.godeliveryservices.shop.R
import com.godeliveryservices.shop.models.Order
import kotlinx.android.synthetic.main.fragment_tab_orders_history.*
import kotlinx.android.synthetic.main.fragment_tab_orders_history.view.*

/**
 * A placeholder fragment containing a simple view.
 */
class OrdersHistoryTabFragment : Fragment(),
    OnListFragmentInteractionListener {

    // TODO: Customize parameters
    private var columnCount = 1

    private lateinit var pageViewModel: OrdersHistoryViewModel
    private val adapter by lazy { OrderHistoryRecyclerViewAdapter(emptyList(), this) }
    private val sectionNumber by lazy {
        arguments?.getInt(ARG_SECTION_NUMBER)
            ?: throw IllegalArgumentException("Unable to retrieve intent data")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel =
            ViewModelProviders.of(requireParentFragment()).get(OrdersHistoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tab_orders_history, container, false)
        with(root.list) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = this@OrdersHistoryTabFragment.adapter
        }
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): OrdersHistoryTabFragment {
            return OrdersHistoryTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onListFragmentInteraction(item: Order?) {

    }

    override fun onPhoneNumberInteraction(item: Order) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${item.CustomerNumber}")
        ContextCompat.startActivity(requireContext(), intent, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupViews()
    }

    private fun fetchData() {
        when (sectionNumber) {
            0 -> pageViewModel.fetchOrders("Pending")
            1 -> pageViewModel.fetchOrders("Active")
            2 -> pageViewModel.fetchOrders("Delivered")
        }
    }

    private fun setupObservers() {
        pageViewModel.pendingOrders.observe(viewLifecycleOwner, Observer { orders ->
            if (sectionNumber == 0) {
                adapter.setValues(orders)
                unavailable_text.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
            }
        })
        pageViewModel.processingOrders.observe(viewLifecycleOwner, Observer { orders ->
            if (sectionNumber == 1) {
                adapter.setValues(orders)
                unavailable_text.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
            }
        })
        pageViewModel.deliveredOrders.observe(viewLifecycleOwner, Observer { orders ->
            if (sectionNumber == 2) {
                adapter.setValues(orders)
                unavailable_text.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
            }
        })

        pageViewModel.showLoading.observe(viewLifecycleOwner, Observer { flag ->
//            loading.visibility = if (flag) View.VISIBLE else View.GONE
            list_layout.isRefreshing = flag
        })

        pageViewModel.orderFilters.observe(viewLifecycleOwner, Observer { filters ->
            fetchData()
        })


        pageViewModel.responseMessagePending.observe(viewLifecycleOwner, Observer { message ->
            if (sectionNumber == 0) {
//                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                unavailable_text.visibility = View.VISIBLE
                unavailable_text.text = "No Pending Orders"
            }
        })

        pageViewModel.responseMessageProcessing.observe(viewLifecycleOwner, Observer { message ->
            if (sectionNumber == 1) {
//                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                unavailable_text.text = "No Active Orders"
                unavailable_text.visibility = View.VISIBLE
            }
        })

        pageViewModel.responseMessageDelivered.observe(viewLifecycleOwner, Observer { message ->
            if (sectionNumber == 2) {
//                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                unavailable_text.text = "No Delivered Orders"
                unavailable_text.visibility = View.VISIBLE
            }
        })

        pageViewModel.responseMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            unavailable_text.visibility = View.VISIBLE
        })
    }

    private fun setupViews() {
        list_layout.setOnRefreshListener { fetchData() }
    }
}