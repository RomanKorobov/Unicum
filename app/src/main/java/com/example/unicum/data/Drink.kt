package com.example.unicum.data

import android.graphics.drawable.Drawable

data class Drink(
    val id: Long,
    var name: String,
    var price: String,
    var free: Boolean,
    var img: Int,
    val volume: String
)
