package com.godeliveryservices.shop.repository

import android.content.Context
import androidx.preference.PreferenceManager
import com.godeliveryservices.shop.models.Shop

class PreferenceRepository(val context: Context) {

    private val sharedPreference by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    object Keys {
        const val SHOP_USER_NAME = "SHOP_USER_NAME"
        const val SHOP_NAME = "SHOP_NAME"
        const val SHOP_ID = "SHOP_ID"
    }

    fun saveShopData(shop: Shop) {
        sharedPreference.edit().apply {
            putString(Keys.SHOP_USER_NAME, shop.Username)
            putString(Keys.SHOP_NAME, shop.Name)
            putLong(Keys.SHOP_ID, shop.ShopID)
        }.apply()
    }

    fun getShopId(): Long {
        return sharedPreference.getLong(Keys.SHOP_ID, 0)
    }

    fun getShopName(): String? {
        return sharedPreference.getString(Keys.SHOP_NAME, "")
    }

    fun getShopUserName(): String? {
        return sharedPreference.getString(Keys.SHOP_USER_NAME, "")
    }
}