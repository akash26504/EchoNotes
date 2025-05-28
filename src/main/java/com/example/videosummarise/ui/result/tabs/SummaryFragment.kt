package com.example.videosummarise.ui.result.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.videosummarise.databinding.FragmentSummaryTabBinding

class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryTabBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_SUMMARY = "summary"

        fun newInstance(summary: String): SummaryFragment {
            val fragment = SummaryFragment()
            val args = Bundle()
            args.putString(ARG_SUMMARY, summary)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val summary = arguments?.getString(ARG_SUMMARY) ?: ""
        binding.summaryText.text = summary
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
