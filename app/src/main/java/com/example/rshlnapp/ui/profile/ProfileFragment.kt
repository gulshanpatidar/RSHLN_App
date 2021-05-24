package com.example.rshlnapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.R
import com.example.rshlnapp.Utils
import com.example.rshlnapp.adapters.AddressAdapter
import com.example.rshlnapp.adapters.IAddressAdapter
import com.example.rshlnapp.daos.UserDao
import com.example.rshlnapp.databinding.FragmentProfileBinding
import com.example.rshlnapp.models.Address
import com.example.rshlnapp.models.User
import com.example.rshlnapp.ui.address.AddressFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment(), IAddressAdapter {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var adapter: AddressAdapter
    private lateinit var userDao: UserDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)

        binding.addNewAddressButtonProfile.setOnClickListener {
            openAddressFragment()
        }

        userDao = UserDao()

        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        GlobalScope.launch {
            val currentUser =
                userDao.getUserById(Utils.currentUserId).await().toObject(User::class.java)!!
            val addresses = currentUser.addresses
            withContext(Dispatchers.Main) {
                adapter = AddressAdapter(addresses, this@ProfileFragment)
                binding.addressRecyclerView.adapter = adapter
                binding.usernameInProfile.text = currentUser.username
                binding.mobileNumberInProfile.text = currentUser.mobileNumber
            }
        }
    }

    private fun openAddressFragment() {
        val addressFragment = AddressFragment()
        val currentFragment = this
        requireActivity().supportFragmentManager.beginTransaction().add(
            R.id.nav_host_fragment_content_main,
            addressFragment,
            getString(R.string.title_address_fragment)
        ).hide(currentFragment).commit()
        (activity as MainActivity).setDrawerLocked(true)
    }

    override fun onEditClicked(address: Address) {
        TODO("Not yet implemented")
    }

    override fun onDeleteClicked(address: Address) {
        TODO("Not yet implemented")
    }
}