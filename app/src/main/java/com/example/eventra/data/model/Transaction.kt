package com.example.eventra.data.model

data class Transaction(
    val transactionId: String = "",
    val uidUser: String = "",
    val idEvent: String = "",
    val namaEvent: String = "",
    val posterEvent: String = "",
    val tanggalEvent: String = "",
    val jumlahTiket: Int = 0,
    val totalHarga: Long = 0,
    val statusPembayaran: String = "Pending", // Pending, Paid, Expired
    val metodePembayaran: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
