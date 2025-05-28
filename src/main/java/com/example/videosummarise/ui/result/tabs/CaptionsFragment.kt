package com.example.videosummarise.ui.result.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videosummarise.data.model.Caption
import com.example.videosummarise.databinding.FragmentCaptionsTabBinding
import com.example.videosummarise.ui.result.adapter.CaptionsAdapter

class CaptionsFragment : Fragment() {

    private var _binding: FragmentCaptionsTabBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var captionsAdapter: CaptionsAdapter

    companion object {
        private const val ARG_CAPTIONS = "captions"

        fun newInstance(captions: List<Caption>): CaptionsFragment {
            val fragment = CaptionsFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_CAPTIONS, ArrayList(captions))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaptionsTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val captions = arguments?.getParcelableArrayList<Caption>(ARG_CAPTIONS) ?: emptyList()
        
        setupRecyclerView(captions)
    }
    
    private fun setupRecyclerView(captions: List<Caption>) {
        captionsAdapter = CaptionsAdapter(captions)
        binding.captionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = captionsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
