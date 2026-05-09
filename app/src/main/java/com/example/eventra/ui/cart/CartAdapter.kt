package com.example.eventra.ui.cart

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventra.data.model.CartItem
import com.example.eventra.databinding.ItemCartBinding
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private var items: List<CartItem> = listOf(),
    private val onDeleteClick: ((CartItem) -> Unit)? = null
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            // PAKSA WARNA TEKS (Hardcoded Hex)
            tvCartEventName.text = item.eventName
            tvCartEventName.setTextColor(Color.WHITE)
            
            tvCartCategory.text = item.category.uppercase()
            tvCartCategory.setTextColor(Color.parseColor("#FFC107")) // Gold
            
            tvCartQty.text = "x${item.quantity}"
            tvCartQty.setTextColor(Color.WHITE)
            
            val priceFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                maximumFractionDigits = 0
            }
            tvCartItemPrice.text = priceFormat.format(item.totalItemPrice)
            tvCartItemPrice.setTextColor(Color.parseColor("#FFC107"))

            Glide.with(root.context)
                .load(item.eventPoster)
                .centerCrop()
                .into(ivCartEventPoster)

            // Sembunyikan tombol hapus jika di halaman checkout
            btnDeleteCart.visibility = if (onDeleteClick == null) android.view.View.GONE else android.view.View.VISIBLE
            btnDeleteCart.setOnClickListener { onDeleteClick?.invoke(item) }
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<CartItem>) {
        val oldList = items
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = oldList.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(oldPos: Int, newPos: Int) = oldList[oldPos].cartId == newItems[newPos].cartId
            override fun areContentsTheSame(oldPos: Int, newPos: Int) = oldList[oldPos] == newItems[newPos]
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems.toList()
        diffResult.dispatchUpdatesTo(this)
    }
}
