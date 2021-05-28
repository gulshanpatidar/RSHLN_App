package com.example.rshlnapp

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.rshlnapp.databinding.ActivityMainBinding
import com.example.rshlnapp.ui.cart.CartFragment
import com.example.rshlnapp.ui.home.HomeFragment
import com.example.rshlnapp.ui.orders.OrdersFragment
import com.example.rshlnapp.ui.profile.ProfileFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), DrawerLocker {

    lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    //create variables for the fragments
    val homeFragment = HomeFragment()
    val ordersFragment = OrdersFragment()
    val profileFragment = ProfileFragment()
    val fragmentManager = supportFragmentManager
    var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.nav_home, R.id.nav_orders, R.id.nav_profile
//            ), drawerLayout
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
        fragmentManager.beginTransaction().apply {
            add(
                R.id.nav_host_fragment_content_main,
                profileFragment,
                getString(R.string.menu_profile)
            ).hide(profileFragment)
            add(
                R.id.nav_host_fragment_content_main,
                ordersFragment,
                getString(R.string.menu_orders)
            ).hide(ordersFragment)
            add(R.id.nav_host_fragment_content_main, homeFragment, getString(R.string.menu_home))
        }.commit()

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment)
                        .commit()
                    activeFragment = homeFragment
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_profile -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(profileFragment)
                        .commit()
                    activeFragment = profileFragment
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_orders -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(ordersFragment)
                        .commit()
                    activeFragment = ordersFragment
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //here create click listener for the cart icon
        return when (item.itemId) {
            android.R.id.home -> {
                false
            }
            R.id.action_cart -> {
//                Toast.makeText(this,"Ruko zara sabr karo",Toast.LENGTH_LONG).show()
                val cartFragment = CartFragment()
                fragmentManager.beginTransaction().add(
                    R.id.nav_host_fragment_content_main,
                    cartFragment,
                    getString(R.string.title_cart_fragment)
                ).hide(activeFragment).commit()
                setDrawerLocked(true)
                true
            }
            else -> false
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun setDrawerLocked(shouldLock: Boolean) {
        if (shouldLock) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }
}

interface DrawerLocker {
    fun setDrawerLocked(shouldLock: Boolean)
}