package com.godeliveryservices.shop.ui.orders_history

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.godeliveryservices.shop.models.Branch

class BranchesSpinnerAdapter(
    context: Context,
    @LayoutRes dropDownResourceId: Int = android.R.layout.simple_spinner_dropdown_item
) : ArrayAdapter<String>(context, dropDownResourceId) {

//    private var branches: List<Branch> = listOf()

    fun addItems(items: List<Branch>) {
        clear()
//        branches = items
//        addAll(branches)
        addAll(items.map { it.Name })
    }

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val textView = super.getView(position, convertView, parent) as TextView
//        textView.text = branches[position].Name
//        return textView
//    }
//
//    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val textView = super.getView(position, convertView, parent) as TextView
//        textView.text = branches[position].Name
//        return textView
//    }
}