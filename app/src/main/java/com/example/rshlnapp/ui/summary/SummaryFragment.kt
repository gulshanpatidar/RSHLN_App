package com.example.rshlnapp.ui.summary

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.R
import com.example.rshlnapp.adapters.SummaryProductAdapter
import com.example.rshlnapp.daos.ProductDao
import com.example.rshlnapp.databinding.SummaryFragmentBinding
import com.example.rshlnapp.models.*
import com.example.rshlnapp.ui.payment.PaymentFragment
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
    private lateinit var cartItemsOffline: ArrayList<CartItemOffline>

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

        binding.continueButtonSummary.setOnClickListener {
            //go to payment screen
            goToPaymentFragment()
        }

        return binding.root
    }

    private fun goToPaymentFragment() {
        val currentFragment = this
        val paymentFragment = PaymentFragment(currentFragment,currentUser,address,cart,cartItemsOffline)
        requireActivity().supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_content_main,paymentFragment,getString(R.string.title_payment_fragment)).hide(currentFragment).commit()
    }

    private fun setupRecyclerView() {
        GlobalScope.launch {
            val items = cart.items
            cartItemsOffline = ArrayList()
            for (i in 0..(items.size-1)){
                val item = items[i]
                val product = productDao.getProductById(item.productId).await().toObject(Product::class.java)!!
                cartItemsOffline.add(CartItemOffline(item.productId,item.quantity,product))
            }
            withContext(Dispatchers.Main){
                adapter = SummaryProductAdapter(cartItemsOffline)
                binding.productsRecyclerViewSummary.adapter = adapter
                binding.totalAmountSummary.text = "???" + cart.price
                binding.payableAmountSummary.text = "???" + cart.price
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
        setHasOptionsMenu(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val currentFragment = this@SummaryFragment
                requireActivity().supportFragmentManager.beginTransaction().remove(currentFragment).show(previousFragment).commit()
                (activity as MainActivity).supportActionBar?.title = "Choose an address"
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