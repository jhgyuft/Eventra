package com.example.eventra.data.model

import java.io.Serializable

data class CartItem(
    val cartId: String = "",
    val eventId: String = "",
    val eventName: String = "",
    val eventPoster: String = "",
    val eventDate: String = "",
    val category: String = "",
    val quantity: Int = 0,
    val price: Long = 0,
    val totalItemPrice: Long = 0
) : Serializable
