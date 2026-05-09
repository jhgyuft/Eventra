package com.example.eventra.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.eventra.R
import com.example.eventra.data.model.Event
import com.example.eventra.databinding.ItemEventHorizontalBinding
import com.example.eventra.ui.detail.DetailEventActivity
import com.example.eventra.utils.addClickScale
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
            root.addClickScale(0.97f)
            
            tvEventName.text = event.namaEvent
            tvEventDate.text = "${event.tanggal} • ${event.jam}"
            
            val priceFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                maximumFractionDigits = 0
            }
            tvEventPrice.text = if (event.harga == 0L) "Free Entrance" else priceFormat.format(event.harga)

            Glide.with(root.context)
                .load(event.poster)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .placeholder(R.drawable.bg_glass_card)
                .into(ivEventPoster)

            // Memastikan seluruh card DAN tombol Book bisa diklik
            val navigateToDetail = {
                val intent = Intent(root.context, DetailEventActivity::class.java)
                intent.putExtra("EVENT_DATA", event)
                root.context.startActivity(intent)
            }

            root.setOnClickListener { navigateToDetail() }
            btnBook.setOnClickListener { navigateToDetail() } // Sekarang tombol Book berfungsi
        }
    }

    override fun getItemCount() = events.size

    fun updateData(newEvents: List<Event>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = events.size
            override fun getNewListSize() = newEvents.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return events[oldItemPosition].eventId == newEvents[newItemPosition].eventId
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return events[oldItemPosition] == newEvents[newItemPosition]
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        events = newEvents
        diffResult.dispatchUpdatesTo(this)
    }
}
