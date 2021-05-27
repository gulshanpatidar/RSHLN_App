package com.example.rshlnapp.ui.payment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.databinding.PaymentFragmentBinding
import com.example.rshlnapp.models.Address
import com.example.rshlnapp.models.Cart
import com.example.rshlnapp.models.User
import com.example.rshlnapp.ui.choose_address.ChooseAddressFragment

class PaymentFragment() : Fragment() {

    private lateinit var viewModel: PaymentViewModel
    private lateinit var binding: PaymentFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PaymentFragmentBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)

        (activity as MainActivity).supportActionBar?.title = "Choose Payment Option"

        return binding.root
    }

}