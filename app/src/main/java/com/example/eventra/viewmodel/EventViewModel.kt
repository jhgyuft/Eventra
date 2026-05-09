package com.example.eventra.viewmodel

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

    private val allEvents = mutableListOf<Event>()

    init {
        // Data Dummy Premium agar aplikasi langsung terlihat hidup
        val dummy = listOf(
            Event("1", "Coldplay: Music of Spheres", "GBK Stadium", "20 May 2026", "19:00", 1500000, "Experience the magic.", "Music", "", "PK Ent", 100),
            Event("2", "Global Tech Summit 2026", "ICE BSD City", "15 Jun 2026", "09:00", 250000, "Tech future is here.", "Tech", "", "TechAsia", 50),
            Event("3", "Art & Soul Exhibition", "National Museum", "10 Jul 2026", "10:00", 50000, "Art for the soul.", "Art", "", "ArtVibe", 200),
            Event("4", "Java Jazz Festival", "JIExpo", "25 May 2026", "16:00", 850000, "Jazz all night.", "Music", "", "JFP", 300),
            Event("5", "Marathon Jakarta 2026", "Monas", "05 May 2026", "05:00", 350000, "Run the city.", "Sport", "", "JktSport", 1000)
        )
        allEvents.addAll(dummy)
        _popularEvents.value = dummy.take(3)
        _newestEvents.value = dummy
        _searchResult.value = dummy
    }

    fun fetchPopularEvents() {
        repository.getPopularEvents().addSnapshotListener { value, error ->
            if (error == null && value != null && !value.isEmpty) {
                _popularEvents.value = value.toObjects(Event::class.java)
            }
        }
    }

    fun fetchNewestEvents() {
        _isLoading.value = true
        repository.getNewestEvents().addSnapshotListener { value, error ->
            _isLoading.value = false
            if (error == null && value != null) {
                val events = value.toObjects(Event::class.java)
                if (events.isNotEmpty()) {
                    allEvents.clear()
                    allEvents.addAll(events)
                    _newestEvents.value = events
                    _searchResult.value = events
                }
            }
        }
    }

    fun searchEvents(query: String) {
        if (query.isEmpty()) {
            _searchResult.value = allEvents
            _newestEvents.value = allEvents
            return
        }
        val filtered = allEvents.filter { 
            it.namaEvent.contains(query, ignoreCase = true) || 
            it.kategori.contains(query, ignoreCase = true) 
        }
        _searchResult.value = filtered
        _newestEvents.value = filtered
    }

    fun filterByCategory(category: String) {
        val filtered = if (category == "All") allEvents 
                      else allEvents.filter { it.kategori.equals(category, ignoreCase = true) }
        _newestEvents.value = filtered
        _searchResult.value = filtered
    }
}
