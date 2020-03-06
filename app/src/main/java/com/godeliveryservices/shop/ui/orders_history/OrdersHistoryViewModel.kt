package com.godeliveryservices.shop.ui.orders_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.godeliveryservices.shop.models.Branch
import com.godeliveryservices.shop.models.Order
import com.godeliveryservices.shop.network.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class OrdersHistoryViewModel : ViewModel() {

    private val apiService = ApiService.create()
    private var disposable: Disposable? = null

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    private val _responseMessagePending = MutableLiveData<String>()
    val responseMessagePending: LiveData<String> = _responseMessagePending

    private val _responseMessageProcessing = MutableLiveData<String>()
    val responseMessageProcessing: LiveData<String> = _responseMessageProcessing

    private val _responseMessageDelivered = MutableLiveData<String>()
    val responseMessageDelivered: LiveData<String> = _responseMessageDelivered

    val orderFilters = MutableLiveData<OrderFilters>()

    private val _pendingOrders = MutableLiveData<List<Order>>()
    val pendingOrders: LiveData<List<Order>> = _pendingOrders

    private val _processingOrders = MutableLiveData<List<Order>>()
    val processingOrders: LiveData<List<Order>> = _processingOrders

    private val _deliveredOrders = MutableLiveData<List<Order>>()
    val deliveredOrders: LiveData<List<Order>> = _deliveredOrders

    private val _branches = MutableLiveData<List<Branch>>()
    val branches: LiveData<List<Branch>> = _branches

    fun fetchOrders(status: String? = null) {
        _showLoading.value = true
        val filters = orderFilters.value
        disposable = apiService.getOrdersByShop(
            status = status,
            startDate = filters?.startDate,
            endDate = filters?.endDate,
            branchId = filters?.shopBranchID
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ success ->
                _showLoading.value = false
                if (success.code() == 200) {
                    when (status) {
                        "Pending" -> _pendingOrders.value = success.body()
                        "Active" -> _processingOrders.value = success.body()
                        "Delivered" -> _deliveredOrders.value = success.body()
                    }
                } else {
                    when (status) {
                        "Pending" -> _responseMessagePending.value = success.message()
                        "Active" -> _responseMessageProcessing.value = success.message()
                        "Delivered" -> _responseMessageDelivered.value = success.message()
                    }
                    clearOrders(status)
                }
            }, { error ->
                when (status) {
                    "Pending" -> _responseMessagePending.value = error.message
                    "Active" -> _responseMessageProcessing.value = error.message
                    "Delivered" -> _responseMessageDelivered.value = error.message
                }
                _showLoading.value = false
                clearOrders(status)
            })
    }

    private fun clearOrders(status: String?) {
        when (status) {
            "Pending" -> _pendingOrders.value = listOf()
            "Active" -> _processingOrders.value = listOf()
            "Delivered" -> _deliveredOrders.value = listOf()
        }
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