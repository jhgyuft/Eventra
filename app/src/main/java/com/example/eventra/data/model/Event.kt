package com.example.eventra.data.model

import java.io.Serializable

data class Event(
    val eventId: String = "",
    val namaEvent: String = "",
    val lokasi: String = "",
    val tanggal: String = "",
    val jam: String = "",
    val harga: Long = 0,
    val deskripsi: String = "",
    val kategori: String = "",
    val poster: String = "",
    val organizer: String = "",
    val seat: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) : Serializable
