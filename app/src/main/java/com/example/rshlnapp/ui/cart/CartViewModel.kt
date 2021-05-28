package com.example.rshlnapp.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rshlnapp.Utils
import com.example.rshlnapp.daos.ProductDao
import com.example.rshlnapp.daos.UserDao
import com.example.rshlnapp.models.CartItem
import com.example.rshlnapp.models.Product
import com.example.rshlnapp.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CartViewModel : ViewModel() {
    private val _items = MutableLiveData<List<CartItem>>()
    val items: LiveData<List<CartItem>>
        get() = _items

    private val _subtotal = MutableLiveData<String>()
    val subtotal: LiveData<String>
        get() = _subtotal

    private val _listIsEmpty = MutableLiveData<Boolean>()
    val listIsEmpty: LiveData<Boolean>
        get() = _listIsEmpty

    private var mProducts: ArrayList<Product> = ArrayList()
    private var mItems: ArrayList<CartItem> = ArrayList()
    private val productDao = ProductDao()
    private lateinit var currentUser: User

    init {
        _subtotal.value = ""
        _items.value = mItems
        retreiveAllProducts()
    }

    private fun retreiveAllProducts() {
        viewModelScope.launch {
            currentUser =
                UserDao().getUserById(Utils.currentUserId).await().toObject(User::class.java)!!
            val cartItems = currentUser.cart.items
            _subtotal.value = "₹" + currentUser.cart.price.toString()
            for (document in cartItems) {
                val productId = document.productId
                val product =
                    productDao.getProductById(productId).await().toObject(Product::class.java)!!
                val cartItem = CartItem(productId, document.quantity, product)
                mItems.add(cartItem)
            }
            withContext(Dispatchers.Main) {
                _items.value = mItems
                if (items.value?.size == 0) {
                    _listIsEmpty.value = true
                }
            }
        }
    }
}