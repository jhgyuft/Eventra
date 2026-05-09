package com.example.eventra.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventra.data.model.Event
import com.example.eventra.databinding.ActivityDetailEventBinding
import com.example.eventra.ui.booking.BookingActivity
import java.text.NumberFormat
import java.util.Locale

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private var event: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        event = intent.getSerializableExtra("EVENT_DATA") as? Event
        
        setupUI()
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.btnBuyTicket.setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            intent.putExtra("EVENT_DATA", event)
            startActivity(intent)
        }
    }

    private fun setupUI() {
        event?.let {
            binding.tvDetailName.text = it.namaEvent
            binding.tvDetailLocation.text = it.lokasi
            binding.tvDetailDateTime.text = "${it.tanggal} | ${it.jam}"
            binding.tvDetailDescription.text = it.deskripsi
            
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            binding.tvDetailPrice.text = formatRupiah.format(it.harga)

            Glide.with(this)
                .load(it.poster)
                .into(binding.ivDetailPoster)
        }
    }
}
