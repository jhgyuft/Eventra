package com.example.eventra.utils

import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.view.ViewCompat

/**
 * Premium Scale Interaction
 * Memberikan efek mengecil saat ditekan dan kembali saat dilepas (Spring-like effect)
 */
fun View.addClickScale(scale: Float = 0.96f) {
    this.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.animate()
                    .scaleX(scale)
                    .scaleY(scale)
                    .setDuration(100)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                v.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .setInterpolator(OvershootInterpolator(2f))
                    .start()
            }
        }
        false
    }
}

/**
 * Smooth Fade In Animation
 */
fun View.fadeIn(duration: Long = 400, delay: Long = 0) {
    this.alpha = 0f
    this.visibility = View.VISIBLE
    this.animate()
        .alpha(1f)
        .setDuration(duration)
        .setStartDelay(delay)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .start()
}

/**
 * Subtle Slide Up Animation for lists or cards
 */
fun View.slideUp(duration: Long = 500, delay: Long = 0) {
    this.translationY = 100f
    this.alpha = 0f
    this.visibility = View.VISIBLE
    this.animate()
        .translationY(0f)
        .alpha(1f)
        .setDuration(duration)
        .setStartDelay(delay)
        .setInterpolator(OvershootInterpolator(0.8f))
        .start()
}
