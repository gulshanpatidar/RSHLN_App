package com.example.rshlnapp

import com.example.rshlnapp.daos.UserDao
import com.example.rshlnapp.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Utils {

    companion object{
        val currentUserId = Firebase.auth.currentUser!!.uid
    }
}