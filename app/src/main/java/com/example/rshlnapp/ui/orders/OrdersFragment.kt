package com.example.rshlnapp.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.R
import com.example.rshlnapp.Utils
import com.example.rshlnapp.adapters.IOrderAdapter
import com.example.rshlnapp.adapters.OrderAdapter
import com.example.rshlnapp.daos.OrderDao
import com.example.rshlnapp.daos.ProductDao
import com.example.rshlnapp.daos.UserDao
import com.example.rshlnapp.databinding.FragmentOrdersBinding
import com.example.rshlnapp.models.CartItemOffline
import com.example.rshlnapp.models.Order
import com.example.rshlnapp.models.Product
import com.example.rshlnapp.models.User
import com.example.rshlnapp.ui.orderDetails.OrderDetailFragment
import com.google.android.gms.common.util.ArrayUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class OrdersFragment : Fragment(), IOrderAdapter {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var currentUser: User
    private lateinit var userDao: UserDao
    private lateinit var orderDao: OrderDao
    private lateinit var adapter: OrderAdapter
    private lateinit var productDao: ProductDao
    private lateinit var currentUserId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater)
        userDao = UserDao()
        orderDao = OrderDao()
        productDao = ProductDao()
        currentUserId = Firebase.auth.currentUser!!.uid

        setupRecyclerView()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home){
            (activity as MainActivity).drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        GlobalScope.launch {
            currentUser =
                userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            val ordersToBeSent = ArrayList<Order>()
            val orders = currentUser.orders
            for (item in orders) {
                val order = orderDao.getOrderById(item).await().toObject(Order::class.java)!!
                val cart = order.cart
                ordersToBeSent.add(order)
            }
            withContext(Dispatchers.Main) {
                adapter = OrderAdapter(this@OrdersFragment, ordersToBeSent)
                binding.ordersRecyclerView.adapter = adapter
            }
        }
    }

    override fun onOrderClicked(order: Order) {
        val items = order.cart.items
        GlobalScope.launch {
            val cartItemsOffline = ArrayList<CartItemOffline>()
            for (i in 0..(items.size-1)){
                val item = items[i]
                val product = productDao.getProductById(item.productId).await().toObject(Product::class.java)!!
                val cartItemOffline = CartItemOffline(item.productId,item.quantity,product)
                cartItemsOffline.add(cartItemOffline)
            }
            withContext(Dispatchers.Main){
                val currentFragment = this@OrdersFragment
                val orderDetailFragment = OrderDetailFragment(currentFragment, order, cartItemsOffline)
                requireActivity().supportFragmentManager.beginTransaction().add(
                    R.id.nav_host_fragment_content_main,
                    orderDetailFragment,
                    getString(R.string.title_order_details)
                ).hide(currentFragment).commit()
            }
        }
    }
}