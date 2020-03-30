package com.godeliveryservices.shop.models

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class Branch(
    @ColumnInfo var ShopName: String?,
    @ColumnInfo val Address: String,
    @ColumnInfo val Email: String,
    @ColumnInfo val JoiningDate: String,
    @ColumnInfo val Mobile: String,
    @ColumnInfo val Name: String,
    @ColumnInfo val Orders: Any,
    @ColumnInfo val Shop: Any,
    @PrimaryKey val ShopBranchID: Long,
    @ColumnInfo val ShopID: Long,
    @ColumnInfo val Status: String
)