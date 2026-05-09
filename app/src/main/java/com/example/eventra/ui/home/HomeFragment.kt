package com.example.eventra.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventra.databinding.FragmentHomeBinding
import com.example.eventra.viewmodel.EventViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EventViewModel
    
    private lateinit var popularAdapter: PopularEventAdapter
    private lateinit var newestAdapter: NewestEventAdapter

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
        
        setupRecyclerViews()
        observeViewModel()

        viewModel.fetchPopularEvents()
        viewModel.fetchNewestEvents()
    }

    private fun setupRecyclerViews() {
        popularAdapter = PopularEventAdapter(listOf())
        binding.rvPopularEvents.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = popularAdapter
        }

        newestAdapter = NewestEventAdapter(listOf())
        binding.rvNewestEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newestAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                binding.shimmerNewest.stopShimmer()
                binding.shimmerNewest.visibility = View.GONE
                binding.rvNewestEvents.visibility = View.VISIBLE
                binding.rvPopularEvents.visibility = View.VISIBLE
            } else {
                binding.shimmerNewest.startShimmer()
                binding.shimmerNewest.visibility = View.VISIBLE
            }
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
