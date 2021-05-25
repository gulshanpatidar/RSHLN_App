package com.example.rshlnapp.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.R
import com.example.rshlnapp.Utils
import com.example.rshlnapp.daos.ProductDao
import com.example.rshlnapp.daos.UserDao
import com.example.rshlnapp.databinding.DetailFragmentBinding
import com.example.rshlnapp.models.CartItem
import com.example.rshlnapp.models.Product
import com.example.rshlnapp.models.User
import com.example.rshlnapp.ui.cart.CartFragment
import com.example.rshlnapp.ui.choose_address.ChooseAddressFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DetailFragment(val productId: String,val fromWhere: String) : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private lateinit var productDao: ProductDao
    private lateinit var product: Product
    private lateinit var binding: DetailFragmentBinding
    private lateinit var currentUser: User
    private lateinit var userDao: UserDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailFragmentBinding.inflate(inflater)
        productDao = ProductDao()
        userDao = UserDao()
        showProductDetails(productId)
        (activity as MainActivity).supportActionBar?.title = "Product Details"
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        //add click listener to add product to the cart
        binding.addToCartButton.setOnClickListener {
            addProductToCart(productId)
        }

        binding.buyNowButton.setOnClickListener {
            startCheckoutProcess()
        }

        return binding.root
    }

    private fun startCheckoutProcess() {
        val chooseAddressFragment = ChooseAddressFragment(this)
        val currentFragment = this
        requireActivity().supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_content_main,chooseAddressFragment,getString(R.string.title_choose_address_fragment)).hide(currentFragment).commit()
    }

    private fun addProductToCart(productId: String) {
        //get the user and then add the product to cart
        GlobalScope.launch {
            currentUser = userDao.getUserById(Utils.currentUserId).await().toObject(User::class.java)!!
            val cart = currentUser.cart
            val items = cart.items
            var quantity = 1
            for (item in items){
                if (item.productId==productId){
                    quantity += item.quantity
                    items.remove(item)
                    break
                }
            }
            val newItem = CartItem(productId,quantity)
            cart.items.add(newItem)
            cart.price = cart.price + product.productPrice
            currentUser.cart = cart
            userDao.updateProfile(currentUser)
            requireActivity().runOnUiThread{
                Toast.makeText(requireActivity(),"Item added to cart",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (fromWhere=="HomeFragment"){
                    val homeFragment = (activity as MainActivity).activeFragment
                    val currentFragment = this@DetailFragment
                    requireActivity().supportFragmentManager.beginTransaction().remove(currentFragment).show(homeFragment).commit()
                    (activity as MainActivity).supportActionBar?.title = "RSHLN"
                    (activity as MainActivity).setDrawerLocked(false)
                    (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }else{
                    val cartFragment = CartFragment()
                    val currentFragment = this@DetailFragment
                    requireActivity().supportFragmentManager.beginTransaction().remove(currentFragment).add(R.id.nav_host_fragment_content_main,cartFragment,getString(R.string.title_cart_fragment)).commit()
                    (activity as MainActivity).supportActionBar?.title = "Cart"
                }
            }
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_cart).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }

    private fun showProductDetails(productId: String) {
        productDao.getProductById(productId).addOnSuccessListener {
            product = it.toObject(Product::class.java)!!
            binding.productImageInDetail.load(product.productImage){
                transformations(RoundedCornersTransformation())
            }
            binding.productNameInDetail.text = product.productName
            binding.productDescriptionInDetail.text = product.description
            binding.productPriceInDetail.setText(product.productPrice.toString())
            if (!product.availability){
                binding.buyNowButton.isEnabled = false
                binding.addToCartButton.isEnabled = false
                binding.productAvailabilityInDetail.visibility = View.GONE
            }
        }
    }
}