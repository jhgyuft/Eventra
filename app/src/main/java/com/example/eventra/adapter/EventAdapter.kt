package com.example.eventra.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventra.data.model.Event
import com.example.eventra.databinding.ItemEventHorizontalBinding
import com.example.eventra.ui.detail.DetailEventActivity
import java.text.NumberFormat
import java.util.Locale

class EventAdapter(private var events: List<Event>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(val binding: ItemEventHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        with(holder.binding) {
            tvEventName.text = event.namaEvent
            tvEventDate.text = event.tanggal
            
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            tvEventPrice.text = formatRupiah.format(event.harga)

            Glide.with(root.context)
                .load(event.poster)
                .into(ivEventPoster)

            root.setOnClickListener {
                val intent = Intent(root.context, DetailEventActivity::class.java)
                intent.putExtra("EVENT_DATA", event)
                root.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = events.size

    fun updateData(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
}
