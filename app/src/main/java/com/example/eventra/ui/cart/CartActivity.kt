package com.example.eventra.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventra.data.model.CartItem
import com.example.eventra.databinding.ActivityCartBinding
import com.example.eventra.ui.booking.BookingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var cartList = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadCartData()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnCheckout.setOnClickListener {
            if (cartList.isNotEmpty()) {
                val intent = Intent(this, BookingActivity::class.java)
                intent.putExtra("CART_ITEMS", ArrayList(cartList))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        // FIX: Constructor CartAdapter disesuaikan (hanya menerima listener)
        adapter = CartAdapter { item -> deleteCartItem(item) }
        binding.rvCartItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartItems.adapter = adapter
    }

    private fun loadCartData() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).collection("cart")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this, "Error loading cart: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (value != null) {
                    cartList = value.toObjects(CartItem::class.java)
                    adapter.updateData(cartList)
                    updateTotalPrice()
                    
                    // Tampilkan pesan jika kosong
                    if (cartList.isEmpty()) {
                        Toast.makeText(this, "Your cart is currently empty", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun updateTotalPrice() {
        val total = cartList.sumOf { it.totalItemPrice }
        val priceFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
        binding.tvTotalCartPrice.text = priceFormat.format(total)
    }

    private fun deleteCartItem(item: CartItem) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).collection("cart").document(item.cartId).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show()
            }
    }
}
