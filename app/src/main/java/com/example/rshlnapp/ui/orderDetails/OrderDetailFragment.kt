package com.example.rshlnapp.ui.orderDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.R
import com.example.rshlnapp.databinding.OrderDetailFragmentBinding

class OrderDetailFragment(val previousFragment: Fragment) : Fragment() {

    private lateinit var viewModel: OrderDetailViewModel
    private lateinit var binding: OrderDetailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(OrderDetailViewModel::class.java)
        binding = OrderDetailFragmentBinding.inflate(inflater)

        binding.textOrderDetails.text = "Order placed successfully"
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentFragment = this@OrderDetailFragment
                    requireActivity().supportFragmentManager.beginTransaction()
                        .remove(currentFragment).show(previousFragment).commit()
                    (activity as MainActivity).supportActionBar?.title = "Choose Payment Option"
                    //TODO: manage the backstack from here.
                }
            })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_cart).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }

}