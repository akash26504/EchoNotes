package com.example.videosummarise.ui.selection.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videosummarise.R
import com.example.videosummarise.databinding.ItemVideoBinding
import com.example.videosummarise.ui.selection.model.Video

class VideoAdapter(
    private val onVideoClick: (Video) -> Unit
) : ListAdapter<Video, VideoAdapter.VideoViewHolder>(VideoDiffCallback()) {

    private var selectedVideo: Video? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video, video == selectedVideo)
    }

    fun setSelectedVideo(video: Video) {
        val previousSelected = selectedVideo
        selectedVideo = video

        // Refresh the previously selected item
        previousSelected?.let { prev ->
            val prevIndex = currentList.indexOfFirst { it.id == prev.id }
            if (prevIndex != -1) {
                notifyItemChanged(prevIndex)
            }
        }

        // Refresh the newly selected item
        val newIndex = currentList.indexOfFirst { it.id == video.id }
        if (newIndex != -1) {
            notifyItemChanged(newIndex)
        }
    }

    inner class VideoViewHolder(
        private val binding: ItemVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(video: Video, isSelected: Boolean) {
            binding.apply {
                videoTitle.text = video.name
                videoDetails.text = "Duration: ${video.getFormattedDuration()} • Size: ${video.getFormattedSize()}"

                // Load video thumbnail using Glide
                Glide.with(videoThumbnail.context)
                    .load(video.uri)
                    .placeholder(R.drawable.video_thumbnail_placeholder)
                    .error(R.drawable.video_thumbnail_placeholder)
                    .centerCrop()
                    .into(videoThumbnail)

                // Update selection state with better visual feedback
                root.isSelected = isSelected

                if (isSelected) {
                    root.strokeColor = ContextCompat.getColor(root.context, R.color.blue_600)
                    root.strokeWidth = 4
                    root.alpha = 1.0f
                } else {
                    root.strokeColor = ContextCompat.getColor(root.context, android.R.color.transparent)
                    root.strokeWidth = 0
                    root.alpha = 0.8f
                }

                root.setOnClickListener {
                    onVideoClick(video)
                }
            }
        }
    }

    private class VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }
    }
}
