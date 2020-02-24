package com.godeliveryservices.shop.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.godeliveryservices.shop.network.ApiService
import com.godeliveryservices.shop.network.NotificationService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PlaceOrderViewModel : ViewModel() {

    private val _placeOrderSuccess = MutableLiveData<Boolean>(false)
    val placeOrderSuccess: LiveData<Boolean> = _placeOrderSuccess

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val notificationService = NotificationService.create()
    private val apiService = ApiService.create()
    private var disposable: Disposable? = null

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }

    val text: LiveData<String> = _text


    private fun sendNotification(token: String) {
        val headers =
            mapOf("Authorization" to "key=AIzaSyC7BJ2jUDgM5LhOMPgHg-E4imx2WnetBa8")

        val notification = mapOf(
            "title" to "New Order",
            "body" to "Order#123 from Branch X for the Customer abc"
        )

        val body = mapOf<String, Any>(
            "notification" to notification,
            "to" to token
        )

        disposable = notificationService.sendNotification(headers, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    _placeOrderSuccess.value = true
                    _showLoading.value = false
                    //                    result.message
                },
                { error ->
                    _showLoading.value = false
                    //                    error.message
                }
            )
    }

    fun placeOrder(
        customerName: String,
        customerNumber: String,
        customerAddress: String,
        orderDetails: String,
        amount: String,
        instructions: String
    ) {
        _showLoading.value = true
        disposable = apiService.createOrder(
            1,
            customerName,
            customerNumber,
            customerAddress,
            orderDetails,
            amount,
            instructions
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { success -> if (success.code() == 200) fetchToken()
                    else
                    _showLoading.value = false },
                { error ->
                    _showLoading.value = false})
    }

    private fun fetchToken() {
        val database = FirebaseDatabase.getInstance()
        val refDatabase = database.getReference("Rider Token")
        refDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                sendNotification(p0.value.toString())
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}