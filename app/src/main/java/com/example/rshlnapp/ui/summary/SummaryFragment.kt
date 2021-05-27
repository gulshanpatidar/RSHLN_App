package com.example.rshlnapp.ui.summary

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.R
import com.example.rshlnapp.adapters.SummaryProductAdapter
import com.example.rshlnapp.daos.ProductDao
import com.example.rshlnapp.databinding.SummaryFragmentBinding
import com.example.rshlnapp.models.Address
import com.example.rshlnapp.models.Cart
import com.example.rshlnapp.models.Product
import com.example.rshlnapp.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SummaryFragment(
    val previousFragment: Fragment,
    val currentUser: User,
    val address: Address,
    val cart: Cart
) : Fragment() {

    private lateinit var viewModel: SummaryViewModel
    private lateinit var binding: SummaryFragmentBinding
    private lateinit var productDao: ProductDao
    private lateinit var adapter: SummaryProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SummaryViewModel::class.java)
        binding = SummaryFragmentBinding.inflate(inflater)
        (activity as MainActivity).supportActionBar?.title = "Order Summary"

        productDao = ProductDao()
        setUpAddress()
        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        GlobalScope.launch {
            val items = cart.items
            for (item in items){
                val product = productDao.getProductById(item.productId).await().toObject(Product::class.java)!!
                item.product = product
            }
            withContext(Dispatchers.Main){
                adapter = SummaryProductAdapter(cart)
                binding.productsRecyclerViewSummary.adapter = adapter
                binding.totalAmountSummary.text = "₹" + cart.price
                binding.payableAmountSummary.text = "₹" + cart.price
            }
        }
    }

    private fun setUpAddress() {
        binding.userNameAddressSummary.text = address.userName
        binding.phoneNumberAddressSummary.text = address.mobileNumber
        binding.houseAndStreetAddressSummary.text = address.houseNumber + ", " + address.streetName
        binding.cityPincodeAddressSummary.text = address.pincodeOfAddress.toString() + ", " + address.city
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val currentFragment = this@SummaryFragment
                requireActivity().supportFragmentManager.beginTransaction().remove(currentFragment).show(previousFragment).commit()
                (activity as MainActivity).supportActionBar?.title = "Choose an address"
            }
        })
    }

}