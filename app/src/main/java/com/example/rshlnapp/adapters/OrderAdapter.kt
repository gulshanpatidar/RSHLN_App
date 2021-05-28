package com.example.rshlnapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.rshlnapp.R
import com.example.rshlnapp.models.Order
import com.example.rshlnapp.ui.OrderStatus

class OrderAdapter(private val clickListener: IOrderAdapter,val orders: List<Order>): RecyclerView.Adapter<OrderAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
        holder.itemOrder.setOnClickListener {
            clickListener.onOrderClicked(order)
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemOrder: CardView = itemView.findViewById(R.id.item_order_view)
        val productsName: TextView = itemView.findViewById(R.id.products_name_item_order)
        val orderStatus: TextView = itemView.findViewById(R.id.order_status_item_order)

        fun bind(order: Order){
            val cartItems = order.cart.items
            var name = cartItems[0].product.productName
            for(i in 1..(cartItems.size-1)){
                name += ", " + cartItems[i].product.productName
            }
            productsName.text = name
            val status = order.orderStatus
            var statusString = ""
            when(status){
                OrderStatus.PLACED ->{
                    statusString = "Order Placed"
                }
                OrderStatus.APPROVED->{
                    statusString = "Order Approved"
                }
                OrderStatus.CANCELLED->{
                    statusString = "Order Cancelled"
                }
                OrderStatus.DELIVERED->{
                    statusString = "Order Delivered"
                }
                OrderStatus.PACKED->{
                    statusString = "Order Packed"
                }
                OrderStatus.REJECTED->{
                    statusString = "Order Rejected"
                }
                OrderStatus.RETURNED->{
                    statusString = "Order Returned"
                }
                OrderStatus.SHIPPED->{
                    statusString = "Order Shipped"
                }
            }
            orderStatus.text = statusString
        }
    }
}

interface IOrderAdapter{
    fun onOrderClicked(order: Order)
}