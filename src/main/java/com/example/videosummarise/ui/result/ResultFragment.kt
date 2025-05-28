package com.example.videosummarise.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.videosummarise.R
import com.example.videosummarise.data.model.VideoSummary
import com.example.videosummarise.databinding.FragmentResultBinding
import com.example.videosummarise.ui.result.adapter.ResultPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val args: ResultFragmentArgs by navArgs()
    private val gson = Gson()
    private lateinit var videoSummary: VideoSummary

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Parse the summary from JSON
        val summaryJson = args.summaryId // This is actually JSON now
        try {
            println("Received JSON: $summaryJson")
            videoSummary = gson.fromJson(summaryJson, VideoSummary::class.java)
            println("Parsed summary: ${videoSummary.title}")
            setupUI()
        } catch (e: Exception) {
            // Handle error - show fallback content
            println("Error parsing JSON: ${e.message}")
            e.printStackTrace()
            binding.resultTitle.text = "Error loading summary: ${e.message}"
        }
    }

    private fun setupUI() {
        // Set result title
        binding.resultTitle.text = "Summary: ${videoSummary.title}"

        // Setup ViewPager with tabs
        val adapter = ResultPagerAdapter(requireActivity(), videoSummary)
        binding.viewPager.adapter = adapter

        // Setup TabLayout with ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Summary"
                1 -> "Key Points"
                2 -> "Captions"
                else -> "Tab ${position + 1}"
            }
        }.attach()

        // Setup action buttons
        setupActionButtons()
    }

    private fun setupActionButtons() {
        binding.saveButton.setOnClickListener {
            // TODO: Implement save functionality
            // For now, just show a message
            android.widget.Toast.makeText(requireContext(), "Summary saved!", android.widget.Toast.LENGTH_SHORT).show()
        }

        binding.shareButton.setOnClickListener {
            shareContent()
        }
    }

    private fun shareContent() {
        val shareText = "Video Summary: ${videoSummary.title}\n\n${videoSummary.summary}"

        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
        }

        startActivity(android.content.Intent.createChooser(shareIntent, "Share Summary"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}