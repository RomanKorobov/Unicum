package com.example.unicum.data

import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DrinkList")
data class Drink(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "price")
    var price: String,
    @ColumnInfo(name = "free")
    var free: Boolean,
    @ColumnInfo(name = "img")
    var img: Int,
    @ColumnInfo(name = "volume")
    val volume: String
)
