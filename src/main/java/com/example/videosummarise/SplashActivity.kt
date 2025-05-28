package com.example.videosummarise

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.example.videosummarise.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set splash theme
        setTheme(R.style.Theme_VideoSummarise_Splash)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start animations
        startSplashAnimations()
    }

    private fun startSplashAnimations() {
        // Initial state - hide all views
        binding.splashLogo.alpha = 0f
        binding.splashLogo.scaleX = 0.3f
        binding.splashLogo.scaleY = 0.3f

        binding.appName.alpha = 0f
        binding.appName.translationY = 50f

        binding.appTagline.alpha = 0f
        binding.appTagline.translationY = 30f

        binding.loadingIndicator.alpha = 0f

        // Logo animation
        val logoScaleX = ObjectAnimator.ofFloat(binding.splashLogo, "scaleX", 0.3f, 1.2f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(binding.splashLogo, "scaleY", 0.3f, 1.2f, 1f)
        val logoAlpha = ObjectAnimator.ofFloat(binding.splashLogo, "alpha", 0f, 1f)

        val logoAnimatorSet = AnimatorSet().apply {
            playTogether(logoScaleX, logoScaleY, logoAlpha)
            duration = 800
            interpolator = OvershootInterpolator(1.2f)
        }

        // App name animation
        val nameAlpha = ObjectAnimator.ofFloat(binding.appName, "alpha", 0f, 1f)
        val nameTranslation = ObjectAnimator.ofFloat(binding.appName, "translationY", 50f, 0f)

        val nameAnimatorSet = AnimatorSet().apply {
            playTogether(nameAlpha, nameTranslation)
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 400
        }

        // Tagline animation
        val taglineAlpha = ObjectAnimator.ofFloat(binding.appTagline, "alpha", 0f, 1f)
        val taglineTranslation = ObjectAnimator.ofFloat(binding.appTagline, "translationY", 30f, 0f)

        val taglineAnimatorSet = AnimatorSet().apply {
            playTogether(taglineAlpha, taglineTranslation)
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 700
        }

        // Loading indicator animation
        val loadingAlpha = ObjectAnimator.ofFloat(binding.loadingIndicator, "alpha", 0f, 1f)
        loadingAlpha.apply {
            duration = 400
            startDelay = 1000
        }

        // Start all animations
        logoAnimatorSet.start()
        nameAnimatorSet.start()
        taglineAnimatorSet.start()
        loadingAlpha.start()

        // Navigate to main activity after animations
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMainActivity()
        }, 2500)
    }

    private fun navigateToMainActivity() {
        // Fade out animation before navigation
        val fadeOut = ObjectAnimator.ofFloat(binding.root, "alpha", 1f, 0f)
        fadeOut.apply {
            duration = 300
            doOnEnd {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }
        fadeOut.start()
    }
}
