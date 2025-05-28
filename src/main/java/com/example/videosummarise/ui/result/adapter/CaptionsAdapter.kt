package com.example.videosummarise.ui.result.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.videosummarise.data.model.Caption
import com.example.videosummarise.databinding.ItemCaptionBinding

class CaptionsAdapter(
    private val captions: List<Caption>
) : RecyclerView.Adapter<CaptionsAdapter.CaptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaptionViewHolder {
        val binding = ItemCaptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CaptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CaptionViewHolder, position: Int) {
        holder.bind(captions[position])
    }

    override fun getItemCount(): Int = captions.size

    class CaptionViewHolder(
        private val binding: ItemCaptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(caption: Caption) {
            binding.captionTime.text = caption.getFormattedTimeRange()
            binding.captionText.text = caption.text
        }
    }
}
