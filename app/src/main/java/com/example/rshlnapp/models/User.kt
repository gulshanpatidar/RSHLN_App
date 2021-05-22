package com.example.rshlnapp.models

data class User(val userId: String="", val username: String="", val mobileNumber: String="",
                var cart: Cart=Cart(), val orders: ArrayList<String> = ArrayList(), val addresses: ArrayList<Address> = ArrayList())