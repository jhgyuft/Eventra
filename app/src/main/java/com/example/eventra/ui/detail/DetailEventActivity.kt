package com.example.eventra.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventra.R
import com.example.eventra.data.model.CartItem
import com.example.eventra.data.model.Event
import com.example.eventra.databinding.ActivityDetailEventBinding
import com.example.eventra.databinding.LayoutBottomSheetCategoryBinding
import com.example.eventra.ui.booking.BookingActivity
import com.example.eventra.utils.addClickScale
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private var event: Event? = null
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        event = intent.getSerializableExtra("EVENT_DATA") as? Event
        
        setupUI()
        setupActions()
    }

    private fun setupUI() {
        event?.let {
            binding.tvDetailName.text = it.namaEvent
            binding.tvDetailLocation.text = it.lokasi
            binding.tvDetailDescription.text = it.deskripsi
            Glide.with(this).load(it.poster).placeholder(R.drawable.bg_glass_card).into(binding.ivDetailPoster)
        }
    }

    private fun setupActions() {
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnAddToCart.addClickScale()
        binding.btnBuyTicket.addClickScale()

        binding.btnAddToCart.setOnClickListener { showCategorySheet(isCheckout = false) }
        binding.btnBuyTicket.setOnClickListener { showCategorySheet(isCheckout = true) }
    }

    private fun showCategorySheet(isCheckout: Boolean) {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val sheetBinding = LayoutBottomSheetCategoryBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        var qty = 1
        var selectedCat = "Regular"
        val basePrice = event?.harga ?: 0L
        val priceFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }

        fun updatePriceDisplay() {
            val multiplier = when (selectedCat) {
                "VIP" -> 1.5
                "VVIP" -> 2.5
                else -> 1.0
            }
            val currentUnitPrice = (basePrice * multiplier).toLong()
            sheetBinding.tvSelectedPrice.text = priceFormat.format(currentUnitPrice)
        }

        updatePriceDisplay()

        sheetBinding.btnPlus.setOnClickListener { 
            qty++
            sheetBinding.tvQty.text = qty.toString()
        }
        
        sheetBinding.btnMinus.setOnClickListener { 
            if (qty > 1) { qty--; sheetBinding.tvQty.text = qty.toString() }
        }

        sheetBinding.cgCategorySelection.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds[0])
                selectedCat = chip.text.toString()
                updatePriceDisplay()
            }
        }

        sheetBinding.btnConfirmAdd.text = if (isCheckout) "Buy Now" else "Add to Cart"
        
        sheetBinding.btnConfirmAdd.setOnClickListener {
            val multiplier = when (selectedCat) {
                "VIP" -> 1.5
                "VVIP" -> 2.5
                else -> 1.0
            }
            val finalPrice = (basePrice * multiplier).toLong()

            val cartItem = CartItem(
                cartId = System.currentTimeMillis().toString(),
                eventId = event?.eventId ?: "",
                eventName = event?.namaEvent ?: "",
                eventPoster = event?.poster ?: "",
                eventDate = event?.tanggal ?: "",
                category = selectedCat,
                quantity = qty,
                price = finalPrice,
                totalItemPrice = finalPrice * qty
            )

            if (isCheckout) {
                val intent = Intent(this, BookingActivity::class.java)
                intent.putExtra("CART_ITEMS", arrayListOf(cartItem))
                startActivity(intent)
            } else {
                addToFirestoreCart(cartItem)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addToFirestoreCart(item: CartItem) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).collection("cart").document(item.cartId).set(item)
            .addOnSuccessListener {
                Toast.makeText(this, "Event added to cart!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
