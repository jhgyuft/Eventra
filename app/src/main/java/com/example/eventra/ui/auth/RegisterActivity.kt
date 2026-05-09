package com.example.eventra.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.eventra.MainActivity
import com.example.eventra.data.model.User
import com.example.eventra.databinding.ActivityRegisterBinding
import com.example.eventra.utils.addClickScale
import com.example.eventra.utils.fadeIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * RegisterActivity - Refined Premium Version
 * Fokus pada: Micro-interactions, Real-time Validation, dan Cinematic Transitions.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSystems()
        setupEntranceAnimations()
        setupInteractions()
        setupValidationRealtime()
    }

    private fun setupSystems() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        
        // Custom cinematic transition untuk Activity agar navigasi terasa "connected"
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun setupEntranceAnimations() {
        // Efek transisi masuk yang bertahap (Staggered fade-in) agar UI tidak kaku
        binding.headerReg.fadeIn(600)
        binding.registerCard.fadeIn(800, 150)
        binding.tvToLogin.fadeIn(1000, 300)
    }

    private fun setupInteractions() {
        // Micro-interaction: Efek membal (scale) saat tombol ditekan (Tactile feedback)
        binding.btnRegister.addClickScale()
        binding.btnRegister.setOnClickListener {
            if (validateInputs()) registerUser()
        }

        binding.tvToLogin.addClickScale()
        binding.tvToLogin.setOnClickListener {
            // Kembali ke layar sebelumnya dengan alur yang halus
            onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Membersihkan pesan error secara instan saat user mulai mengetik ulang.
     * Membuat alur pengisian form terasa jauh lebih fluid dan tidak menghalangi user.
     */
    private fun setupValidationRealtime() {
        binding.etName.doOnTextChanged { _, _, _, _ -> binding.tilName.error = null }
        binding.etEmail.doOnTextChanged { _, _, _, _ -> binding.tilEmail.error = null }
        binding.etPassword.doOnTextChanged { _, _, _, _ -> binding.tilPassword.error = null }
        binding.etConfirmPassword.doOnTextChanged { _, _, _, _ -> binding.tilConfirmPassword.error = null }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Menggunakan TextInputLayout.error (Material 3) untuk visual yang lebih modern
        if (name.isEmpty()) {
            binding.tilName.error = "Nama lengkap diperlukan"
            isValid = false
        }
        
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email diperlukan"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Format email tidak valid"
            isValid = false
        }
        
        if (password.length < 6) {
            binding.tilPassword.error = "Password minimal 6 karakter"
            isValid = false
        }
        
        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Konfirmasi password tidak cocok"
            isValid = false
        }
        
        return isValid
    }

    private fun registerUser() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        setLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    saveUserToFirestore(User(uid, name, email))
                } else {
                    setLoading(false)
                    showError(task.exception?.message ?: "Pendaftaran gagal")
                }
            }
    }

    private fun saveUserToFirestore(user: User) {
        db.collection("users").document(user.uid).set(user)
            .addOnSuccessListener {
                setLoading(false)
                navigateToHome()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                showError(e.message ?: "Gagal menyimpan data profil")
            }
    }

    private fun setLoading(isLoading: Boolean) {
        // Feedback visual modern: menyembunyikan text tombol saat loading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
        binding.btnRegister.text = if (isLoading) "" else "Create Account"
        
        // Animasi subtle alpha untuk kesan disabled yang halus
        binding.btnRegister.animate()
            .alpha(if (isLoading) 0.6f else 1.0f)
            .setDuration(200)
            .start()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
        // Smooth transition ke layar utama
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
