package com.example.rshlnapp.ui.summary

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rshlnapp.R
import com.example.rshlnapp.databinding.SummaryFragmentBinding

class SummaryFragment : Fragment() {

    private lateinit var viewModel: SummaryViewModel
    private lateinit var binding: SummaryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SummaryViewModel::class.java)
        binding = SummaryFragmentBinding.inflate(inflater)

        return binding.root
    }

}