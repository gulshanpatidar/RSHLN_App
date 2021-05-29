package com.example.rshlnapp.ui.orderDetails

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.R
import com.example.rshlnapp.adapters.SummaryProductAdapter
import com.example.rshlnapp.databinding.OrderDetailFragmentBinding
import com.example.rshlnapp.models.Order
import com.example.rshlnapp.ui.orders.OrdersFragment

class OrderDetailFragment(val previousFragment: Fragment,val order: Order) : Fragment() {

    private lateinit var viewModel: OrderDetailViewModel
    private lateinit var binding: OrderDetailFragmentBinding
    private lateinit var adapter: SummaryProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(OrderDetailViewModel::class.java)
        binding = OrderDetailFragmentBinding.inflate(inflater)
        setupRecyclerView()
        (activity as MainActivity).supportActionBar?.title = "Order Details"
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        return binding.root
    }

    private fun setupRecyclerView() {
        val cart = order.cart
        val adapter = SummaryProductAdapter(cart)
        binding.productsRecyclerViewOrder.adapter = adapter
        binding.orderIdOrderDetails.text = "ORDER ID - " + order.orderId
        binding.orderedOnOrder.text = "Ordered " + order.orderDate
        binding.orderStatusOrder.text = "Order status - " + order.orderStatus.toString()
        binding.userNameAddressOrder.text = order.address.userName
        binding.phoneNumberAddressOrder.text = order.address.mobileNumber
        binding.houseAndStreetAddressOrder.text = order.address.houseNumber + ", " + order.address.streetName
        binding.cityPincodeAddressOrder.text = order.address.pincodeOfAddress.toString() + ", " + order.address.city
        binding.totalAmountOrder.text = "₹" + cart.price
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentFragment = this@OrderDetailFragment
                    if (previousFragment is OrdersFragment){
                        requireActivity().supportFragmentManager.beginTransaction().remove(currentFragment).show(previousFragment).commit()
                        (activity as MainActivity).supportActionBar?.title = "Your Profile"
                        (activity as MainActivity).setDrawerLocked(false)
                        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
                    }else{
                        requireActivity().supportFragmentManager.beginTransaction()
                            .remove(currentFragment).show(previousFragment).commit()
                        (activity as MainActivity).supportActionBar?.title = "Choose Payment Option"
                        //TODO: manage the backstack from here.
                    }
                }
            })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_cart).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home){
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}