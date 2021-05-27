package com.example.rshlnapp.ui.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.R
import com.example.rshlnapp.Utils
import com.example.rshlnapp.adapters.CartAdapter
import com.example.rshlnapp.adapters.ICartAdapter
import com.example.rshlnapp.daos.UserDao
import com.example.rshlnapp.databinding.CartFragmentBinding
import com.example.rshlnapp.models.CartItem
import com.example.rshlnapp.models.User
import com.example.rshlnapp.ui.choose_address.ChooseAddressFragment
import com.example.rshlnapp.ui.detail.DetailFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CartFragment : Fragment(), ICartAdapter {

    private lateinit var viewModel: CartViewModel
    private lateinit var binding: CartFragmentBinding
    private lateinit var adapter: CartAdapter
    private lateinit var userDao: UserDao
    private lateinit var currentUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CartFragmentBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        (activity as MainActivity).supportActionBar?.title = "Cart"
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        userDao = UserDao()
        binding.viewModel = viewModel
        adapter = CartAdapter(this)
        binding.cartRecyclerView.adapter = adapter

        initializeCurrentUser()

        viewModel.items.observe(viewLifecycleOwner,{
            adapter.notifyDataSetChanged()
        })

        viewModel.subtotal.observe(viewLifecycleOwner,{
            binding.totalAmountCart.text = it
        })

        binding.proceedToBuyCart.setOnClickListener {
            proceedToCheckout()
        }

        return binding.root
    }

    private fun initializeCurrentUser() {
        GlobalScope.launch {
            currentUser = userDao.getUserById(Utils.currentUserId).await().toObject(User::class.java)!!
        }
    }

    private fun proceedToCheckout() {
        val currentFragment = this
        //get the cart from the current user
        val cart = currentUser.cart
        //create instance of chooseAddressFragment
        val chooseAddressFragment = ChooseAddressFragment(currentFragment,cart)
        //navigate using fragment manager
        requireActivity().supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_content_main,chooseAddressFragment,getString(R.string.title_choose_address_fragment)).hide(currentFragment).commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val homeFragment = (activity as MainActivity).activeFragment
                    val currentFragment = this@CartFragment
                    requireActivity().supportFragmentManager.beginTransaction()
                        .remove(currentFragment).show(homeFragment).commit()
                    (activity as MainActivity).supportActionBar?.title = "RSHLN"
                    (activity as MainActivity).setDrawerLocked(false)
                    (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_cart).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onProductClicked(productId: String) {
        val currentFragment = this
        val productDetailFragment = DetailFragment(productId,"CartFragment")
        requireActivity().supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_content_main,productDetailFragment,getString(R.string.title_detail_fragment)).remove(currentFragment).commit()
    }

    override fun onAddClicked(cartItem: CartItem) {
        GlobalScope.launch {
            currentUser = userDao.getUserById(Utils.currentUserId).await().toObject(User::class.java)!!
            for (item in currentUser.cart.items){
                if (item.productId==cartItem.productId){
                    currentUser.cart.items.remove(item)
                    break
                }
            }
            cartItem.quantity += 1
            val price = cartItem.product.productPrice
            currentUser.cart.price += price
            currentUser.cart.items.add(cartItem)
            userDao.updateProfile(currentUser)
            withContext(Dispatchers.Main){
                adapter.notifyDataSetChanged()
                binding.totalAmountCart.text = "₹"  +currentUser.cart.price.toString()
            }
        }
    }

    override fun onDeleteClicked(cartItem: CartItem) {
        GlobalScope.launch {
            currentUser = userDao.getUserById(Utils.currentUserId).await().toObject(User::class.java)!!
            for (item in currentUser.cart.items){
                if (item.productId==cartItem.productId){
                    currentUser.cart.items.remove(item)
                    break
                }
            }
            if (cartItem.quantity>1){
                cartItem.quantity -= 1
                val price = cartItem.product.productPrice
                currentUser.cart.price -= price
                currentUser.cart.items.add(cartItem)
                userDao.updateProfile(currentUser)
            }else if(cartItem.quantity==1){
                cartItem.quantity = 0
                val price = cartItem.product.productPrice
                currentUser.cart.price -= price
                userDao.updateProfile(currentUser)
            }
            withContext(Dispatchers.Main){
                adapter.notifyDataSetChanged()
                binding.totalAmountCart.text = "₹"  +currentUser.cart.price.toString()
            }
        }
    }
}