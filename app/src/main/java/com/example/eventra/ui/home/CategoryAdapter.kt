package com.example.eventra.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.eventra.R
import com.example.eventra.databinding.ItemCategoryBinding
import com.example.eventra.utils.addClickScale

data class Category(val name: String, val icon: Int)

/**
 * CategoryAdapter - Refined Premium Version
 * Fokus: Smooth selection transitions, tactile feedback, dan consistent branding.
 */
class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategorySelected: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedPosition = 0

    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        val context = holder.itemView.context
        val isSelected = selectedPosition == position
        
        holder.binding.apply {
            // Tactile feedback: Efek membal saat ditekan
            root.addClickScale(0.92f)
            
            tvCategoryName.text = category.name
            ivCategoryIcon.setImageResource(category.icon)

            // Premium Selection Logic
            if (isSelected) {
                // State: Selected (Glowing Gold)
                iconContainer.setBackgroundResource(R.drawable.bg_category_icon_glow)
                ivCategoryIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary_gold))
                tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.primary_gold))
                tvCategoryName.alpha = 1.0f
                
                // Animation: Pop-up effect untuk kategori yang dipilih
                root.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(250)
                    .setInterpolator(android.view.animation.OvershootInterpolator())
                    .start()
            } else {
                // State: Unselected (Subtle Glassmorphism)
                iconContainer.setBackgroundResource(R.drawable.bg_glass_card)
                ivCategoryIcon.setColorFilter(ContextCompat.getColor(context, R.color.text_dim))
                tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
                tvCategoryName.alpha = 0.6f
                
                // Animation: Return to normal size
                root.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(200)
                    .start()
            }

            root.setOnClickListener {
                if (selectedPosition != position) {
                    val oldPos = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(oldPos)
                    notifyItemChanged(selectedPosition)
                    onCategorySelected(category)
                }
            }
        }
    }

    override fun getItemCount() = categories.size
}
