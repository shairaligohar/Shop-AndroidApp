package com.godeliveryservices.shop.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.godeliveryservices.shop.models.Branch
import com.godeliveryservices.shop.models.Shop

@Dao
interface UserDao<T> {

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadShop(): Shop

    @Insert
    fun insertAll(vararg item: T)

    @Delete
    fun delete(vararg item: T)
}