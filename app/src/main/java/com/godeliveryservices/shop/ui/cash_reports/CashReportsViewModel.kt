package com.godeliveryservices.shop.ui.cash_reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.godeliveryservices.shop.models.Branch
import com.godeliveryservices.shop.models.Order
import com.godeliveryservices.shop.network.ApiService
import com.godeliveryservices.shop.ui.orders_history.OrderFilters
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CashReportsViewModel : ViewModel() {
    private val apiService = ApiService.create()
    private var disposable: Disposable? = null

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    private val _branches = MutableLiveData<List<Branch>>()
    val branches: LiveData<List<Branch>> = _branches

    val orderFilters = MutableLiveData<OrderFilters>()

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    fun fetchOrders() {
        _showLoading.value = true
        val filters = orderFilters.value
        disposable = apiService.getOrdersByShop(
            status = "Delivered",
            startDate = filters?.startDate,
            endDate = filters?.endDate,
            branchId = filters?.shopBranchID
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ success ->
                _showLoading.value = false
                if (success.code() == 200) {
                    _orders.value = success.body()
                } else {
                    _responseMessage.value = success.message()
                }
            }, { error ->
                _showLoading.value = false
                _responseMessage.value = error.message
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