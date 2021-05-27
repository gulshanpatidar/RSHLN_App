package com.example.rshlnapp.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.R
import com.example.rshlnapp.Utils
import com.example.rshlnapp.daos.OrderDao
import com.example.rshlnapp.daos.UserDao
import com.example.rshlnapp.databinding.PaymentFragmentBinding
import com.example.rshlnapp.models.Address
import com.example.rshlnapp.models.Cart
import com.example.rshlnapp.models.Order
import com.example.rshlnapp.models.User
import com.example.rshlnapp.ui.orderDetails.OrderDetailFragment
import java.text.SimpleDateFormat
import java.util.*

class PaymentFragment(
    val previousFragment: Fragment,
    val currentUser: User,
    val address: Address,
    val cart: Cart
) : Fragment() {

    private lateinit var viewModel: PaymentViewModel
    private lateinit var binding: PaymentFragmentBinding
    private lateinit var orderDao: OrderDao
    private lateinit var userDao: UserDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PaymentFragmentBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)

        orderDao = OrderDao()
        userDao = UserDao()

        binding.placeOrderButton.setOnClickListener {
            if (binding.codRadioButton.isChecked) {
                placeTheOrder()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please choose an valid option",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        (activity as MainActivity).supportActionBar?.title = "Choose Payment Option"

        return binding.root
    }

    private fun placeTheOrder() {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val simpleTimeFormat = SimpleDateFormat("hh:mm aa")
        val dateString = simpleDateFormat.format(System.currentTimeMillis())
        val timeString = simpleTimeFormat.format(System.currentTimeMillis())
        val time = String.format("at %s on %s", timeString, dateString)
        val order = Order(
            cart = cart,
            userId = Utils.currentUserId,
            orderDate = time,
            paymentMethod = "COD",
            address = address
        )
        currentUser.cart = Cart()
        userDao.updateProfile(currentUser)
        orderDao.placeOrder(order)
        val currentFragment = this
        val orderDetailsFragment = OrderDetailFragment(currentFragment)
        requireActivity().supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_content_main,orderDetailsFragment,getString(R.string.title_order_details)).hide(currentFragment).commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentFragment = this@PaymentFragment
                    requireActivity().supportFragmentManager.beginTransaction()
                        .remove(currentFragment).show(previousFragment).commit()
                    (activity as MainActivity).supportActionBar?.title = "Order Summary"
                }
            })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_cart).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }

}