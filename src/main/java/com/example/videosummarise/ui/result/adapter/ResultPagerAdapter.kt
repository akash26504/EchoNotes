package com.example.videosummarise.ui.result.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.videosummarise.data.model.VideoSummary
import com.example.videosummarise.ui.result.tabs.CaptionsFragment
import com.example.videosummarise.ui.result.tabs.KeyPointsFragment
import com.example.videosummarise.ui.result.tabs.SummaryFragment

class ResultPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val videoSummary: VideoSummary
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SummaryFragment.newInstance(videoSummary.summary)
            1 -> KeyPointsFragment.newInstance(videoSummary.keyPoints)
            2 -> CaptionsFragment.newInstance(videoSummary.captions)
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
