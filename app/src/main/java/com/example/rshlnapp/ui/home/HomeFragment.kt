package com.example.rshlnapp.ui.home

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.R
import com.example.rshlnapp.adapters.IProductAdapter
import com.example.rshlnapp.adapters.ProductAdapter
import com.example.rshlnapp.databinding.FragmentHomeBinding
import com.example.rshlnapp.ui.detail.DetailFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment(), IProductAdapter {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater)

        binding.viewModel = homeViewModel
        adapter = ProductAdapter(this)
        binding.productsRecyclerView.adapter = adapter

        homeViewModel.products.observe(viewLifecycleOwner,{
            adapter.notifyDataSetChanged()
            binding.productsRecyclerView.smoothScrollToPosition(it.size)
        })

        return binding.root
    }

    override fun onProductClicked(productId: String) {
        //navigate to the product detail fragment
        val currentFragment = this
        val productDetailFragment = DetailFragment(productId,"HomeFragment")
        requireActivity().supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_content_main,productDetailFragment,getString(R.string.title_detail_fragment)).hide(currentFragment).commit()
        (activity as MainActivity).setDrawerLocked(true)
    }
}