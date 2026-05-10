package com.example.eventra.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventra.data.model.User
import com.example.eventra.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var imageUri: Uri? = null
    private var existingImageUrl: String = ""

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.ivEditPhoto.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Sesi tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvEditEmail.text = auth.currentUser?.email.orEmpty()

        binding.btnChangePhoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSaveProfile.setOnClickListener {
            saveProfile(uid)
        }

        loadUser(uid)
    }

    private fun loadUser(uid: String) {
        binding.progressEditProfile.visibility = View.VISIBLE
        db.collection("users").document(uid).get()
            .addOnCompleteListener { task ->
                binding.progressEditProfile.visibility = View.GONE
                if (!task.isSuccessful) {
                    Toast.makeText(this, "Gagal memuat profil", Toast.LENGTH_SHORT).show()
                    binding.etEditName.setText(auth.currentUser?.displayName.orEmpty())
                    return@addOnCompleteListener
                }
                val user = task.result?.toObject(User::class.java)
                if (user != null) {
                    binding.etEditName.setText(user.nama)
                    existingImageUrl = user.profileImage
                    if (user.profileImage.isNotEmpty()) {
                        Glide.with(this).load(user.profileImage).into(binding.ivEditPhoto)
                    }
                } else {
                    binding.etEditName.setText(auth.currentUser?.displayName.orEmpty())
                }
            }
    }

    private fun saveProfile(uid: String) {
        val name = binding.etEditName.text.toString().trim()
        if (name.isEmpty()) {
            binding.etEditName.error = "Nama wajib diisi"
            return
        }

        val email = auth.currentUser?.email.orEmpty()

        binding.btnSaveProfile.isEnabled = false
        binding.progressEditProfile.visibility = View.VISIBLE

        val uri = imageUri
        if (uri != null) {
            val ref = storage.reference.child("profile_images/$uid.jpg")
            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { download ->
                        persistUser(uid, name, email, download.toString())
                    }
                }
                .addOnFailureListener { e ->
                    binding.btnSaveProfile.isEnabled = true
                    binding.progressEditProfile.visibility = View.GONE
                    Toast.makeText(this, "Upload gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            persistUser(uid, name, email, existingImageUrl)
        }
    }

    private fun persistUser(uid: String, name: String, email: String, photoUrl: String) {
        val data = hashMapOf<String, Any>(
            "uid" to uid,
            "nama" to name,
            "email" to email,
            "profileImage" to photoUrl
        )

        db.collection("users").document(uid).set(data, SetOptions.merge())
            .addOnSuccessListener {
                val firebaseUser = auth.currentUser
                if (firebaseUser == null) {
                    binding.btnSaveProfile.isEnabled = true
                    binding.progressEditProfile.visibility = View.GONE
                    Toast.makeText(this, "Profil disimpan", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }
                val requestBuilder = UserProfileChangeRequest.Builder().setDisplayName(name)
                if (photoUrl.isNotEmpty()) {
                    requestBuilder.setPhotoUri(Uri.parse(photoUrl))
                }
                firebaseUser.updateProfile(requestBuilder.build())
                    .addOnCompleteListener { task ->
                        binding.btnSaveProfile.isEnabled = true
                        binding.progressEditProfile.visibility = View.GONE
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Profil disimpan", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Data tersimpan; sinkron akun gagal: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        finish()
                    }
            }
            .addOnFailureListener { e ->
                binding.btnSaveProfile.isEnabled = true
                binding.progressEditProfile.visibility = View.GONE
                Toast.makeText(this, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
