package com.example.rshlnapp.ui.address

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.R
import com.example.rshlnapp.Utils
import com.example.rshlnapp.daos.UserDao
import com.example.rshlnapp.databinding.AddressFragmentBinding
import com.example.rshlnapp.models.Address
import com.example.rshlnapp.models.User
import com.example.rshlnapp.ui.cart.CartFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddressFragment : Fragment() {

    private lateinit var viewModel: AddressViewModel
    private lateinit var binding: AddressFragmentBinding
    private lateinit var userDao: UserDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddressFragmentBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(AddressViewModel::class.java)

        (activity as MainActivity).supportActionBar?.title = "Add new address"
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        userDao = UserDao()

        binding.addAddressButton.setOnClickListener {
            addTheAddress()
        }
        return binding.root
    }

    private fun addTheAddress() {
        if (binding.usernameInAddress.text.isNotEmpty() && binding.mobileNumberInAddress.text.isNotEmpty() && binding.houseNumberInAddress.text.isNotEmpty() && binding.streetNameInAddress.text.isNotEmpty() && binding.pincodeInAddress.text.isNotEmpty() && binding.cityInAddress.text.isNotEmpty()){
            val name = binding.usernameInAddress.text.toString()
            val number = binding.mobileNumberInAddress.text.toString()
            val houseNumber = binding.houseNumberInAddress.text.toString()
            val streetName = binding.streetNameInAddress.text.toString()
            val pincode = binding.pincodeInAddress.text.toString().toInt()
            val city = binding.cityInAddress.text.toString()
            val address = Address(name,number,pincode,houseNumber,streetName,city)
            GlobalScope.launch {
                val currentUser = userDao.getUserById(Utils.currentUserId).await().toObject(User::class.java)!!
                currentUser.addresses.add(address)
                userDao.updateProfile(currentUser)
                withContext(Dispatchers.Main){
                    goToProfileFragment()
                }
            }
        }else{
            Toast.makeText(requireContext(),"Please enter all the fields",Toast.LENGTH_LONG).show()
        }
    }

    private fun goToProfileFragment() {
        val profileFragment = (activity as MainActivity).activeFragment
        val currentFragment = this@AddressFragment
        requireActivity().supportFragmentManager.beginTransaction().remove(currentFragment).show(profileFragment).commit()
        (activity as MainActivity).supportActionBar?.title = "Your Profile"
        (activity as MainActivity).setDrawerLocked(false)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Toast.makeText(requireContext(),"Address added successfully",Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                    val profileFragment = (activity as MainActivity).activeFragment
                    val currentFragment = this@AddressFragment
                    requireActivity().supportFragmentManager.beginTransaction().remove(currentFragment).show(profileFragment).commit()
                    (activity as MainActivity).supportActionBar?.title = "Your Profile"
                    (activity as MainActivity).setDrawerLocked(false)
                    (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_cart).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }

}