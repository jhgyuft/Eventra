package com.example.eventra

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class EventraApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // PAKSA seluruh aplikasi menggunakan Mode Malam (Dark Mode)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}
