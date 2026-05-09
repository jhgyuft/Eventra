package com.example.eventra.data.repository

import com.example.eventra.data.model.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class EventRepository {
    private val db = FirebaseFirestore.getInstance()
    private val eventCollection = db.collection("events")

    fun getPopularEvents() = eventCollection
        .orderBy("seat", Query.Direction.DESCENDING)
        .limit(10)

    fun getNewestEvents() = eventCollection
        .orderBy("createdAt", Query.Direction.DESCENDING)

    fun getEventsByCategory(category: String) = eventCollection
        .whereEqualTo("kategori", category)

    fun searchEvents(query: String) = eventCollection
        .orderBy("namaEvent")
        .startAt(query)
        .endAt(query + "\uf8ff")
}
