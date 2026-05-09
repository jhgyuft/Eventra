package com.example.eventra.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.eventra.R
import com.example.eventra.data.model.Event
import com.example.eventra.databinding.ItemEventVerticalBinding
import com.example.eventra.utils.addClickScale
import com.example.eventra.utils.slideUp
import java.text.NumberFormat
import java.util.Locale

class NewestEventAdapter(private var events: List<Event>) : RecyclerView.Adapter<NewestEventAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemEventVerticalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        
        holder.itemView.slideUp(500, (position * 50).toLong())

        holder.binding.apply {
            root.addClickScale(0.98f)
            
            tvEventName.text = event.namaEvent
            tvEventDate.text = "${event.tanggal} • ${event.jam}"
            tvCategory.text = event.kategori
            
            val priceFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                maximumFractionDigits = 0
            }
            tvEventPrice.text = if (event.harga == 0L) "Free" else priceFormat.format(event.harga)

            Glide.with(root.context)
                .load(event.poster)
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .placeholder(R.drawable.bg_glass_card)
                .into(ivEventPoster)

            // Info Only: Tidak membuka detail/booking, hanya menampilkan pesan
            root.setOnClickListener {
                Toast.makeText(root.context, "This event will be available soon!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = events.size

    fun updateData(newEvents: List<Event>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = events.size
            override fun getNewListSize() = newEvents.size
            override fun areItemsTheSame(oldPos: Int, newPos: Int) = events[oldPos].eventId == newEvents[newPos].eventId
            override fun areContentsTheSame(oldPos: Int, newPos: Int) = events[oldPos] == newEvents[newPos]
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        events = newEvents
        diffResult.dispatchUpdatesTo(this)
    }
}
