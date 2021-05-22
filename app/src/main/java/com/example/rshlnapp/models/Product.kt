package com.example.rshlnapp.models

data class Product(var productId: String = "",val productName: String="", var productImage: String="", var productPrice: Float = 0F, var availability: Boolean = true,val reviews: ArrayList<Review> = ArrayList(),var description: String = "")