package com.godeliveryservices.shop.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.godeliveryservices.shop.R
import com.godeliveryservices.shop.dummy.DummyContent
import com.godeliveryservices.shop.models.Notification
import com.godeliveryservices.shop.repository.PreferenceRepository
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*

class NotificationsFragment : Fragment(), OnListFragmentInteractionListener {
    // TODO: Customize parameters
    private var columnCount = 1

    private lateinit var notificationsViewModel: NotificationsViewModel
    private val recyclerViewAdapter =
        NotificationsRecyclerViewAdapter(
            arrayListOf(),
            this@NotificationsFragment
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        with(root.list) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = this@NotificationsFragment.recyclerViewAdapter
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        fetchData()
        setupObservers()
    }

    private fun init() {
        notificationsViewModel =
            ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        list_layout.setOnRefreshListener { fetchData() }
    }

    private fun fetchData() {
        notificationsViewModel.fetchNotifications(PreferenceRepository(requireContext()).getShopId())
    }

    private fun setupObservers() {
        notificationsViewModel.notifications.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { notifications ->
                recyclerViewAdapter.setValues(notifications)
            })

        notificationsViewModel.showLoading.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { flag ->
//            loading.visibility = if (flag) View.VISIBLE else View.GONE
                list_layout.isRefreshing = flag
            })

        notificationsViewModel.responseMessage.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { message ->
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
                unavailable_text.visibility = View.VISIBLE
            })
    }

    override fun onListFragmentInteraction(item: Notification?) {

    }
}