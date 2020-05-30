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

    private var orderNumber: String? = null

    private val _placeOrderSuccess = MutableLiveData<String>()
    val placeOrderSuccess: LiveData<String> = _placeOrderSuccess

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    private val _branches = MutableLiveData<List<Branch>>()
    val branches: LiveData<List<Branch>> = _branches

    private val notificationService = NotificationService.create()
    private val apiService = ApiService.create()
    private var disposable: Disposable? = null


    private fun sendNotification(branch: Branch, ids: List<String>) {
        val headers =
            mapOf("Authorization" to "key=AIzaSyC7BJ2jUDgM5LhOMPgHg-E4imx2WnetBa8")

        val notification = mapOf(
            "title" to branch.ShopName,
            "body" to "${branch.Name} has placed a new order. "
        )

        val body = mapOf(
            "notification" to notification,
            "registration_ids" to ids
        )

        disposable = notificationService.sendNotification(headers, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    _showLoading.value = false
                    if (result.code() == 200) {
                        _placeOrderSuccess.value = orderNumber
                    } else {
                        _responseMessage.value = result.errorBody()?.string()
                    }
                },
                {
                    _showLoading.value = false
                    _responseMessage.value = "Connection Timeout"
                }
            )
    }

    fun placeOrder(
        shopName: String?,
        branchName: String,
        customerName: String,
        customerNumber: String,
        customerAddress: String,
        orderDetails: String,
        amount: String,
        instructions: String
    ) {
        val branch = _branches.value?.find { it.Name == branchName } ?: return
        branch.ShopName = shopName
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
                    if (success.code() == 200) {
                        orderNumber = success.body()
                        fetchRiders(branch)
                    } else {
                        _responseMessage.value = success.errorBody()?.string()
                        _showLoading.value = false
                    }
                },
                {
                    _showLoading.value = false
                    _responseMessage.value = "Connection Error"
                })
    }

    private fun fetchRiders(branch: Branch) {
        disposable = apiService.getRiders(branch.ShopBranchID)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ success ->
                if (success.code() == 200) {
                    val riderIds = success.body()?.map { it.Token } ?: return@subscribe
                    sendNotification(branch, riderIds)
                } else {
                    _responseMessage.value = success.errorBody()?.string()
                    _showLoading.value = false
                }
            }, { error ->
                _responseMessage.value = "Connection Error"
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
                    _showLoading.value = false
                    if (success.code() == 200) {
                        _branches.value = success.body()
                    } else {
                        _responseMessage.value = success.errorBody()?.string()
                    }
                },
                {
                    _responseMessage.value = "Connection Error"
                    _showLoading.value = false
                }
            )
    }
}