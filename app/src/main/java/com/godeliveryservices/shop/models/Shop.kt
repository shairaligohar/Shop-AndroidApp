package com.godeliveryservices.shop.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Shop(
    @ColumnInfo val Address: String,
    @ColumnInfo val Email: String,
    @ColumnInfo val JoiningDate: String,
    @ColumnInfo val MainStatus: Any,
    @ColumnInfo val Mobile: String,
    @ColumnInfo val Name: String,
    @ColumnInfo val Password: Any,
    @ColumnInfo val ShopBranches: Any,
    @PrimaryKey val ShopID: Long,
    @ColumnInfo val Status: String,
    @ColumnInfo val Username: String
)