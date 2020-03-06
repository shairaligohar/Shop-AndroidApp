package com.godeliveryservices.shop.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.godeliveryservices.shop.models.Branch
import com.godeliveryservices.shop.network.ApiService
import com.godeliveryservices.shop.network.NotificationService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PlaceOrderViewModel : ViewModel() {

    private val _placeOrderSuccess = MutableLiveData<Boolean>(false)
    val placeOrderSuccess: LiveData<Boolean> = _placeOrderSuccess

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    private val _branches = MutableLiveData<List<Branch>>()
    val branches: LiveData<List<Branch>> = _branches

    private val notificationService = NotificationService.create()
    private val apiService = ApiService.create()
    private var disposable: Disposable? = null


    private fun sendNotification(branchName: String, ids: List<String>) {
        val headers =
            mapOf("Authorization" to "key=AIzaSyC7BJ2jUDgM5LhOMPgHg-E4imx2WnetBa8")

        val notification = mapOf(
            "title" to "New Order",
            "body" to "$branchName has placed a new order. "
        )

        val body = mapOf<String, Any>(
            "notification" to notification,
            "registration_ids" to ids
        )

        disposable = notificationService.sendNotification(headers, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    if (result.code() == 200) {
                        _placeOrderSuccess.value = true
                        _showLoading.value = false
                    } else {
                        _responseMessage.value = result.message()
                        _showLoading.value = false
                    }
                },
                { error ->
                    _showLoading.value = false
                    _responseMessage.value = error.message
                }
            )
    }

    fun placeOrder(
        branchName: String,
        customerName: String,
        customerNumber: String,
        customerAddress: String,
        orderDetails: String,
        amount: String,
        instructions: String
    ) {
        val branch = _branches.value?.find { it.Name == branchName } ?: return
        _showLoading.value = true
        disposable = apiService.createOrder(
            branch.ShopBranchID,
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
                { success ->
                    if (success.code() == 200)
                        fetchRiders(branch)
                    else {
                        _responseMessage.value = success.errorBody()?.string()
                        _showLoading.value = false
                    }
                },
                { error ->
                    _showLoading.value = false
                    _responseMessage.value = error.message
                })
    }

    private fun fetchRiders(branch: Branch) {
        disposable = apiService.getRiders(branch.ShopBranchID)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ success ->
                if (success.code() == 200) {
                    val riderIds = success.body()?.map { it.Token } ?: return@subscribe
                    sendNotification(branch.Name, riderIds)
                } else {
                    _responseMessage.value = success.message()
                    _showLoading.value = false
                }
            }, { error ->
                _responseMessage.value = error.message
                _showLoading.value = false
            })
    }

    fun fetchBranches(shopId: Long) {
        _showLoading.value = true
        disposable = apiService.getBranches(shopId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { success ->
                    _branches.value = success.body()
                    _showLoading.value = false
                },
                { error ->
                    _responseMessage.value = error.message
                    _showLoading.value = false
                }
            )
    }
}