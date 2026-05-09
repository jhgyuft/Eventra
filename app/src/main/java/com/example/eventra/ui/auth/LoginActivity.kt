package com.example.eventra.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.eventra.MainActivity
import com.example.eventra.databinding.ActivityLoginBinding
import com.example.eventra.utils.addClickScale
import com.example.eventra.utils.fadeIn
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        
        setupUI()
        setupInteractions()
    }

    private fun setupUI() {
        // Soft entrance animation
        binding.headerSection.fadeIn(600)
        binding.loginCard.fadeIn(800, 200)
    }

    private fun setupInteractions() {
        // Premium Button Interaction
        binding.btnLogin.addClickScale()
        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.tvToRegister.addClickScale()
        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Real-time Validation Polish
        binding.etEmail.doOnTextChanged { _, _, _, _ -> 
            binding.tilEmail.error = null 
        }
        binding.etPassword.doOnTextChanged { _, _, _, _ -> 
            binding.tilPassword.error = null 
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (!validate(email, password)) return

        setLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    navigateToHome()
                } else {
                    showError(task.exception?.message ?: "Login Gagal")
                }
            }
    }

    private fun validate(email: String, password: String): Boolean {
        var isValid = true
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Email tidak valid"
            isValid = false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password tidak boleh kosong"
            isValid = false
        }
        return isValid
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.btnLogin.text = if (isLoading) "" else "Sign In"
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
        // Smooth transition to Home
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
