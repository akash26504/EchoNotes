package com.example.videosummarise.ui.result.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videosummarise.databinding.FragmentKeyPointsTabBinding
import com.example.videosummarise.ui.result.adapter.KeyPointsAdapter

class KeyPointsFragment : Fragment() {

    private var _binding: FragmentKeyPointsTabBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var keyPointsAdapter: KeyPointsAdapter

    companion object {
        private const val ARG_KEY_POINTS = "key_points"

        fun newInstance(keyPoints: List<String>): KeyPointsFragment {
            val fragment = KeyPointsFragment()
            val args = Bundle()
            args.putStringArrayList(ARG_KEY_POINTS, ArrayList(keyPoints))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeyPointsTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val keyPoints = arguments?.getStringArrayList(ARG_KEY_POINTS) ?: emptyList()
        
        setupRecyclerView(keyPoints)
    }
    
    private fun setupRecyclerView(keyPoints: List<String>) {
        keyPointsAdapter = KeyPointsAdapter(keyPoints)
        binding.keyPointsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = keyPointsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
