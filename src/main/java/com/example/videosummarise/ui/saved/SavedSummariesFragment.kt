package com.example.videosummarise.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.videosummarise.databinding.FragmentSavedSummariesBinding

class SavedSummariesFragment : Fragment() {

    private var _binding: FragmentSavedSummariesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedSummariesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup RecyclerView and adapter
        // This would typically be populated from a ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}