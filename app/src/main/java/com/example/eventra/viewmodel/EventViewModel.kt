package com.example.eventra.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventra.data.model.Event
import com.example.eventra.data.repository.EventRepository
import com.google.firebase.firestore.toObjects

class EventViewModel : ViewModel() {
    private val repository = EventRepository()

    private val _popularEvents = MutableLiveData<List<Event>>()
    val popularEvents: LiveData<List<Event>> = _popularEvents

    private val _newestEvents = MutableLiveData<List<Event>>()
    val newestEvents: LiveData<List<Event>> = _newestEvents

    private val _searchResult = MutableLiveData<List<Event>>()
    val searchResult: LiveData<List<Event>> = _searchResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchPopularEvents() {
        _isLoading.value = true
        // Gunakan Dummy Data jika Firebase belum siap
        val dummyList = listOf(
            Event("1", "Festival Musik Jakarta", "Gelora Bung Karno", "20 Mei 2026", "19:00", 150000, "Seru abis!", "Music", "", "Eventra", 100),
            Event("2", "Tech Conference 2026", "ICE BSD", "15 Juni 2026", "09:00", 50000, "Belajar IT!", "Tech", "", "Eventra", 50)
        )
        _popularEvents.value = dummyList
        
        repository.getPopularEvents().addSnapshotListener { value, error ->
            _isLoading.value = false
            if (error != null) {
                Log.e("EventViewModel", "Error fetchPopular: ${error.message}")
                return@addSnapshotListener
            }
            if (value != null && !value.isEmpty) {
                _popularEvents.value = value.toObjects(Event::class.java)
            }
        }
    }

    fun fetchNewestEvents() {
        _isLoading.value = true
        // Gunakan Dummy Data jika Firebase belum siap
        val dummyList = listOf(
            Event("3", "Pameran Seni Budaya", "Kota Tua", "10 Juli 2026", "10:00", 25000, "Seni klasik", "Art", "", "Eventra", 200)
        )
        _newestEvents.value = dummyList

        repository.getNewestEvents().addSnapshotListener { value, error ->
            _isLoading.value = false
            if (error != null) {
                Log.e("EventViewModel", "Error fetchNewest: ${error.message}")
                return@addSnapshotListener
            }
            if (value != null && !value.isEmpty) {
                _newestEvents.value = value.toObjects(Event::class.java)
            }
        }
    }

    fun searchEvents(query: String) {
        _isLoading.value = true
        repository.searchEvents(query).addSnapshotListener { value, error ->
            _isLoading.value = false
            if (error == null && value != null) {
                _searchResult.value = value.toObjects(Event::class.java)
            }
        }
    }
}
