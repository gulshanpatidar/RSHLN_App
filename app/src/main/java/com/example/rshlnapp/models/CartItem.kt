package com.example.rshlnapp.models

data class CartItem(
    val productId: String = "",
    var quantity: Int = 0,
    val product: Product = Product()
)
