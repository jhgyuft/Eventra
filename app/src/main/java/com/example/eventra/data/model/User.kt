package com.example.eventra.data.model

data class User(
    val uid: String = "",
    val nama: String = "",
    val email: String = "",
    val role: String = "user",
    val profileImage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
