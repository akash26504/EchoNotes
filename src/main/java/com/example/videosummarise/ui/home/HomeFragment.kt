package com.example.videosummarise.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.videosummarise.R
import com.example.videosummarise.databinding.FragmentHomeBinding
import com.example.videosummarise.utils.ThemeManager
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        startEntranceAnimations()
    }

    private fun setupClickListeners() {
        // Set up click listeners for navigation with animations
        binding.selectVideoButton.setOnClickListener {
            animateButtonClick(it) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToVideoSelectionFragment()
                )
            }
        }

        binding.mySummariesButton.setOnClickListener {
            animateButtonClick(it) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToSavedSummariesFragment()
                )
            }
        }

        // Theme toggle button
        binding.themeToggleButton.setOnClickListener {
            if (isAdded && !isDetached) {
                toggleTheme()
            }
        }

        // Initialize theme button icon
        updateThemeButtonIcon()
    }

    private fun updateThemeButtonIcon() {
        if (!isAdded || isDetached) return

        try {
            val currentTheme = ThemeManager.getCurrentTheme(requireContext())
            val isDarkMode = currentTheme == ThemeManager.THEME_DARK

            // Set icon based on current theme (show opposite of current theme)
            val iconRes = if (isDarkMode) {
                R.drawable.ic_light_mode // Show sun icon when in dark mode
            } else {
                R.drawable.ic_dark_mode // Show moon icon when in light mode
            }

            binding.themeToggleButton.setImageResource(iconRes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toggleTheme() {
        try {
            val currentTheme = ThemeManager.getCurrentTheme(requireContext())
            val newTheme = if (currentTheme == ThemeManager.THEME_DARK) {
                ThemeManager.THEME_LIGHT
            } else {
                ThemeManager.THEME_DARK
            }

            val themeName = if (newTheme == ThemeManager.THEME_DARK) "Dark" else "Light"

            // Animate the theme toggle button first
            animateThemeToggle()

            // Show snackbar
            Snackbar.make(binding.root, "Theme changed to $themeName", Snackbar.LENGTH_SHORT).show()

            // Save and apply theme change
            ThemeManager.setTheme(requireContext(), newTheme)

        } catch (e: Exception) {
            // Handle any errors gracefully
            e.printStackTrace()
            Snackbar.make(binding.root, "Error changing theme", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun animateThemeToggle() {
        val rotation = ObjectAnimator.ofFloat(binding.themeToggleButton, "rotation", 0f, 360f)
        val scaleX = ObjectAnimator.ofFloat(binding.themeToggleButton, "scaleX", 1f, 0.8f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.themeToggleButton, "scaleY", 1f, 0.8f, 1f)

        val animatorSet = AnimatorSet().apply {
            playTogether(rotation, scaleX, scaleY)
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
        }
        animatorSet.start()
    }

    private fun startEntranceAnimations() {
        // Initial state - hide all views
        binding.themeToggleButton.alpha = 0f
        binding.themeToggleButton.scaleX = 0f
        binding.themeToggleButton.scaleY = 0f

        binding.headerCard.alpha = 0f
        binding.headerCard.translationY = -50f

        binding.actionsContainer.alpha = 0f
        binding.actionsContainer.translationY = 50f

        binding.featuresCard.alpha = 0f
        binding.featuresCard.translationY = 30f

        // Theme button animation
        val themeButtonAlpha = ObjectAnimator.ofFloat(binding.themeToggleButton, "alpha", 0f, 1f)
        val themeButtonScaleX = ObjectAnimator.ofFloat(binding.themeToggleButton, "scaleX", 0f, 1f)
        val themeButtonScaleY = ObjectAnimator.ofFloat(binding.themeToggleButton, "scaleY", 0f, 1f)

        val themeButtonAnimatorSet = AnimatorSet().apply {
            playTogether(themeButtonAlpha, themeButtonScaleX, themeButtonScaleY)
            duration = 400
            interpolator = OvershootInterpolator(1.2f)
            startDelay = 50
        }

        // Header animation
        val headerAlpha = ObjectAnimator.ofFloat(binding.headerCard, "alpha", 0f, 1f)
        val headerTranslation = ObjectAnimator.ofFloat(binding.headerCard, "translationY", -50f, 0f)

        val headerAnimatorSet = AnimatorSet().apply {
            playTogether(headerAlpha, headerTranslation)
            duration = 600
            interpolator = OvershootInterpolator(0.8f)
            startDelay = 100
        }

        // Actions animation
        val actionsAlpha = ObjectAnimator.ofFloat(binding.actionsContainer, "alpha", 0f, 1f)
        val actionsTranslation = ObjectAnimator.ofFloat(binding.actionsContainer, "translationY", 50f, 0f)

        val actionsAnimatorSet = AnimatorSet().apply {
            playTogether(actionsAlpha, actionsTranslation)
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 300
        }

        // Features animation
        val featuresAlpha = ObjectAnimator.ofFloat(binding.featuresCard, "alpha", 0f, 1f)
        val featuresTranslation = ObjectAnimator.ofFloat(binding.featuresCard, "translationY", 30f, 0f)

        val featuresAnimatorSet = AnimatorSet().apply {
            playTogether(featuresAlpha, featuresTranslation)
            duration = 400
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = 500
        }

        // Start all animations
        themeButtonAnimatorSet.start()
        headerAnimatorSet.start()
        actionsAnimatorSet.start()
        featuresAnimatorSet.start()
    }

    private fun animateButtonClick(view: View, action: () -> Unit) {
        val scaleDown = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f)
            )
            duration = 100
        }

        val scaleUp = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f)
            )
            duration = 100
        }

        scaleDown.start()
        scaleDown.doOnEnd {
            scaleUp.start()
            action()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}