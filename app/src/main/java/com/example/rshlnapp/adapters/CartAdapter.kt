package com.example.rshlnapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.rshlnapp.databinding.ItemProductCartBinding
import com.example.rshlnapp.models.CartItem
import com.example.rshlnapp.R

class CartAdapter(private val clickListener: ICartAdapter) :
    ListAdapter<CartItem, CartAdapter.ViewHolder>(DiffCallback) {

//    val cart = currentUser.cart
//    val products = cart.products

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemProductCartBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = getItem(position)
        holder.bind(cartItem)
        holder.addButton.setOnClickListener {
            clickListener.onAddClicked(cartItem)
        }
        holder.deleteButton.setOnClickListener {
            clickListener.onDeleteClicked(cartItem)
        }
        holder.item_product.setOnClickListener{
            clickListener.onProductClicked(cartItem.productId)
        }
    }

    class ViewHolder(private var binding: ItemProductCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val item_product: LinearLayout = binding.productItemCart
        val productImage: ImageView = binding.productImageInCart
        val productName: TextView = binding.productNameInCart
        val productPrice: TextView = binding.productPriceInCart
        val availabilityLabel: TextView = binding.availabilityLabelCart
        val deleteButton: ImageButton = binding.deleteProductButtonInCart
        val quantityView: TextView = binding.numberOfProductsInCart
        val addButton: ImageButton = binding.addProductButtonInCart

        fun bind(cartItem: CartItem) {
            productImage.load(cartItem.product.productImage) {
                transformations(RoundedCornersTransformation())
            }
            productName.text = cartItem.product.productName
            val price = cartItem.product.productPrice.toString()
            if (cartItem.product.availability) {
                productPrice.text = "Price - ₹$price"
            } else {
                availabilityLabel.visibility = View.VISIBLE
                productPrice.visibility = View.GONE
                availabilityLabel.text = "Currently Not Available"
            }
            val quantity = cartItem.quantity
            if (quantity>1){
                deleteButton.setImageResource(R.drawable.ic_minus)
            }
            quantityView.text = quantity.toString()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}

interface ICartAdapter {
    fun onProductClicked(productId: String)
    fun onAddClicked(cartItem: CartItem)
    fun onDeleteClicked(cartItem: CartItem)
}