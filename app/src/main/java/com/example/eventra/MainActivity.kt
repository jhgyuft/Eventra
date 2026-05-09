package com.example.eventra

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.eventra.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // Sempurnakan UX: Deteksi keyboard untuk menyembunyikan Bottom Nav
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.root.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - r.bottom

            // Jika keyboard muncul (tinggi > 15% layar), sembunyikan navigasi dengan animasi halus
            if (keypadHeight > screenHeight * 0.15) {
                if (binding.bottomNavCard.visibility == View.VISIBLE) {
                    binding.bottomNavCard.animate().translationY(binding.bottomNavCard.height.toFloat() + 100f).setDuration(200).withEndAction {
                        binding.bottomNavCard.visibility = View.GONE
                    }.start()
                }
            } else {
                if (binding.bottomNavCard.visibility == View.GONE) {
                    binding.bottomNavCard.visibility = View.VISIBLE
                    binding.bottomNavCard.animate().translationY(0f).setDuration(200).start()
                }
            }
        }
    }
}
