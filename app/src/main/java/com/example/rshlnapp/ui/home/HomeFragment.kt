package com.example.rshlnapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rshlnapp.MainActivity
import com.example.rshlnapp.R
import com.example.rshlnapp.adapters.IProductAdapter
import com.example.rshlnapp.adapters.ProductAdapter
import com.example.rshlnapp.databinding.FragmentHomeBinding
import com.example.rshlnapp.ui.detail.DetailFragment

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

        homeViewModel.products.observe(viewLifecycleOwner, {
            adapter.notifyDataSetChanged()
            binding.productsRecyclerView.smoothScrollToPosition(it.size)
        })

        homeViewModel.status.observe(viewLifecycleOwner,{
            if (it==ProductStatus.DONE){
                binding.homeFragmentProgressBar.visibility = View.GONE
            }
        })

        return binding.root
    }

    override fun onProductClicked(productId: String) {
        //navigate to the product detail fragment
        val currentFragment = this
        val productDetailFragment = DetailFragment(productId, "HomeFragment")
        requireActivity().supportFragmentManager.beginTransaction().add(
            R.id.nav_host_fragment_content_main,
            productDetailFragment,
            getString(R.string.title_detail_fragment)
        ).hide(currentFragment).commit()
        (activity as MainActivity).setDrawerLocked(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            (activity as MainActivity).drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}