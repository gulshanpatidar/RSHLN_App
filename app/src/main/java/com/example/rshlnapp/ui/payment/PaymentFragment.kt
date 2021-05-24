package com.example.rshlnapp.ui.payment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rshlnapp.R
import com.example.rshlnapp.databinding.PaymentFragmentBinding

class PaymentFragment : Fragment() {

    private lateinit var viewModel: PaymentViewModel
    private lateinit var binding: PaymentFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PaymentFragmentBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)
        return binding.root
    }

}