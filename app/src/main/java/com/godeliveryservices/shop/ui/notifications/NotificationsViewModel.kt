package com.godeliveryservices.shop.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.godeliveryservices.shop.models.Notification
import com.godeliveryservices.shop.network.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class NotificationsViewModel : ViewModel() {

    private val apiService = ApiService.create()
    private var disposable: Disposable? = null

    private val _showLoading = MutableLiveData(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    fun fetchNotifications(shopId: Long) {
        _showLoading.value = true
        disposable = apiService.getNotifications(shopId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { success ->
                    _notifications.value = success.body()
                    _showLoading.value = false
                },
                { error ->
                    _responseMessage.value = "Connection Error"
                    _showLoading.value = false
                }
            )
    }
}