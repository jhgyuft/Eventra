package com.example.eventra.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventra.databinding.FragmentExploreBinding
import com.example.eventra.viewmodel.EventViewModel

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EventViewModel
    private lateinit var eventAdapter: NewestEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[EventViewModel::class.java]

        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        eventAdapter = NewestEventAdapter(listOf())
        binding.rvExplore.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExplore.adapter = eventAdapter
    }

    private fun setupSearch() {
        binding.etSearchExplore.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchEvents(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarExplore.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.searchResult.observe(viewLifecycleOwner) { events ->
            if (events.isNullOrEmpty()) {
                binding.emptyStateExplore.visibility = View.VISIBLE
                binding.rvExplore.visibility = View.GONE
                eventAdapter.updateData(listOf())
            } else {
                binding.emptyStateExplore.visibility = View.GONE
                binding.rvExplore.visibility = View.VISIBLE
                eventAdapter.updateData(events)
            }
        }
        
        viewModel.fetchNewestEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
