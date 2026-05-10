package com.example.eventra.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.eventra.R
import com.example.eventra.databinding.FragmentProfileBinding
import com.example.eventra.ui.admin.AddEventActivity
import com.example.eventra.ui.auth.LoginActivity
import com.example.eventra.utils.addClickScale
import com.example.eventra.utils.fadeIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupUI()
        setupMenuActions()
        loadUserData()
    }

    override fun onResume() {
        super.onResume()
        if (_binding == null) return
        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finishAffinity()
            return
        }
        loadUserData()
    }

    private fun setupUI() {
        binding.ivProfile.fadeIn(600)
        binding.statsCard.fadeIn(800, 200)
        binding.btnLogout.addClickScale()
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finishAffinity()
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser ?: return
        binding.tvProfileName.text = user.displayName ?: "Explorer"
        binding.tvProfileEmail.text = user.email.orEmpty()

        db.collection("users").document(user.uid).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                binding.tvProfileName.text = doc.getString("nama") ?: user.displayName ?: "Explorer"
                doc.getString("profileImage")?.takeIf { it.isNotEmpty() }?.let { url ->
                    Glide.with(this).load(url).placeholder(R.drawable.ic_profile).into(binding.ivProfile)
                }
                val role = doc.getString("role")
                binding.menuAdminPanel.root.visibility =
                    if (role == "admin") View.VISIBLE else View.GONE
            } else {
                binding.menuAdminPanel.root.visibility = View.GONE
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Gagal memuat profil: ${e.message}", Toast.LENGTH_SHORT).show()
            binding.menuAdminPanel.root.visibility = View.GONE
        }

        db.collection("transactions").whereEqualTo("uidUser", user.uid).get().addOnSuccessListener {
            binding.tvTicketsCount.text = it.size().toString()
        }
    }

    private fun setupMenuActions() {
        binding.menuEditProfile.menuTitle.text = "Edit Profile"
        binding.menuAdminPanel.menuTitle.text = "Admin Panel"
        binding.menuPurchaseHistory.menuTitle.text = "My Tickets"
        binding.menuNotificationSettings.menuTitle.text = "Notifications"
        binding.menuPrivacyPolicy.menuTitle.text = "Privacy Policy"

        binding.menuEditProfile.root.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
        binding.menuAdminPanel.root.setOnClickListener {
            startActivity(Intent(requireContext(), AddEventActivity::class.java))
        }
        binding.menuPurchaseHistory.root.setOnClickListener {
            Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
        }
        binding.menuNotificationSettings.root.setOnClickListener {
            Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
        }
        binding.menuPrivacyPolicy.root.setOnClickListener {
            Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
