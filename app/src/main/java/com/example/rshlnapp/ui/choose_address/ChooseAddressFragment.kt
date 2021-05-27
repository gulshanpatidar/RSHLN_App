package com.example.rshlnapp.ui.choose_address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.R
import com.example.rshlnapp.Utils
import com.example.rshlnapp.adapters.ChooseAddressAdapter
import com.example.rshlnapp.adapters.IChooseAddressAdapter
import com.example.rshlnapp.daos.UserDao
import com.example.rshlnapp.databinding.ChooseAddressFragmentBinding
import com.example.rshlnapp.models.Cart
import com.example.rshlnapp.models.User
import com.example.rshlnapp.ui.address.AddressFragment
import com.example.rshlnapp.ui.detail.DetailFragment
import com.example.rshlnapp.ui.summary.SummaryFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChooseAddressFragment(val previousFragment: Fragment, val cart: Cart) : Fragment(),
    IChooseAddressAdapter {

    private lateinit var binding: ChooseAddressFragmentBinding
    private lateinit var adapter: ChooseAddressAdapter
    private lateinit var userDao: UserDao
    private lateinit var currentUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChooseAddressFragmentBinding.inflate(inflater)
        (activity as MainActivity).supportActionBar?.title = "Choose Address"
        userDao = UserDao()
        setupRecyclerView()

        //add new address button click listener
        binding.addNewAddressChooseAddress.setOnClickListener {
            val currentFragment = this
            val addressFragment = AddressFragment(currentFragment)
            requireActivity().supportFragmentManager.beginTransaction().add(
                R.id.nav_host_fragment_content_main,
                addressFragment,
                getString(R.string.title_address_fragment)
            ).hide(currentFragment).commit()
        }
        //continue button action
        binding.deliverHereButtonChooseAddress.setOnClickListener {
            val position = adapter.lastSelectedPosition
            if (position < 0) {
                Toast.makeText(requireContext(), "Please choose an address", Toast.LENGTH_SHORT)
                    .show()
            } else {
                goToPaymentFragment(position)
            }
        }

        return binding.root
    }

    private fun goToPaymentFragment(position: Int) {
        val address = currentUser.addresses[position]
        val currentFragment = this
        val summaryFragment = SummaryFragment(currentFragment, currentUser, address, cart)
        requireActivity().supportFragmentManager.beginTransaction().add(
            R.id.nav_host_fragment_content_main,
            summaryFragment,
            getString(R.string.title_summary_fragment)
        ).hide(currentFragment).commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentFragment = this@ChooseAddressFragment
                    requireActivity().supportFragmentManager.beginTransaction()
                        .remove(currentFragment).show(previousFragment).commit()
                    if (previousFragment is DetailFragment) {
                        (activity as MainActivity).supportActionBar?.title = "Product Details"
                    } else {
                        (activity as MainActivity).supportActionBar?.title = "Cart"
                    }
                }
            })
    }

    fun setupRecyclerView() {
        GlobalScope.launch {
            currentUser =
                userDao.getUserById(Utils.currentUserId).await().toObject(User::class.java)!!
            val addresses = currentUser.addresses
            withContext(Dispatchers.Main) {
                adapter = ChooseAddressAdapter(addresses, this@ChooseAddressFragment)
                binding.chooseAddressRecyclerView.adapter = adapter
            }
        }
    }
}