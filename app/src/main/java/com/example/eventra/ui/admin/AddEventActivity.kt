package com.example.eventra.ui.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.eventra.data.model.Event
import com.example.eventra.databinding.ActivityAddEventBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.UUID

class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEventBinding
    private var imageUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.ivEventPosterPreview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardAddPoster.setOnClickListener {
            getImage.launch("image/*")
        }

        binding.etAddDate.setOnClickListener {
            showDatePicker()
        }

        binding.etAddTime.setOnClickListener {
            showTimePicker()
        }

        binding.btnSaveEvent.setOnClickListener {
            uploadImageAndSaveEvent()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                binding.etAddDate.setText("$dayOfMonth/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                binding.etAddTime.setText(String.format("%02d:%02d", hourOfDay, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun uploadImageAndSaveEvent() {
        val name = binding.etAddEventName.text.toString().trim()
        val location = binding.etAddLocation.text.toString().trim()
        val date = binding.etAddDate.text.toString().trim()
        val time = binding.etAddTime.text.toString().trim()
        val price = binding.etAddPrice.text.toString().trim()
        val description = binding.etAddDescription.text.toString().trim()

        if (name.isEmpty() || location.isEmpty() || date.isEmpty() || 
            time.isEmpty() || price.isEmpty() || description.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSaveEvent.isEnabled = false
        binding.btnSaveEvent.text = "Uploading..."

        val fileName = UUID.randomUUID().toString()
        val ref = storage.reference.child("event_posters/$fileName")

        ref.putFile(imageUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    saveEventToFirestore(uri.toString())
                }
            }
            .addOnFailureListener {
                binding.btnSaveEvent.isEnabled = true
                binding.btnSaveEvent.text = "Save Event"
                Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveEventToFirestore(imageUrl: String) {
        val eventId = db.collection("events").document().id
        val event = Event(
            eventId = eventId,
            namaEvent = binding.etAddEventName.text.toString(),
            lokasi = binding.etAddLocation.text.toString(),
            tanggal = binding.etAddDate.text.toString(),
            jam = binding.etAddTime.text.toString(),
            harga = binding.etAddPrice.text.toString().toLong(),
            deskripsi = binding.etAddDescription.text.toString(),
            kategori = "Concert", // Default for now
            poster = imageUrl,
            organizer = "Admin",
            seat = 100,
            createdAt = System.currentTimeMillis()
        )

        db.collection("events").document(eventId).set(event)
            .addOnSuccessListener {
                Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                binding.btnSaveEvent.isEnabled = true
                binding.btnSaveEvent.text = "Save Event"
                Toast.makeText(this, "Failed to save event: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
