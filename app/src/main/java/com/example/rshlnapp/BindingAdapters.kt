package com.example.rshlnapp

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rshlnapp.adapters.AddressAdapter
import com.example.rshlnapp.adapters.CartAdapter
import com.example.rshlnapp.adapters.ProductAdapter
import com.example.rshlnapp.models.Address
import com.example.rshlnapp.models.CartItem
import com.example.rshlnapp.models.CartItemOffline
import com.example.rshlnapp.models.Product

@BindingAdapter("productListData")
fun bindProductRecyclerView(recyclerView: RecyclerView, data: List<Product>?){
    val adapter = recyclerView.adapter as ProductAdapter
    adapter.submitList(data)
}

@BindingAdapter("cartListData")
fun bindCartRecyclerView(recyclerView: RecyclerView, data: List<CartItemOffline>?){
    val adapter = recyclerView.adapter as CartAdapter
    adapter.submitList(data)
}