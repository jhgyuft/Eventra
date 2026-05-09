package com.example.eventra.ui.booking

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventra.data.model.Event
import com.example.eventra.data.model.Transaction
import com.example.eventra.databinding.ActivityBookingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import java.text.NumberFormat
import java.util.Locale

class BookingActivity : AppCompatActivity(), TransactionFinishedCallback {

    private lateinit var binding: ActivityBookingBinding
    private var event: Event? = null
    private var quantity = 1
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        event = intent.getSerializableExtra("EVENT_DATA") as? Event

        setupUI()
        setupListeners()
        setupMidtrans()
    }

    private fun setupMidtrans() {
        // This is usually initialized in Application class, but we can ensure it's ready here if needed
        // Or configure specific UI theme
    }

    private fun setupUI() {
        event?.let {
            binding.tvBookingEventName.text = it.namaEvent
            binding.tvBookingDate.text = it.tanggal
            Glide.with(this).load(it.poster).into(binding.ivBookingPoster)
            updateTotalPrice()
        }
    }

    private fun setupListeners() {
        binding.btnPlus.setOnClickListener {
            quantity++
            binding.tvQty.text = quantity.toString()
            updateTotalPrice()
        }

        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQty.text = quantity.toString()
                updateTotalPrice()
            }
        }

        binding.btnPayNow.setOnClickListener {
            startPayment()
        }
    }

    private fun updateTotalPrice() {
        event?.let {
            val total = it.harga * quantity
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            binding.tvTotalBookingPrice.text = formatRupiah.format(total)
        }
    }

    private fun startPayment() {
        val eventData = event ?: return
        val user = auth.currentUser ?: return
        
        val transactionId = "TRX-" + System.currentTimeMillis()
        val totalAmount = (eventData.harga * quantity).toDouble()

        val transactionRequest = TransactionRequest(transactionId, totalAmount)
        
        val itemDetails = ItemDetails(eventData.eventId, eventData.harga.toDouble(), quantity, eventData.namaEvent)
        val itemDetailsList = ArrayList<ItemDetails>()
        itemDetailsList.add(itemDetails)
        transactionRequest.itemDetails = itemDetailsList

        val customerDetails = CustomerDetails()
        customerDetails.firstName = user.displayName ?: "User"
        customerDetails.email = user.email
        transactionRequest.customerDetails = customerDetails

        MidtransSDK.getInstance().transactionRequest = transactionRequest
        MidtransSDK.getInstance().startPaymentUiFlow(this)
    }

    override fun onTransactionFinished(result: TransactionResult) {
        if (result.response != null) {
            when (result.status) {
                TransactionResult.STATUS_SUCCESS -> {
                    saveTransactionToFirestore(result.response.transactionId, "Paid")
                }
                TransactionResult.STATUS_PENDING -> {
                    saveTransactionToFirestore(result.response.transactionId, "Pending")
                }
                TransactionResult.STATUS_FAILED -> {
                    Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (result.isTransactionCanceled) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_SHORT).show()
        } else {
            if (result.status.equals(TransactionResult.STATUS_INVALID, true)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Transaction Finished with failure", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTransactionToFirestore(midtransId: String, status: String) {
        val uid = auth.currentUser?.uid ?: return
        val eventData = event ?: return
        
        val transactionId = db.collection("transactions").document().id
        val transaction = Transaction(
            transactionId = transactionId,
            uidUser = uid,
            idEvent = eventData.eventId,
            namaEvent = eventData.namaEvent,
            posterEvent = eventData.poster,
            tanggalEvent = eventData.tanggal,
            jumlahTiket = quantity,
            totalHarga = eventData.harga * quantity,
            statusPembayaran = status,
            metodePembayaran = "Midtrans",
            createdAt = System.currentTimeMillis()
        )

        db.collection("transactions").document(transactionId).set(transaction)
            .addOnSuccessListener {
                Toast.makeText(this, "Booking Successful with status: $status", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save transaction: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun processBooking() {
        // Legacy method, replaced by startPayment
    }
}
