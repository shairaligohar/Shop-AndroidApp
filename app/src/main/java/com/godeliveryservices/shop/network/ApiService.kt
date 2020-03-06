package com.godeliveryservices.shop.network

import com.godeliveryservices.shop.models.Branch
import com.godeliveryservices.shop.models.Order
import com.godeliveryservices.shop.models.Rider
import com.godeliveryservices.shop.models.Shop
import io.reactivex.Observable
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
    fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Observable<Response<Shop>>

    @POST("/api/order")
    fun createOrder(
        @Query("ShopBranchID") shopId: Long,
        @Query("CustomerName") customerName: String,
        @Query("CustomerNumber") customerNumber: String,
        @Query("CustomerAddress") customerAddress: String,
        @Query("OrderDetails") orderDetails: String,
        @Query("Amount") amount: String,
        @Query("Instructions") instructions: String
    ): Observable<Response<Void>>

    @GET("/api/shop")
    fun getBranches(@Query("ShopID") shopID: Long): Observable<Response<List<Branch>>>

    @GET("/api/shop")
    fun getRiders(@Query("ShopBranchID") shopBranchID: Long): Observable<Response<List<Rider>>>

    @GET("/api/order")
    fun getOrdersByShop(
        @Query("Status") status: String?,
        @Query("StartDate") startDate: String?,
        @Query("EndDate") endDate: String?,
        @Query("ShopBranchID") branchId: Long?
    ): Observable<Response<List<Order>>>

    @GET("/api/rider")
    fun getRiders(): Observable<Response<List<Rider>>>
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
    fun sendNotification(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Observable<Response<APIResponse>>
}