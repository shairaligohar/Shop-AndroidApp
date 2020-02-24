package com.godeliveryservices.shop.network

import io.reactivex.Observable
import okhttp3.internal.http.RealResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://web.godeliveryservice.com")
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }

    @GET("/api/shop")
    fun login(@Query("username") username: String, @Query("password") password: String): Observable<Response<APIResponse>>

    @POST("/api/order")
    fun createOrder(
        @Query("ShopID") shopId: Long,
        @Query("CustomerName") customerName: String,
        @Query("CustomerNumber") customerNumber: String,
        @Query("CustomerAddress") customerAddress: String,
        @Query("OrderDetails") orderDetails: String,
        @Query("Amount") amount: String,
        @Query("Instructions") instructions: String
    ): Observable<Response<Void>>
}

interface NotificationService {
    companion object {
        fun create(): NotificationService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://fcm.googleapis.com")
                .build()
            return retrofit.create(NotificationService::class.java)
        }
    }

    @POST("/fcm/send")
    fun sendNotification(@HeaderMap headers: Map<String, String>, @Body body: Map<String, @JvmSuppressWildcards Any>): Observable<Response<APIResponse>>
}