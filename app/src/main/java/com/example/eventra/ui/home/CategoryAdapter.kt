package com.example.eventra.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.eventra.R
import com.example.eventra.databinding.ItemCategoryBinding

data class Category(val name: String, val icon: Int)

class CategoryAdapter(private val categories: List<Category>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedPosition = 0

    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.apply {
            tvCategoryName.text = category.name
            ivCategoryIcon.setImageResource(category.icon)

            val isSelected = selectedPosition == position
            root.setCardBackgroundColor(
                if (isSelected) ContextCompat.getColor(root.context, R.color.primary_gold)
                else ContextCompat.getColor(root.context, R.color.card_dark)
            )
            tvCategoryName.setTextColor(
                if (isSelected) ContextCompat.getColor(root.context, R.color.bg_black)
                else ContextCompat.getColor(root.context, R.color.text_gray)
            )
            ivCategoryIcon.setColorFilter(
                if (isSelected) ContextCompat.getColor(root.context, R.color.bg_black)
                else ContextCompat.getColor(root.context, R.color.primary_gold)
            )

            root.setOnClickListener {
                val oldPos = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(oldPos)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun getItemCount() = categories.size
}
