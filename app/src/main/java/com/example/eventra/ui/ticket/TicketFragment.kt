package com.example.eventra.ui.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventra.data.model.Transaction
import com.example.eventra.databinding.FragmentTicketBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TicketFragment : Fragment() {

    private var _binding: FragmentTicketBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: TicketAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        loadTickets()
    }

    private fun setupRecyclerView() {
        adapter = TicketAdapter(emptyList())
        binding.rvTickets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTickets.adapter = adapter
    }

    private fun loadTickets() {
        val uid = auth.currentUser?.uid ?: return
        
        db.collection("transactions")
            .whereEqualTo("uidUser", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                
                val tickets = value?.toObjects(Transaction::class.java) ?: emptyList()
                if (tickets.isEmpty()) {
                    binding.emptyStateTicket.visibility = View.VISIBLE
                    binding.rvTickets.visibility = View.GONE
                } else {
                    binding.emptyStateTicket.visibility = View.GONE
                    binding.rvTickets.visibility = View.VISIBLE
                    adapter.updateData(tickets)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
