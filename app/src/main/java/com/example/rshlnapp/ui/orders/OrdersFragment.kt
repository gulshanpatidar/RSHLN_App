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
import com.example.rshlnapp.daos.UserDao
import com.example.rshlnapp.databinding.FragmentOrdersBinding
import com.example.rshlnapp.models.Order
import com.example.rshlnapp.models.User
import com.example.rshlnapp.ui.orderDetails.OrderDetailFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class OrdersFragment : Fragment(), IOrderAdapter {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var currentUser: User
    private lateinit var userDao: UserDao
    private lateinit var orderDao: OrderDao
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater)
        userDao = UserDao()
        orderDao = OrderDao()

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
                userDao.getUserById(Utils.currentUserId).await().toObject(User::class.java)!!
            val ordersToBeSent = ArrayList<Order>()
            val orders = currentUser.orders
            for (item in orders) {
                val order = orderDao.getOrderById(item).await().toObject(Order::class.java)!!
                ordersToBeSent.add(order)
            }
            withContext(Dispatchers.Main) {
                adapter = OrderAdapter(this@OrdersFragment, ordersToBeSent)
                binding.ordersRecyclerView.adapter = adapter
            }
        }
    }

    override fun onOrderClicked(order: Order) {
        val currentFragment = this
        val orderDetailFragment = OrderDetailFragment(currentFragment, order)
        requireActivity().supportFragmentManager.beginTransaction().add(
            R.id.nav_host_fragment_content_main,
            orderDetailFragment,
            getString(R.string.title_order_details)
        ).hide(currentFragment).commit()
    }
}