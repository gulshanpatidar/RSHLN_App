package com.example.rshlnapp.daos

import com.example.rshlnapp.Utils
import com.example.rshlnapp.models.Order
import com.example.rshlnapp.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderDao {
    //get the instance of the database
    private val db = FirebaseFirestore.getInstance()
    //get the collection
    val ordersCollection = db.collection("orders")

    fun placeOrder(order: Order){
        //do this in background thread
        GlobalScope.launch {
            val documentId = ordersCollection.add(order).await().id
            val userDao = UserDao()
            val user = userDao.getUserById(Utils.currentUserId).await().toObject(User::class.java)!!
            order.orderId = documentId
            user.orders.add(documentId)
            ordersCollection.document(documentId).set(order)
        }
    }

    fun getOrderById(orderId: String): Task<DocumentSnapshot>{
        return ordersCollection.document(orderId).get()
    }
}