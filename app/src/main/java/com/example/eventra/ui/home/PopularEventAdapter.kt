package com.example.eventra.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventra.R
import com.example.eventra.data.model.Event
import com.example.eventra.databinding.ItemEventHorizontalBinding
import com.example.eventra.ui.detail.DetailEventActivity
import java.text.NumberFormat
import java.util.Locale

class PopularEventAdapter(private var events: List<Event>) : RecyclerView.Adapter<PopularEventAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemEventHorizontalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.binding.apply {
            tvEventName.text = event.namaEvent
            tvEventDate.text = "${event.tanggal} • ${event.jam}"
            
            val priceFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            tvEventPrice.text = if (event.harga == 0L) "Free" else priceFormat.format(event.harga)

            Glide.with(root.context)
                .load(event.poster)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(ivEventPoster)

            root.setOnClickListener {
                val intent = Intent(root.context, DetailEventActivity::class.java)
                intent.putExtra("EVENT_DATA", event)
                root.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = events.size

    fun updateData(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
}
