package com.example.eventra.ui.ticket

import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventra.R
import com.example.eventra.data.model.Transaction
import com.example.eventra.databinding.ItemTicketBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class TicketAdapter(private var tickets: List<Transaction>) :
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(private val binding: ItemTicketBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(transaction: Transaction) {
            binding.tvEventName.text = transaction.namaEvent
            binding.tvEventDate.text = transaction.tanggalEvent
            binding.tvTicketCount.text = "${transaction.jumlahTiket} Ticket(s)"
            binding.tvStatus.text = transaction.statusPembayaran
            
            val statusColor = when (transaction.statusPembayaran.lowercase()) {
                "paid" -> R.color.success
                "pending" -> R.color.warning
                else -> R.color.error
            }
            binding.tvStatus.setBackgroundResource(
                if (transaction.statusPembayaran.lowercase() == "paid") R.drawable.bg_status_paid 
                else if (transaction.statusPembayaran.lowercase() == "pending") R.drawable.bg_status_pending
                else R.drawable.bg_status_expired
            )

            Glide.with(binding.ivEventPoster.context)
                .load(transaction.posterEvent)
                .into(binding.ivEventPoster)

            if (transaction.statusPembayaran.lowercase() == "paid") {
                binding.ivQRCode.visibility = View.VISIBLE
                binding.tvTransactionId.visibility = View.VISIBLE
                binding.tvTransactionId.text = "ID: ${transaction.transactionId}"
                try {
                    val bitmap = generateQRCode(transaction.transactionId)
                    binding.ivQRCode.setImageBitmap(bitmap)
                } catch (e: WriterException) {
                    e.printStackTrace()
                }
            } else {
                binding.ivQRCode.visibility = View.GONE
                binding.tvTransactionId.visibility = View.GONE
            }
        }

        private fun generateQRCode(text: String): Bitmap {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            return bitmap
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TicketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(tickets[position])
    }

    override fun getItemCount(): Int = tickets.size

    fun updateData(newTickets: List<Transaction>) {
        tickets = newTickets
        notifyDataSetChanged()
    }
}
