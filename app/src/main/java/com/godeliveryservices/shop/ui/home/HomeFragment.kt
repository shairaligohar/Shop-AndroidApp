package com.godeliveryservices.shop.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.godeliveryservices.shop.R
import com.godeliveryservices.shop.ui.orders_history.OrdersHistoryFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return root
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLoadedCallback {
            setupRidersObservable()
//            val markers = ArrayList<MarkerOptions>()
//            // Add a marker in Sydney and move the camera
//            val deliverBoy1 = LatLng(25.247317, 55.431333)
//            markers.add(
//                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_marker_32dp)).position(
//                    deliverBoy1
//                ).title("Delivery Boy 1")
//            )
//
//            // Add a marker in Sydney and move the camera
//            val deliverBoy2 = LatLng(25.260839, 55.389845)
//            markers.add(
//                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_marker_32dp)).position(
//                    deliverBoy2
//                ).title("Delivery Boy 2")
//            )
//
//            // Add a marker in Sydney and move the camera
//            val deliverBoy3 = LatLng(25.228835, 55.464972)
//            markers.add(
//                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_marker_32dp)).position(
//                    deliverBoy3
//                ).title("Delivery Boy 3")
//            )
//
//            // Add a marker in Sydney and move the camera
//            val deliverBoy4 = LatLng(25.209000, 55.420502)
//            markers.add(
//                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_marker_32dp)).position(
//                    deliverBoy4
//                ).title("Delivery Boy 4")
//            )
//
//            // Add a marker in Sydney and move the camera
//            val deliverBoy5 = LatLng(25.238533, 55.341212)
//            markers.add(
//                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_marker_32dp)).position(
//                    deliverBoy5
//                ).title("Delivery Boy 5")
//            )
//
//
//            val builder = LatLngBounds.builder()
//            for (marker in markers) {
//                builder.include(marker.position)
//                mMap.addMarker(marker)
//            }
//            val bounds = builder.build()
//            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    private fun setupRidersObservable() {
        homeViewModel.riderLocationMarkers.observe(viewLifecycleOwner, Observer { markers ->
            mMap.clear()
            val builder = LatLngBounds.builder()
            for (marker in markers) {
                builder.include(marker.position)
                mMap.addMarker(marker)
            }
            val bounds = builder.build()
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        })
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pending_orders_layout.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(OrdersHistoryFragment.ARG_TAB_POSITION, 0)
            findNavController().navigate(R.id.navigation_orders_history, bundle)
        }
        processing_orders_layout.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(OrdersHistoryFragment.ARG_TAB_POSITION, 1)
            findNavController().navigate(R.id.navigation_orders_history, bundle)
        }
        delivered_orders_layout.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(OrdersHistoryFragment.ARG_TAB_POSITION, 2)
            findNavController().navigate(R.id.navigation_orders_history, bundle)
        }

        place_order_fab.setOnClickListener {
            findNavController().navigate(R.id.navigation_place_order)
        }
    }
}