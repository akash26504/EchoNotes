package com.example.videosummarise.ui.processing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.videosummarise.databinding.FragmentProcessingBinding
import com.example.videosummarise.service.VideoProcessingService
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProcessingFragment : Fragment() {

    private var _binding: FragmentProcessingBinding? = null
    private val binding get() = _binding!!

    private val args: ProcessingFragmentArgs by navArgs()
    private val videoProcessingService = VideoProcessingService()
    private val gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProcessingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get video URI from arguments
        val videoUri = args.videoUri

        // Start processing simulation
        simulateProcessing()
    }

    private fun simulateProcessing() {
        val videoUri = args.videoUri
        println("ProcessingFragment received video URI: $videoUri")
        binding.processingText.text = "Analyzing video content..."

        lifecycleScope.launch {
            try {
                // Update UI during processing
                updateProcessingStatus("Extracting video metadata...")

                // Process the video
                println("Starting video processing...")
                val videoSummary = videoProcessingService.processVideo(requireContext(), videoUri)
                println("Video processing completed: ${videoSummary.title}")

                updateProcessingStatus("Generating summary...")

                // Convert to JSON for passing to result fragment
                val summaryJson = gson.toJson(videoSummary)
                println("Generated JSON length: ${summaryJson.length}")

                updateProcessingStatus("Processing complete!")

                // Navigate to result fragment with the summary
                val action = ProcessingFragmentDirections
                    .actionProcessingFragmentToResultFragment(summaryId = summaryJson)
                findNavController().navigate(action)

            } catch (e: Exception) {
                // Handle error
                binding.processingText.text = "Error processing video: ${e.message}"

                // Navigate back after showing error
                binding.root.postDelayed({
                    findNavController().popBackStack()
                }, 2000)
            }
        }
    }

    private fun updateProcessingStatus(status: String) {
        binding.processingText.text = status
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}