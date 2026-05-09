package com.example.eventra.ui.booking

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventra.data.model.CartItem
import com.example.eventra.data.model.Transaction
import com.example.eventra.databinding.ActivityBookingBinding
import com.example.eventra.ui.cart.CartAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private var cartItems = ArrayList<CartItem>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menerima list item dari Intent (Bisa banyak tiket)
        @Suppress("UNCHECKED_CAST")
        cartItems = intent.getSerializableExtra("CART_ITEMS") as? ArrayList<CartItem> ?: arrayListOf()

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "No items to checkout", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        calculateTotal()
        
        binding.btnBack.setOnClickListener { finish() }
        binding.btnConfirmPay.setOnClickListener { processBatchPayment() }
    }

    private fun setupRecyclerView() {
        // Menggunakan CartAdapter untuk menampilkan ringkasan di checkout
        val adapter = CartAdapter(cartItems) { /* Disable delete in checkout */ }
        binding.rvCheckoutItems.layoutManager = LinearLayoutManager(this)
        binding.rvCheckoutItems.adapter = adapter
    }

    private fun calculateTotal() {
        val total = cartItems.sumOf { it.totalItemPrice }
        val priceFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
        binding.tvTotalCheckoutPrice.text = priceFormat.format(total)
    }

    private fun processBatchPayment() {
        val user = auth.currentUser ?: return
        binding.btnConfirmPay.isEnabled = false
        binding.btnConfirmPay.text = "Processing..."

        val batch = db.batch()
        
        cartItems.forEach { item ->
            val transactionId = "TRX-${System.currentTimeMillis()}-${item.cartId}"
            val trxRef = db.collection("transactions").document(transactionId)
            
            val transaction = Transaction(
                transactionId = transactionId,
                uidUser = user.uid,
                idEvent = item.eventId,
                namaEvent = "${item.eventName} (${item.category})",
                posterEvent = item.eventPoster,
                tanggalEvent = item.eventDate,
                jumlahTiket = item.quantity,
                totalHarga = item.totalItemPrice,
                statusPembayaran = "Paid",
                metodePembayaran = "Balance/System",
                createdAt = System.currentTimeMillis()
            )
            batch.set(trxRef, transaction)
            
            // Hapus dari keranjang setelah sukses
            val cartRef = db.collection("users").document(user.uid).collection("cart").document(item.cartId)
            batch.delete(cartRef)
        }

        batch.commit().addOnSuccessListener {
            Toast.makeText(this, "Payment Successful! Tickets added to your list.", Toast.LENGTH_LONG).show()
            finish()
        }.addOnFailureListener {
            binding.btnConfirmPay.isEnabled = true
            binding.btnConfirmPay.text = "Confirm and Pay"
            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
