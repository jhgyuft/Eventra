package com.example.eventra.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventra.R
import com.example.eventra.databinding.FragmentHomeBinding
import com.example.eventra.ui.cart.CartActivity
import com.example.eventra.utils.addClickScale
import com.example.eventra.utils.fadeIn
import com.example.eventra.utils.slideUp
import com.example.eventra.viewmodel.EventViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EventViewModel
    
    private lateinit var popularAdapter: PopularEventAdapter
    private lateinit var newestAdapter: NewestEventAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[EventViewModel::class.java]
        
        setupUI()
        setupRecyclerViews()
        setupSearch()
        observeViewModel()

        viewModel.fetchPopularEvents()
        viewModel.fetchNewestEvents()
    }

    private fun setupUI() {
        binding.ivUserProfile.addClickScale()
        binding.btnCart.addClickScale()
        binding.btnNotification.addClickScale()
        binding.searchCard.addClickScale(0.98f)

        binding.tvGreeting.fadeIn(600)
        binding.searchCard.slideUp(700, 100)

        // Aktifkan Fitur Keranjang
        binding.btnCart.setOnClickListener {
            val intent = Intent(requireContext(), CartActivity::class.java)
            startActivity(intent)
        }

        // FIX: Gunakan CustomDialogTheme agar tulisan putih terlihat di latar gelap
        binding.btnNotification.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val name = user?.displayName ?: "Explorer"
            
            MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)
                .setTitle("🔔 Eventra Updates")
                .setMessage("Hi $name, welcome back!\n\nSpecial for you: Get 20% OFF for VIP Tickets at Coldplay Concert! Check your cart now.")
                .setPositiveButton("Awesome") { d, _ -> d.dismiss() }
                .show()
        }
    }

    private fun setupRecyclerViews() {
        val categories = listOf(
            Category("All", R.drawable.ic_home),
            Category("Music", R.drawable.ic_home),
            Category("Art", R.drawable.ic_home),
            Category("Tech", R.drawable.ic_home),
            Category("Sport", R.drawable.ic_home)
        )
        
        categoryAdapter = CategoryAdapter(categories) { category ->
            viewModel.filterByCategory(category.name)
            binding.rvNewestEvents.slideUp(400)
        }
        
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        popularAdapter = PopularEventAdapter(listOf())
        binding.rvPopularEvents.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = popularAdapter
        }

        newestAdapter = NewestEventAdapter(listOf())
        binding.rvNewestEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newestAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupSearch() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.searchEvents(text.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Shimmer handling
        }

        viewModel.popularEvents.observe(viewLifecycleOwner) { events ->
            popularAdapter.updateData(events)
        }

        viewModel.newestEvents.observe(viewLifecycleOwner) { events ->
            newestAdapter.updateData(events)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
